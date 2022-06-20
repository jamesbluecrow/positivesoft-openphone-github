package club.positivesoft.openphone.github.domain.usecases

import club.positivesoft.openphone.github.data.repository.UsersRepository
import club.positivesoft.openphone.github.db.UserEntity

class GetUsersUseCase(private val usersRepository: UsersRepository) {
    suspend operator fun invoke(query: String): List<UserEntity> = usersRepository.getUsers(query)
}