package club.positivesoft.openphone.github.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {
    @Query("SELECT * FROM user WHERE login LIKE :query OR email LIKE :query OR name LIKE :query")
    fun observeFindByQuery(query: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM user WHERE login LIKE :query OR email LIKE :query OR name LIKE :query")
    fun findByQuery(query: String): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: UserEntity)
}