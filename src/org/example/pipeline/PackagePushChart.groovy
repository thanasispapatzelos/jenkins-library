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
                    cd ./my-chart/
                    sed -i "s/^version:.*/version: 0.1.${env.BUILD_NUMBER}/" Chart.yaml
                    sed -i 's|^\\(\\s*repository:\\s*\\).*|\\1nexus.docker:30050/quarkus|' values.yaml
                    sed -i 's|^\\(\\s*tag:\\s*\\).*|\\1${env.BUILD_NUMBER}|' values.yaml
                    helm lint .
                    helm package .
                    curl -u \$USERNAME:\$PASSWORD --upload-file my-chart-0.1.${env.BUILD_NUMBER}.tgz ${env.NEXUS_HELM_REGISTRY}
                """
            }
        }
    }
}
