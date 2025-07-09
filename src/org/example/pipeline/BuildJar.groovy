package org.example.pipeline

class BuildJar implements Serializable {
    def steps

    BuildJar(steps) { 
        this.steps = steps  // store pipeline context
    }

    def execute() {
        steps.container('mvn') {
            steps.sh 'mvn clean package'
        }
    }
}
