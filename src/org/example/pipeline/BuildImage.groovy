package org.example.pipeline

class BuildImage implements Serializable {
    def steps

    BuildImage(steps) {
        this.steps = steps
    }

    def execute(String nexusRegistry, String imageTag) {
        steps.container('docker-cli') {
            steps.script {
                // Build the Docker image and store it in a variable accessible outside if needed
                def myImage = steps.docker.build("${nexusRegistry}/quarkus:${imageTag}")
                
            }
        }
    }
}
