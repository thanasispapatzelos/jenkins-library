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
            //NEXUS_REGISTRY = 'nexus.docker:30050'
            NEXUS_REGISTRY = 'nexus-nexus-repository-manager:5000'
            NEXUS_HELM_REGISTRY = 'http://nexus-nexus-repository-manager:8081/repository/helm-repo/'
            //IMAGE_TAG = 'myversion'
            GIT_CREDENTIALS_ID = 'github-credentials-id'  // Jenkins credential ID for GitHub
            REPO_URL = 'https://github.com/PapatzelosThanashs/quarkus.git'
            BRANCH = 'main'

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
                
                stage('Push-docker-image') {
                    steps {
                        script {
                            def pushDockerStage = new PushDockerImage(this, myImage, env)
                            pushDockerStage.execute()
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
                

               

                //stage('Add-install-nexus-chart') {
                //    steps {
               //         script {
               //             def installer = new InstallHelmChart(this, env)
               //             installer.execute()
                //        }
                 //   }
                //}   
                
            stage('editApplicationYml') {
                    steps {
                        script {
                            def editApplicationYml = new EditApplicationYml(this,env)
                            editApplicationYml.execute()
                        }
                    }
                }

            stage('Deploy-commitApplicationYml') {
                    steps {
                        script {
                            def commitApplicationYml = new CommitApplicationYml(this,env)
                            commitApplicationYml.execute()
                        }
                    }
            }    

            }
    }
}