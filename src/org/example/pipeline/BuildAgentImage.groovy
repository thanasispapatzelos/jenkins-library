package org.example.pipeline

class BuildAgentImage implements Serializable {
    def steps
    def env

    BuildAgentImage(steps, env) {
        this.steps = steps
        this.env = env
    }

    def execute() {
        steps.script {
            def dockerfileText = steps.libraryResource('org/example/pipeline/Dockerfile')
            steps.writeFile file: 'Dockerfile', text: dockerfileText

            def image = steps.docker.build("${env.NEXUS_REGISTRY}/quarkus:${env.BUILD_NUMBER}")
            return image
        }
    }
}
