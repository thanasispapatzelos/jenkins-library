package org.example.pipeline

class PackagePushChart implements Serializable {
    def steps
    def env

    PackagePushChart(steps, env) {
        this.steps = steps
        this.env = env
    }

    def execute() {
        steps.container('helm') {
            steps.helmWithKubeconfig {
                steps.sh """
                    helm package ./my-chart
                    curl -u \$USERNAME:\$PASSWORD --upload-file my-chart-0.1.0.tgz ${env.NEXUS_HELM_REGISTRY}
                """
            }
        }
    }
}
