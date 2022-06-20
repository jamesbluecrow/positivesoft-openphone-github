package club.positivesoft.openphone.github.data.repository

import club.positivesoft.openphone.github.MainCoroutineRule
import club.positivesoft.openphone.github.db.UserEntity
import club.positivesoft.openphone.github.db.UsersDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class UsersLocalDataSourceTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Test
    fun findByQuery_returnsDataFromDao() = runTest {
        val query = "new query"
        val expectedQuery = "%${query}%"

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

        val usersDaoMock = mock(UsersDao::class.java)
        `when`(usersDaoMock.findByQuery(expectedQuery)).thenReturn(users)
        val localDataSource = UsersLocalDataSourceImpl(Dispatchers.Main, usersDaoMock)
        val result = localDataSource.findByQuery(query)

        Assert.assertEquals(users, result)
    }

    @Test
    fun create_callsDao() = runTest {
        val user = UserEntity(
            id = 1,
            login = "login",
            email = "email",
            name = "name",
            picture = "picture",
            publicRepos = 1,
            type = "User"
        )

        val usersDaoMock = mock(UsersDao::class.java)
        val localDataSource = UsersLocalDataSourceImpl(Dispatchers.Main, usersDaoMock)
        localDataSource.create(user)

        verify(usersDaoMock, times(1)).insert(user)
    }
}