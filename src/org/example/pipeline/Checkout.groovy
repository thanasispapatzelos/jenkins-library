class Checkout implements Serializable {
    def steps
    Checkout(steps) { 
        this.steps = steps  // store reference to pipeline steps
    }

    def execute() {
        steps.checkout steps.scm  // use pipeline DSL 'checkout' step and scm var
    }
}
