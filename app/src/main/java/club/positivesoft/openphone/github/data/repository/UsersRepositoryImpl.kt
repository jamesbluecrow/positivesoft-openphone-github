package club.positivesoft.openphone.github.data.repository

import club.positivesoft.openphone.github.data.extensions.toEntity
import club.positivesoft.openphone.github.db.UserEntity
import club.positivesoft.openphone.github.domain.exceptions.NoInternetConnectionException
import club.positivesoft.openphone.github.domain.exceptions.RateLimitExceededException
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.net.UnknownHostException

class UsersRepositoryImpl(
    private val usersLocalDataSource: UsersLocalDataSource,
    private val usersRemoteDataSource: UsersRemoteDataSource,
) : UsersRepository {
    override suspend fun syncUsers(query: String): Result<List<UserEntity>> {
        usersRemoteDataSource.searchUsers(query).fold(onSuccess = {
            val savedUsers = mutableListOf<UserEntity>()
            for (item in it.items) {
                val userEntity = item.toEntity()
                usersLocalDataSource.create(userEntity)
                savedUsers.add(userEntity)
            }
            return Result.success(savedUsers)
        }, onFailure = {
            when (it) {
                is UnknownHostException -> return Result.failure(NoInternetConnectionException(it))
                is HttpException -> {
                    when (it.code()) {
                        403 -> return Result.failure(RateLimitExceededException(it))
                    }
                }
            }
            return Result.failure(it)
        })
    }

    override suspend fun getUsers(query: String): List<UserEntity> =
        usersLocalDataSource.findByQuery(query)

    override fun getUsersStream(query: String): Flow<List<UserEntity>> =
        usersLocalDataSource.findByQueryStream(query)
}