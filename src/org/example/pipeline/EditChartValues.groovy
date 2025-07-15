
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

                ls -l 

            """
        }
    }
}
