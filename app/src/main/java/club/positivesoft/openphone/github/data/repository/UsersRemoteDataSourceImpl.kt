package club.positivesoft.openphone.github.data.repository

import club.positivesoft.openphone.github.data.api.github.GithubApi
import club.positivesoft.openphone.github.data.api.github.model.OrderType
import club.positivesoft.openphone.github.data.api.github.model.SearchUsersApiResponse
import club.positivesoft.openphone.github.data.api.github.model.SortType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.await

class UsersRemoteDataSourceImpl(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val githubDataSource: GithubApi
) :
    UsersRemoteDataSource {
    override suspend fun searchUsers(
        query: String,
        sort: SortType?,
        order: OrderType?,
        perPage: Int?,
        page: Int?
    ): Result<SearchUsersApiResponse> {
        return withContext(coroutineDispatcher) {
            runCatching { githubDataSource.searchUsers(query, sort, order, perPage, page).await() }
        }
    }
}