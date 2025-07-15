
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
                sed -i "s|^\(\s*repository:\s*\).*|\1${env.NEXUS_REGISTRY}/quarkus|" values.yaml

                # Update tag
                sed -i "s|^\(\s*tag:\s*\).*|\1\"0.1.${env.BUILD_NUMBER}\"|" values.yaml

                cat values.yml

            """
        }
    }
}
