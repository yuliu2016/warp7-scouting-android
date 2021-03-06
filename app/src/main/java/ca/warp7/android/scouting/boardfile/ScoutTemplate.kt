package ca.warp7.android.scouting.boardfile

class ScoutTemplate(val screens: List<TemplateScreen>, val tags: List<String>) {
    private val indices = screens.map { it.layout.flatten() }.flatten()

    fun lookup(templateField: TemplateField): Int {
        return indices.indexOf(templateField)
    }

    fun lookupForTag(tagIndex: Int): Int {
        return indices.size + tagIndex
    }
}