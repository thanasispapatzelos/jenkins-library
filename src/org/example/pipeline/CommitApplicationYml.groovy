package org.example.pipeline

class CommitApplicationYml implements Serializable {
    def steps
    def env

    CommitApplicationYml(steps, env) {
        this.steps = steps
        this.env = env
    }

    def execute() {
                steps.container('helm') {
                    steps.sh """

                       git remote set-url origin https://x-access-token:${env.GIT_CREDENTIALS_ID}@github.com/PapatzelosThanashs/quarkus.git

                        git add Application.yml
                        git commit -m "Update Application.yml from pipeline" || echo "No changes"
                        git push 
                    """
                }

        }
}