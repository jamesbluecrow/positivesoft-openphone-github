package club.positivesoft.openphone.github.presentation.resources

interface ResourceProvider {
    fun get(resource: StringResource): String
}