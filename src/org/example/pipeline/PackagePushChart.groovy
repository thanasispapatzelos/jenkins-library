package org.example.pipeline

class PackagePushChart implements Serializable {
    def steps

    PackagePushChart(steps) {
        this.steps = steps
    }

    def execute() {
        steps.container('helm') {
            steps.helmWithKubeconfig {
                steps.sh '''
                    helm package ./my-chart
                    curl -u $USERNAME:$PASSWORD --upload-file my-chart-0.1.0.tgz http://nexus-nexus-repository-manager:8081/repository/helm-repo/
                '''
            }
        }
    }
}
