package club.positivesoft.openphone.github.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import club.positivesoft.openphone.github.domain.exceptions.NoInternetConnectionException
import club.positivesoft.openphone.github.domain.exceptions.RateLimitExceededException
import club.positivesoft.openphone.github.domain.usecases.GetUsersUseCase
import club.positivesoft.openphone.github.domain.usecases.SyncUsersUseCase
import club.positivesoft.openphone.github.presentation.extensions.toViewData
import club.positivesoft.openphone.github.presentation.resources.ResourceProvider
import club.positivesoft.openphone.github.presentation.resources.StringResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.CancellationException
import javax.inject.Inject

data class UserViewData(val name: String, val image: String, val publicRepos: Long)

@HiltViewModel
class UsersListViewModel @Inject constructor(
    private val syncUsersUseCase: SyncUsersUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val resources: ResourceProvider,
) : ViewModel() {
    val isLoading = mutableStateOf(false)
    val currentQuery = mutableStateOf("")
    val users = mutableStateListOf<UserViewData>()
    val infoMessage = mutableStateOf<String?>(null)
    private var currentJob: Job? = null

    fun onQueryChange(query: String) {
        currentQuery.value = query
    }

    fun onSubmitSearch(query: String) {
        cancelPreviousSearch()

        if (query.isEmpty()) {
            users.clear()
            return
        }

        isLoading.value = true
        currentJob = viewModelScope.launch {
            syncUsersUseCase
                .invoke(query)
                .fold(onSuccess = {
                    val users = it.map { item -> item.toViewData() }
                    onSearchSuccess(users)
                }, onFailure = {
                    onSearchError(it)
                    val users = getUsersUseCase.invoke(query)
                    onSearchSuccess(users.map { item -> item.toViewData() })
                })

        }
    }

    fun onSnackbarMessageShown() {
        infoMessage.value = null
    }

    private fun cancelPreviousSearch() {
        if (currentJob?.isActive == true) {
            currentJob?.cancel("Cancelled to use new query!")
            currentJob = null
        }
    }

    private fun onSearchSuccess(items: List<UserViewData>) {
        users.clear()
        users.addAll(items)
        isLoading.value = false

        if (items.isEmpty()) {
            infoMessage.value = resources.get(StringResource.NO_RESULTS_MESSAGE)
        }
    }

    private fun onSearchError(throwable: Throwable) {
        Timber.e(throwable)
        isLoading.value = false
        when (throwable) {
            is CancellationException -> {
                // Ignore error if task was cancelled when new search is triggered.
            }
            is NoInternetConnectionException -> infoMessage.value =
                resources.get(StringResource.OFFLINE_RESULTS_MESSAGE)
            is RateLimitExceededException -> infoMessage.value =
                resources.get(StringResource.RATE_LIMIT_EXCEEDED_MESSAGE)
            else -> infoMessage.value = resources.get(StringResource.UNKNOWN_ERROR_MESSAGE)
        }
    }
}
