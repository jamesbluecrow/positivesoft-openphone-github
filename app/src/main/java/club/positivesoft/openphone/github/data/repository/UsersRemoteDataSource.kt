package club.positivesoft.openphone.github.data.repository


import club.positivesoft.openphone.github.data.api.github.model.OrderType
import club.positivesoft.openphone.github.data.api.github.model.SearchUsersApiResponse
import club.positivesoft.openphone.github.data.api.github.model.SortType

interface UsersRemoteDataSource {
    suspend fun searchUsers(
        query: String,
        sort: SortType? = null,
        order: OrderType? = null,
        perPage: Int? = null,
        page: Int? = null
    ): Result<SearchUsersApiResponse>
}