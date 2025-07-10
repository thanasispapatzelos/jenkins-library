package org.example.pipeline

class PushAgentImage implements Serializable {
    def steps
    def env
    def agentImage

    PushAgentImage(steps, agentImage, env) {
        this.steps = steps
        this.env = env
        this.agentImage = agentImage
    }

    def execute() {
        steps.container('docker-initial') {
            steps.script {
                steps.script {
                    steps.docker.withRegistry("http://${env.NEXUS_REGISTRY}", env.DOCKER_CREDS_ID) {
                    agentImage.push('myversion')
                    }
                }
            }
        }
    }
}
