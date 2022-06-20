package club.positivesoft.openphone.github.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "login") val login: String?,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "picture") val picture: String?,
    @ColumnInfo(name = "publicRepos") val publicRepos: Long?,
    @ColumnInfo(name = "type") val type: String?
)