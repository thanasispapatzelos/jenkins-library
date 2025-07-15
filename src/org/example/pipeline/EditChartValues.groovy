
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
            steps.sh """

                cd GitOps/apps/dev

                # Update repository
                sed -i "s|^\\(\\s*repository:\\s*\\).*|\\1host.docker.internal:30050/quarkus|" values.yml
                sed -i "s|^\\(\\s*tag:\\s*\\).*|\\1\\"0.1.${env.BUILD_NUMBER}\\"|" values.yml

                cat values.yml

                git config --global --add safe.directory .
                git remote set-url origin https://x-access-token:\$GITHUB_PAT@github.com/PapatzelosThanashs/GitOps.git
                git config user.email "papatzelosthanashs@gmail.com"
                git config user.name "PapatzelosThanashs"

                git add values.yml
                git commit -m "Update values.yml from pipeline" || echo "No changes"
                git push origin HEAD:master

            """
        }
    }
}
