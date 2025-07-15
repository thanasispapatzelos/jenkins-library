package org.example.pipeline

class Checkout implements Serializable {
    def steps
    Checkout(steps) { 
        this.steps = steps  // store reference to pipeline steps
    }

    def execute() {
        //steps.checkout steps.scm  // use pipeline DSL 'checkout' step and scm var
        steps.git url: 'https://github.com/PapatzelosThanashs/quarkus.git', branch: 'master'

    }
}
