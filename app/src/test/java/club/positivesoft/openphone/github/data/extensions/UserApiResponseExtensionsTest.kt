package club.positivesoft.openphone.github.data.extensions

import club.positivesoft.openphone.github.data.api.github.model.UserApiResponse
import org.junit.Assert
import org.junit.Test

class UserApiResponseExtensionsTest {
    @Test
    fun toEntity_mapsData() {
        val data =
            UserApiResponse(id = 1, login = "login", avatarUrl = "avatar_url", publicRepos = 1)
        val result = data.toEntity()
        Assert.assertEquals(data.id, result.id)
        Assert.assertEquals(data.login, result.login)
        Assert.assertEquals(data.avatarUrl, result.picture)
        Assert.assertEquals(data.publicRepos, result.publicRepos)
    }
}