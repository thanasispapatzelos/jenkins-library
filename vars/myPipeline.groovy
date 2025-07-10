import org.example.pipeline.*

def myImage
def agentImage

def call(Map config = [:]) {
    pipeline {
            agent none 

            environment { 
            //KUBECONFIG = "${WORKSPACE}/kubeconfig"  // kubectl will use this path
            DOCKER_CREDS_ID = 'nexus-creds' 
            NEXUS_REGISTRY = 'nexus.docker:30050'
            NEXUS_HELM_REGISTRY = 'http://nexus-nexus-repository-manager:8081/repository/helm-repo/'
            //IMAGE_TAG = 'myversion'
            }

            stages {

                stage('Init') {
                    agent {
                        kubernetes {
                            yaml libraryResource('podTemplates/initial-agent.yaml')
                        }
                    }  
                    
                    steps {
                        script {
                            env.ENV_NAME = config.envName ?: 'dev'
                        }
                    }
                }

                stage('Built-docker-agent') {
                    agent {
                        kubernetes {
                            yaml libraryResource('podTemplates/initial-agent.yaml')
                        }
                    }  
                    steps {
                        script {
                            def buildStage = new org.example.pipeline.BuildAgentImage(this, env)
                            agentImage = buildStage.execute()
                        }
                    }
                }
                


                stage('Checkout') {
                    agent {
                        kubernetes {
                            yaml libraryResource('podTemplates/mvn-docker-helm.yaml')
                        }
                    }  
                    steps {
                        script{
                            def checkoutStage = new Checkout(this)
                            checkoutStage.execute()
                        }

                    }
                }


                stage('Build-jar') {
                    agent {
                        kubernetes {
                            yaml libraryResource('podTemplates/mvn-docker-helm.yaml')
                        }
                    } 
                    steps {
                        script {
                            def buildJarStage = new BuildJar(this)
                            buildJarStage.execute()
                        }
                    }
                }

                stage('Build-image') {
                    agent {
                        kubernetes {
                            yaml libraryResource('podTemplates/mvn-docker-helm.yaml')
                        }
                    } 
                    steps {
                        script {
                        def buildImageStage = new BuildImage(this)
                        myImage = buildImageStage.execute(env.NEXUS_REGISTRY, env.BUILD_NUMBER)
                        }
                    }
                }
                

                stage('Package-Push-chart') {
                    agent {
                        kubernetes {
                            yaml libraryResource('podTemplates/mvn-docker-helm.yaml')
                        }
                    } 
                    steps {
                        script {
                            def packagePushChartStage = new PackagePushChart(this,env)
                            packagePushChartStage.execute()
                        }
                    }
                }
                

                stage('Push-docker-image') {
                    agent {
                        kubernetes {
                            yaml libraryResource('podTemplates/mvn-docker-helm.yaml')
                        }
                    } 
                    steps {
                        script {
                            def pushDockerStage = new PushDockerImage(this, myImage, env)
                            pushDockerStage.execute()
                        }
                    }
                }

                stage('Add-install-nexus-chart') {
                    agent {
                        kubernetes {
                            yaml libraryResource('podTemplates/mvn-docker-helm.yaml')
                        }
                    } 
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