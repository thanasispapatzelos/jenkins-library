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
                    helm repo add --username \$USERNAME --password \$PASSWORD helm-nexus ${env.NEXUS_HELM_REGISTRY}
                    helm repo update
                    helm repo list
                    helm upgrade --install helm-nexus helm-nexus/my-chart \\
                        --version 0.1.${env.BUILD_NUMBER} \\
                        -n jenkins 
                """
            }
            //--set image.repository=${env.NEXUS_REGISTRY}/quarkus \\
            //--set image.tag=${env.BUILD_NUMBER}
        }
    }
}
