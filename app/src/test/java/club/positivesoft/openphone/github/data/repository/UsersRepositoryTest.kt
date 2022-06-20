package club.positivesoft.openphone.github.data.repository

import club.positivesoft.openphone.github.MainCoroutineRule
import club.positivesoft.openphone.github.data.api.github.model.SearchUsersApiResponse
import club.positivesoft.openphone.github.data.api.github.model.UserApiResponse
import club.positivesoft.openphone.github.data.extensions.toEntity
import club.positivesoft.openphone.github.db.UserEntity
import club.positivesoft.openphone.github.domain.exceptions.NoInternetConnectionException
import club.positivesoft.openphone.github.domain.exceptions.RateLimitExceededException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

@ExperimentalCoroutinesApi
class UsersRepositoryTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Test
    fun syncUsers_whenSuccess_saveUsersToDatabase() = runTest {
        val query = "new query"

        val apiUser = UserApiResponse(
            id = 1,
            login = "login",
            email = "email",
            name = "name",
            avatarUrl = "picture",
            publicRepos = 1,
            type = "User"
        )
        val expectedUser = apiUser.toEntity()
        val apiUsers = mutableListOf(apiUser)
        val apiResponse = SearchUsersApiResponse(1, false, apiUsers)

        val usersLocalDataSourceMock = Mockito.mock(UsersLocalDataSource::class.java)
        val usersRemoteDataSourceMock = Mockito.mock(UsersRemoteDataSource::class.java)
        Mockito.`when`(usersRemoteDataSourceMock.searchUsers(query))
            .thenReturn(Result.success(apiResponse))

        val usersRepository =
            UsersRepositoryImpl(usersLocalDataSourceMock, usersRemoteDataSourceMock)
        val result = usersRepository.syncUsers(query)

        result.onSuccess {
            Assert.assertEquals(it.size, 1)

            Assert.assertEquals(it.single().id, expectedUser.id)
            Assert.assertEquals(it.single().login, expectedUser.login)
            Assert.assertEquals(it.single().name, expectedUser.name)
            Assert.assertEquals(it.single().email, expectedUser.email)
            Assert.assertEquals(it.single().picture, expectedUser.picture)
            Assert.assertEquals(it.single().publicRepos, expectedUser.publicRepos)
            Assert.assertEquals(it.single().type, expectedUser.type)
        }.onFailure {
            throw Exception("Result should be successful.")
        }

        Mockito.verify(usersLocalDataSourceMock, Mockito.times(1)).create(expectedUser)
    }

    @Test
    fun syncUsers_whenForbidden_returnsRateLimitExceededException() = runTest {
        val query = "new query"

        val usersLocalDataSourceMock = Mockito.mock(UsersLocalDataSource::class.java)
        val usersRemoteDataSourceMock = Mockito.mock(UsersRemoteDataSource::class.java)
        Mockito.`when`(usersRemoteDataSourceMock.searchUsers(query))
            .thenReturn(
                Result.failure(
                    HttpException(
                        Response.error<SearchUsersApiResponse>(
                            403,
                            ResponseBody.create(
                                contentType = null,
                                content = "",
                            )
                        )
                    )
                )
            )

        val usersRepository =
            UsersRepositoryImpl(usersLocalDataSourceMock, usersRemoteDataSourceMock)
        val result = usersRepository.syncUsers(query)

        result.onSuccess {
            throw Exception("Result should be failure.")
        }.onFailure {
            Assert.assertTrue(it is RateLimitExceededException)
        }
    }

    @Test
    fun syncUsers_whenNoInternet_returnsNoInternetConnectionException() = runTest {
        val query = "new query"

        val usersLocalDataSourceMock = Mockito.mock(UsersLocalDataSource::class.java)
        val usersRemoteDataSourceMock = Mockito.mock(UsersRemoteDataSource::class.java)
        Mockito.`when`(usersRemoteDataSourceMock.searchUsers(query))
            .thenReturn(Result.failure(UnknownHostException()))

        val usersRepository =
            UsersRepositoryImpl(usersLocalDataSourceMock, usersRemoteDataSourceMock)
        val result = usersRepository.syncUsers(query)

        result.onSuccess {
            throw Exception("Result should be failure.")
        }.onFailure {
            Assert.assertTrue(it is NoInternetConnectionException)
        }
    }

    @Test
    fun getUsers_returnsLocalStoredUsers() = runTest {
        val query = "new query"

        val users = mutableListOf(
            UserEntity(
                id = 1,
                login = "login",
                email = "email",
                name = "name",
                picture = "picture",
                publicRepos = 1,
                type = "User"
            )
        )

        val usersLocalDataSourceMock = Mockito.mock(UsersLocalDataSource::class.java)
        val usersRemoteDataSourceMock = Mockito.mock(UsersRemoteDataSource::class.java)

        Mockito.`when`(usersLocalDataSourceMock.findByQuery(query)).thenReturn(users)
        val localDataSource =
            UsersRepositoryImpl(usersLocalDataSourceMock, usersRemoteDataSourceMock)
        val result = localDataSource.getUsers(query)

        Assert.assertEquals(users, result)
    }
}