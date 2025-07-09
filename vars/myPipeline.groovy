import org.example.pipeline.*


def call(Map config = [:]) {
    pipeline {
            agent {
                kubernetes {
                    yaml libraryResource('podTemplates/mvn-docker-helm.yaml')
                }
            }   

            environment { 
            KUBECONFIG = "${WORKSPACE}/kubeconfig"  // kubectl will use this path
            DOCKER_CREDS_ID = 'nexus-creds' 
            NEXUS_REGISTRY = 'nexus.docker:30050'
            IMAGE_TAG = 'myversion'
            }

            stages {

                   stage('Init') {
                        steps {
                            script {
                                env.ENV_NAME = config.envName ?: 'dev'
                            }
                        }
                    }

                stage('Checkout') {
                    steps {
                        script{
                            def checkoutStage = new Checkout(this)
                            checkoutStage.execute()
                        }

                    }
                }


                stage('Build-jar') {
                    steps {
                        container('mvn') {
                            script {
                                def buildJarStage = new BuildJar(this)
                                buildJarStage.execute()
                            }
                        }
                    }
                }

                stage('Build-image') {
                    steps {
                        container('docker-cli') {
                            script {
                            def buildImageStage = new BuildImage(this)
                            def myImage = buildImageStage.execute(env.NEXUS_REGISTRY, env.IMAGE_TAG)
                            // If you want to keep using myImage later, you can save it in env or elsewhere
                            env.MY_IMAGE = myImage.toString()  // or however you want to handle it
                            }
                        }
                    }
                }
                

                stage('Package-Push-chart') {
                    steps {
                        container('helm') {
                            helmWithKubeconfig {
                                sh '''
                                    helm package ./my-chart
                                    curl -u $USERNAME:$PASSWORD --upload-file my-chart-0.1.0.tgz http://nexus-nexus-repository-manager:8081/repository/helm-repo/
                                    
                                '''
                            }  
                        }
                    }
                }
                

                stage('Push-docker-image') {
                    steps {
                        container('docker-cli') {
                            script {
                                    docker.withRegistry("http://${NEXUS_REGISTRY}", "${DOCKER_CREDS_ID}") {
                                    myImage.push("${IMAGE_TAG}") 

                                    }   
                            }
                        }
                    }
                }

                stage('Add-install-nexus-chart') {
                    steps {
                        container('helm') {
                            helmWithKubeconfig {
                                echo "Deploying to ${env.ENV_NAME}"
                                sh 'helm repo add --username $USERNAME --password $PASSWORD helm-nexus http://nexus-nexus-repository-manager:8081/repository/helm-repo/'
                                sh 'helm repo update'
                                sh 'helm repo list'
                                sh 'helm install helm-nexus helm-nexus/my-chart --version 0.1.0 -n jenkins --set image.repository=nexus.docker:30050/quarkus  --set image.tag=myversion'    

                            }
                        }
                    }
                }   
                


            }
    }
}