package club.positivesoft.openphone.github.domain

import club.positivesoft.openphone.github.data.repository.UsersRepository
import club.positivesoft.openphone.github.db.UserEntity
import club.positivesoft.openphone.github.domain.usecases.GetUsersUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class GetUsersUseCaseTest {
    @Test
    fun getUsers_returnsData() = runTest {
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
        Mockito.`when`(userRepositoryMock.getUsers(query)).thenReturn(users)
        val useCase = GetUsersUseCase(userRepositoryMock)
        val result = useCase.invoke(query)

        Assert.assertEquals(users, result)
    }
}