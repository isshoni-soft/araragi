package tv.isshoni.araragi.plugin.extension

class AraragiExtension {

    AraragiTestExtension testingExtension = new AraragiTestExtension()

    def testing(Closure c) {
        c.resolveStrategy = Closure.DELEGATE_FIRST
        c.delegate = this.testingExtension
        c()
    }
}
