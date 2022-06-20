package club.positivesoft.openphone.github.domain.usecases

import club.positivesoft.openphone.github.data.repository.UsersRepository
import club.positivesoft.openphone.github.db.UserEntity

class SyncUsersUseCase(private val usersRepository: UsersRepository) {
    suspend operator fun invoke(query: String): Result<List<UserEntity>> =
        usersRepository.syncUsers(query)
}