package org.example.pipeline

class InstallHelmChart implements Serializable {
    def steps
    def env

    InstallHelmChart(steps, env) {
        this.steps = steps
        this.env = env
    }

    def execute() {
        steps.container('helm') {
            steps.helmWithKubeconfig {
                steps.echo "Deploying to ${env.ENV_NAME}"

                steps.sh """
                    helm repo add --username \$USERNAME --password \$PASSWORD helm-nexus http://nexus-nexus-repository-manager:8081/repository/helm-repo/
                    helm repo update
                    helm repo list
                    helm upgrade --install helm-nexus helm-nexus/my-chart \\
                        --version 0.1.0 \\
                        -n jenkins \\
                        --set image.repository=nexus.docker:30050/quarkus \\
                        --set image.tag=${env.BUILD_NUMBER}
                """
            }
        }
    }
}
