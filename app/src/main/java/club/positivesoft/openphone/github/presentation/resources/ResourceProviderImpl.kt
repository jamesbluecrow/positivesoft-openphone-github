package club.positivesoft.openphone.github.presentation.resources

import android.content.Context
import club.positivesoft.openphone.github.R

/**
 * This allows us to replace strings with some other system other than android resources if needed.
 */
class ResourceProviderImpl(private val applicationContext: Context) : ResourceProvider {
    override fun get(resource: StringResource): String {
        return when (resource) {
            StringResource.NO_RESULTS_MESSAGE -> applicationContext.getString(R.string.no_results_message)
            StringResource.OFFLINE_RESULTS_MESSAGE -> applicationContext.getString(R.string.offline_results_message)
            StringResource.RATE_LIMIT_EXCEEDED_MESSAGE -> applicationContext.getString(R.string.rate_limit_exceeded_message)
            StringResource.UNKNOWN_ERROR_MESSAGE -> applicationContext.getString(R.string.unknown_error)
            else -> throw NotImplementedError("Resource type $resource not implemented.")
        }
    }
}