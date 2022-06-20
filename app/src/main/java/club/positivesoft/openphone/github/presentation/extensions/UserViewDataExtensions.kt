package club.positivesoft.openphone.github.presentation.extensions

import club.positivesoft.openphone.github.db.UserEntity
import club.positivesoft.openphone.github.presentation.UserViewData

fun UserEntity.toViewData(): UserViewData = UserViewData(
    name = this.login ?: "",
    image = this.picture ?: "",
    publicRepos = this.publicRepos ?: 0
)

