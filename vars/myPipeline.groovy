import org.example.pipeline.*

def myImage

def call(Map config = [:]) {
    pipeline {
            agent {
                kubernetes {
                    yaml libraryResource('podTemplates/mvn-docker-helm.yaml')
                }
            }   

            environment { 
            //KUBECONFIG = "${WORKSPACE}/kubeconfig"  // kubectl will use this path
            DOCKER_CREDS_ID = 'nexus-creds' 
            NEXUS_REGISTRY = 'nexus.docker:30050'
            NEXUS_HELM_REGISTRY = 'http://nexus-nexus-repository-manager:8081/repository/helm-repo/'
            //IMAGE_TAG = 'myversion'
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
                        script {
                            def buildJarStage = new BuildJar(this)
                            buildJarStage.execute()
                        }
                    }
                }

                stage('Build-image') {
                    steps {
                        script {
                        def buildImageStage = new BuildImage(this)
                        myImage = buildImageStage.execute(env.NEXUS_REGISTRY, env.BUILD_NUMBER)
                        }
                    }
                }
                

                stage('Package-Push-chart') {
                    steps {
                        script {
                            def packagePushChartStage = new PackagePushChart(this,env)
                            packagePushChartStage.execute()
                        }
                    }
                }
                

                stage('Push-docker-image') {
                    steps {
                        script {
                            def pushDockerStage = new PushDockerImage(this, myImage, env)
                            pushDockerStage.execute()
                        }
                    }
                }

                stage('Add-install-nexus-chart') {
                    steps {
                        script {
                            def installer = new InstallHelmChart(this, env)
                            installer.execute()
                        }
                    }
                }   
                


            }
    }
}