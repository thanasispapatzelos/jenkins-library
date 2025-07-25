
package org.example.pipeline

class EditChartValues implements Serializable {
    def steps
    def env

    EditChartValues(steps, env) {
        this.steps = steps
        this.env = env
    }

    def execute() {
        steps.container('helm') {
            steps.dir('GitOps') {
                steps.git url: 'https://github.com/PapatzelosThanashs/GitOps.git', branch: 'master'
            }
            steps.withCredentials([steps.string(credentialsId: env.GIT_CREDENTIALS_ID , variable: 'GITHUB_PAT')]) {
                steps.sh """
                    
                    git config --global --add safe.directory /home/jenkins/agent/workspace/multi_master/GitOps
                    cd GitOps/apps/dev

                    # Update repository
                    sed -i "s|^\\(\\s*repository:\\s*\\).*|\\1host.docker.internal:30050/quarkus|" values.yaml
                    sed -i "s|^\\(\\s*tag:\\s*\\).*|\\1\\"${env.BUILD_NUMBER}\\"|" values.yaml

                    cat values.yaml

                    git remote set-url origin https://x-access-token:\$GITHUB_PAT@github.com/PapatzelosThanashs/GitOps.git
                    git config user.email "papatzelosthanashs@gmail.com"
                    git config user.name "PapatzelosThanashs"

                    git add values.yaml
                    git commit -m "Update values.yaml from pipeline" || echo "No changes"
                    git push origin HEAD:master

                """
            }
        }
    }
}
