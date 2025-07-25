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
                steps.withCredentials([steps.string(credentialsId: env.GIT_CREDENTIALS_ID , variable: 'GITHUB_PAT')]) {
                    steps.sh """
                        git config --global --add safe.directory .
                        git remote set-url origin https://x-access-token:\$GITHUB_PAT@github.com/PapatzelosThanashs/quarkus.git
                          git config user.email "papatzelosthanashs@gmail.com"
                          git config user.name "PapatzelosThanashs"

                        git add Application.yaml
                        git commit -m "Update Application.yml from pipeline" || echo "No changes"
                        git push origin HEAD:master
                    """
                    }
                }
        }
}