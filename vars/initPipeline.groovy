import org.example.pipeline.*


def agentImage

def call(Map config = [:]) {
    pipeline {
            agent {
                kubernetes {
                    yaml libraryResource('podTemplates/initial-agent.yaml')
                }
            }   

            environment { 
            //KUBECONFIG = "${WORKSPACE}/kubeconfig"  // kubectl will use this path
            DOCKER_CREDS_ID = 'nexus-creds' 
            NEXUS_REGISTRY = 'nexus.docker:30050'
            //NEXUS_REGISTRY = 'nexus-nexus-repository-manager:5000'
            NEXUS_HELM_REGISTRY = 'http://nexus-nexus-repository-manager:8081/repository/helm-repo/'
            //IMAGE_TAG = 'myversion'
            }

            stages {

                
                stage('Built-docker-agent') {
                    steps {
                      script {
                            def buildStage = new org.example.pipeline.BuildAgentImage(this, env)
                            agentImage = buildStage.execute()
                        }
                    }
                }

                stage('Push-docker-agent') {
                    steps {
                      script {
                            def pushStage = new org.example.pipeline.PushAgentImage(this, env)
                            agentImage = pushStage.execute()
                        }
                    }
                }



            }
    }
}