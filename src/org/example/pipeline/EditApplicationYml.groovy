package org.example.pipeline

class EditApplicationYml implements Serializable {
    def steps
    def env

    EditApplicationYml(steps, env) {
        this.steps = steps
        this.env = env
    }

    def execute() {
        steps.container('helm') {
            steps.sh """

           sed -i "s/targetRevision:.*/targetRevision: 0.1.${env.BUILD_NUMBER}/" Application.yaml

            """
        }
    }
}
