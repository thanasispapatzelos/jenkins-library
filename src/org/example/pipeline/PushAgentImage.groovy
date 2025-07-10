package org.example.pipeline

class BuildAgentImage implements Serializable {
    def steps
    def env

    BuildAgentImage(steps, env) {
        this.steps = steps
        this.env = env
    }

    def execute() {
        steps.container('docker-initial') {
            steps.script {
                steps.script {
                    steps.docker.withRegistry("http://${env.NEXUS_REGISTRY}", env.DOCKER_CREDS_ID) {
                    agentImage.push(env.BUILD_NUMBER)
                    }
                }
            }
        }
    }
}
