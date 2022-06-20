package club.positivesoft.openphone.github.data.repository

import club.positivesoft.openphone.github.db.UserEntity
import club.positivesoft.openphone.github.db.UsersDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UsersLocalDataSourceImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val usersDao: UsersDao,
) : UsersLocalDataSource {
    override fun findByQueryStream(query: String): Flow<List<UserEntity>> =
        usersDao.observeFindByQuery("%${query}%")

    override suspend fun findByQuery(query: String): List<UserEntity> {
        return withContext(ioDispatcher) {
            usersDao.findByQuery("%${query}%")
        }
    }

    override suspend fun create(user: UserEntity) {
        usersDao.insert(user)
    }
}