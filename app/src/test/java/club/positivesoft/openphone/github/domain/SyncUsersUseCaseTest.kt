package club.positivesoft.openphone.github.domain

import club.positivesoft.openphone.github.data.repository.UsersRepository
import club.positivesoft.openphone.github.db.UserEntity
import club.positivesoft.openphone.github.domain.usecases.SyncUsersUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class SyncUsersUseCaseTest {
    @Test
    fun syncUsers_returnsData() = runTest {
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

        val userRepositoryMock = Mockito.mock(UsersRepository::class.java)
        Mockito.`when`(userRepositoryMock.syncUsers(query)).thenReturn(Result.success(users))
        val useCase = SyncUsersUseCase(userRepositoryMock)
        val result = useCase.invoke(query)

        result.onSuccess {
            Assert.assertEquals(users, it)
        }.onFailure {
            throw Exception("Result should have been successful.")
        }
    }
}