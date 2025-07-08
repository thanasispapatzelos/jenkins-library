def call(Closure body) {
    def yamlContent = libraryResource('podTemplates/mvn-docker-helm.yaml')
    
    podTemplate(yaml: yamlContent) {
        node(POD_LABEL) {
            body()
        }
    }
}
