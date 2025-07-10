package org.example.pipeline

class BuildAgentImage implements Serializable {
    def steps
    def env

    BuildAgentImage(steps, env) {
        this.steps = steps
        this.env = env
    }

    def execute() {
        steps.container('docker') {
            steps.script {
                def dockerfileText = steps.libraryResource('docker/Dockerfile')
                steps.writeFile file: 'Dockerfile', text: dockerfileText

                def image = steps.docker.build("${env.NEXUS_REGISTRY}/custom-agent:myversion")
                return image
            }
        }
    }
}
