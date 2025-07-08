def call(Closure body) {
    withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG_FILE')]) {
        withEnv(["KUBECONFIG=${KUBECONFIG_FILE}"]) {
            sh '''
                # Replace IP in kubeconfig
                sed -i 's|127.0.0.1|host.docker.internal|g' $KUBECONFIG
            '''
            body()
        }
    }
}
