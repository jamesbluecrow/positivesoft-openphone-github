package club.positivesoft.openphone.github.presentation

import club.positivesoft.openphone.github.MainCoroutineRule
import club.positivesoft.openphone.github.data.repository.UsersRepository
import club.positivesoft.openphone.github.db.UserEntity
import club.positivesoft.openphone.github.domain.exceptions.NoInternetConnectionException
import club.positivesoft.openphone.github.domain.exceptions.RateLimitExceededException
import club.positivesoft.openphone.github.domain.usecases.GetUsersUseCase
import club.positivesoft.openphone.github.domain.usecases.SyncUsersUseCase
import club.positivesoft.openphone.github.presentation.extensions.toViewData
import club.positivesoft.openphone.github.presentation.resources.ResourceProvider
import club.positivesoft.openphone.github.presentation.resources.StringResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class UsersListViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Test
    fun onQueryChange_updatesCurrentQueryState() = runTest {
        val query = "new query"

        val usersRepositoryMock = Mockito.mock(UsersRepository::class.java)
        val syncUsersUseCaseMock = SyncUsersUseCase(usersRepositoryMock)
        val getUsersUseCaseMock = GetUsersUseCase(usersRepositoryMock)
        val resourceProviderMock = Mockito.mock(ResourceProvider::class.java)

        val viewModel =
            UsersListViewModel(syncUsersUseCaseMock, getUsersUseCaseMock, resourceProviderMock)
        viewModel.onQueryChange(query)

        Assert.assertEquals(query, viewModel.currentQuery.value)
    }

    @Test
    fun onSnackbarMessageShown_infoMessageState() = runTest {
        val usersRepositoryMock = Mockito.mock(UsersRepository::class.java)
        val syncUsersUseCaseMock = SyncUsersUseCase(usersRepositoryMock)
        val getUsersUseCaseMock = GetUsersUseCase(usersRepositoryMock)
        val resourceProviderMock = Mockito.mock(ResourceProvider::class.java)

        val viewModel =
            UsersListViewModel(syncUsersUseCaseMock, getUsersUseCaseMock, resourceProviderMock)
        viewModel.onSnackbarMessageShown()

        Assert.assertNull(viewModel.infoMessage.value)
    }

    @Test
    fun onSubmitSearch_clearsList() = runTest {
        val query = ""

        val usersRepositoryMock = Mockito.mock(UsersRepository::class.java)
        val syncUsersUseCaseMock = SyncUsersUseCase(usersRepositoryMock)
        val getUsersUseCaseMock = GetUsersUseCase(usersRepositoryMock)
        val resourceProviderMock = Mockito.mock(ResourceProvider::class.java)

        val viewModel =
            UsersListViewModel(syncUsersUseCaseMock, getUsersUseCaseMock, resourceProviderMock)
        viewModel.onSubmitSearch(query)

        Assert.assertTrue(viewModel.users.isEmpty())
    }

    @Test
    fun onSubmitSearch_displaysUsers() = runTest {
        val query = "new query"

        val user = UserEntity(
            id = 1,
            login = "login",
            email = "email",
            name = "name",
            picture = "picture",
            publicRepos = 1,
            type = "User"
        )
        val expectedUser = user.toViewData()
        val users = mutableListOf(user)

        val usersRepositoryMock = Mockito.mock(UsersRepository::class.java)
        Mockito.`when`(usersRepositoryMock.syncUsers(query)).thenReturn(Result.success(users))

        val syncUsersUseCaseMock = SyncUsersUseCase(usersRepositoryMock)
        val getUsersUseCaseMock = GetUsersUseCase(usersRepositoryMock)
        val resourceProviderMock = Mockito.mock(ResourceProvider::class.java)

        val viewModel =
            UsersListViewModel(syncUsersUseCaseMock, getUsersUseCaseMock, resourceProviderMock)
        viewModel.onSubmitSearch(query)

        Assert.assertEquals(1, viewModel.users.size)
        Assert.assertEquals(expectedUser, viewModel.users.single())
    }

    @Test
    fun onSubmitSearch_whenEmpty_displaysNoResultsMessage() = runTest {
        val query = "new query"
        val infoMessage = "No Results"

        val users = mutableListOf<UserEntity>()

        val usersRepositoryMock = Mockito.mock(UsersRepository::class.java)
        Mockito.`when`(usersRepositoryMock.syncUsers(query)).thenReturn(Result.success(users))

        val syncUsersUseCaseMock = SyncUsersUseCase(usersRepositoryMock)
        val getUsersUseCaseMock = GetUsersUseCase(usersRepositoryMock)
        val resourceProviderMock = Mockito.mock(ResourceProvider::class.java)
        Mockito.`when`(resourceProviderMock.get(StringResource.NO_RESULTS_MESSAGE))
            .thenReturn(infoMessage)

        val viewModel =
            UsersListViewModel(syncUsersUseCaseMock, getUsersUseCaseMock, resourceProviderMock)
        viewModel.onSubmitSearch(query)

        Assert.assertEquals(infoMessage, viewModel.infoMessage.value)
    }

    @Test
    fun onSubmitSearch_whenNoInternetConnectionException_displaysErrorMessage() = runTest {
        val query = "new query"
        val infoMessage = "Offline results"
        val user = UserEntity(
            id = 1,
            login = "login",
            email = "email",
            name = "name",
            picture = "picture",
            publicRepos = 1,
            type = "User"
        )
        val expectedUser = user.toViewData()
        val users = mutableListOf(user)

        val usersRepositoryMock = Mockito.mock(UsersRepository::class.java)
        Mockito.`when`(usersRepositoryMock.syncUsers(query))
            .thenReturn(Result.failure(NoInternetConnectionException()))
        Mockito.`when`(usersRepositoryMock.getUsers(query)).thenReturn(users)

        val syncUsersUseCaseMock = SyncUsersUseCase(usersRepositoryMock)
        val getUsersUseCaseMock = GetUsersUseCase(usersRepositoryMock)
        val resourceProviderMock = Mockito.mock(ResourceProvider::class.java)
        Mockito.`when`(resourceProviderMock.get(StringResource.OFFLINE_RESULTS_MESSAGE))
            .thenReturn(infoMessage)

        val viewModel =
            UsersListViewModel(syncUsersUseCaseMock, getUsersUseCaseMock, resourceProviderMock)
        viewModel.onSubmitSearch(query)

        Assert.assertEquals(infoMessage, viewModel.infoMessage.value)
        Assert.assertEquals(1, viewModel.users.size)
        Assert.assertEquals(expectedUser, viewModel.users.single())
    }

    @Test
    fun onSubmitSearch_whenRateLimitExceededException_displaysErrorMessageAndOfflineData() =
        runTest {
            val query = "new query"
            val infoMessage = "Rate limit exceeded"

            val user = UserEntity(
                id = 1,
                login = "login",
                email = "email",
                name = "name",
                picture = "picture",
                publicRepos = 1,
                type = "User"
            )
            val expectedUser = user.toViewData()
            val users = mutableListOf(user)

            val usersRepositoryMock = Mockito.mock(UsersRepository::class.java)
            Mockito.`when`(usersRepositoryMock.syncUsers(query)).thenReturn(
                Result.failure(RateLimitExceededException())
            )
            Mockito.`when`(usersRepositoryMock.getUsers(query)).thenReturn(users)

            val syncUsersUseCaseMock = SyncUsersUseCase(usersRepositoryMock)
            val getUsersUseCaseMock = GetUsersUseCase(usersRepositoryMock)
            val resourceProviderMock = Mockito.mock(ResourceProvider::class.java)
            Mockito.`when`(resourceProviderMock.get(StringResource.RATE_LIMIT_EXCEEDED_MESSAGE))
                .thenReturn(infoMessage)

            val viewModel =
                UsersListViewModel(syncUsersUseCaseMock, getUsersUseCaseMock, resourceProviderMock)
            viewModel.onSubmitSearch(query)

            Assert.assertEquals(infoMessage, viewModel.infoMessage.value)
            Assert.assertEquals(1, viewModel.users.size)
            Assert.assertEquals(expectedUser, viewModel.users.single())
        }
}