package club.positivesoft.openphone.github.data.api.github.model

import com.google.gson.annotations.SerializedName

data class SearchUsersApiResponse(
    @SerializedName("total_count") val totalCount: Long,
    @SerializedName("incomplete_results") val incompleteResults: Boolean,
    @SerializedName("items") val items: List<UserApiResponse>
)