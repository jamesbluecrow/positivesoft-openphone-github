package club.positivesoft.openphone.github.data.api.github

import club.positivesoft.openphone.github.data.api.github.model.OrderType
import club.positivesoft.openphone.github.data.api.github.model.SearchUsersApiResponse
import club.positivesoft.openphone.github.data.api.github.model.SortType
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface GithubApi {
    /**
     * Query users & organisations.
     * [See endpoint documentation](https://docs.github.com/en/rest/search#search-users)
     */
    @Headers("Accept: application/vnd.github.v3+json")
    @GET("search/users")
    fun searchUsers(
        @Query("q") query: String,
        @Query("sort") sort: SortType? = null,
        @Query("order") order: OrderType? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null
    ): Call<SearchUsersApiResponse>
}