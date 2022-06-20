package club.positivesoft.openphone.github.data.repository

import club.positivesoft.openphone.github.db.UserEntity
import kotlinx.coroutines.flow.Flow

// Defining an interface will allow us to create a fake variation for testing.
interface UsersRepository {
    suspend fun syncUsers(query: String): Result<List<UserEntity>>
    suspend fun getUsers(query: String): List<UserEntity>
    fun getUsersStream(query: String): Flow<List<UserEntity>>
}