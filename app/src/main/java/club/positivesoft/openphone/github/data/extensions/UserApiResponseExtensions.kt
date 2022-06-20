package club.positivesoft.openphone.github.data.extensions

import club.positivesoft.openphone.github.data.api.github.model.UserApiResponse
import club.positivesoft.openphone.github.db.UserEntity

fun UserApiResponse.toEntity(): UserEntity = UserEntity(
    id = this.id,
    login = this.login,
    name = this.name,
    email = this.email,
    picture = this.avatarUrl,
    publicRepos = this.publicRepos,
    type = this.type
)
