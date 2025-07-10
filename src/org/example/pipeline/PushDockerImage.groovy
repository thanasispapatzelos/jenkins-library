package org.example.pipeline

class PushDockerImage implements Serializable {
    def steps
    def image
    def env

    PushDockerImage(steps, image, env) {
        this.steps = steps
        this.image = image
        this.env= env
    }

    def execute() {
        steps.container('docker') {
            steps.script {
                steps.docker.withRegistry("http://${env.NEXUS_REGISTRY}", env.DOCKER_CREDS_ID) {
                    image.push(env.BUILD_NUMBER)
                }
            }
        }
    }
}
