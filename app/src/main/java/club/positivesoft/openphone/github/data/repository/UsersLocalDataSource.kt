package club.positivesoft.openphone.github.data.repository

import club.positivesoft.openphone.github.db.UserEntity
import kotlinx.coroutines.flow.Flow

interface UsersLocalDataSource {
    fun findByQueryStream(query: String): Flow<List<UserEntity>>
    suspend fun findByQuery(query: String): List<UserEntity>
    suspend fun create(user: UserEntity)
}