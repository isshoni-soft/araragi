package tv.isshoni.araragi.plugin.extension

class AraragiTestExtension {

    List<String> patchModulesList = new LinkedList<>()
    List<String> opensList = new LinkedList<>()

    boolean prettyBoolean = true

    def pretty(boolean pretty) {
        this.prettyBoolean = pretty
    }

    def add_opens(String first, String second) {
        this.opensList.add(first)
        this.opensList.add(second)
    }

    def add_patch(String patch) {
        this.patchModulesList.add(patch)
    }
}
