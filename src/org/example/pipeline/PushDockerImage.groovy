package org.example.pipeline

class PushDockerImage implements Serializable {
    def steps
    def image
    def registry
    def credsId
    def imageTag

    PushDockerImage(steps, image, registry, credsId, imageTag) {
        this.steps = steps
        this.image = image
        this.registry = registry
        this.credsId = credsId
        this.imageTag = imageTag
    }

    def execute() {
        steps.container('docker-cli') {
            steps.script {
                steps.docker.withRegistry("http://${registry}", credsId) {
                    image.push(imageTag)
                }
            }
        }
    }
}
