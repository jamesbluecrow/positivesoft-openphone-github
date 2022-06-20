package club.positivesoft.openphone.github.di

import android.content.Context
import androidx.room.Room
import club.positivesoft.openphone.github.data.api.github.GithubApi
import club.positivesoft.openphone.github.data.repository.*
import club.positivesoft.openphone.github.db.AppDatabase
import club.positivesoft.openphone.github.domain.usecases.GetUsersUseCase
import club.positivesoft.openphone.github.domain.usecases.SyncUsersUseCase
import club.positivesoft.openphone.github.presentation.resources.ResourceProvider
import club.positivesoft.openphone.github.presentation.resources.ResourceProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Database

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
/**
 * As the app grows we would probably split the DI module into smaller modules...
 */
object DaggerModule {
    @Singleton
    @Provides
    @Database
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "AppDatabase.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideUsersRepository(
        usersLocalDataSource: UsersLocalDataSource,
        usersRemoteDataSource: UsersRemoteDataSource
    ): UsersRepository = UsersRepositoryImpl(usersLocalDataSource, usersRemoteDataSource)

    @Singleton
    @Provides
    fun provideGithubApi(): GithubApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(GithubApi::class.java)
    }

    @Singleton
    @Provides
    fun provideUsersLocalDataSource(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @Database database: AppDatabase,
    ): UsersLocalDataSource {
        return UsersLocalDataSourceImpl(ioDispatcher, database.userDao())
    }

    @Singleton
    @Provides
    fun provideUsersRemoteDataSource(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        githubApi: GithubApi,
    ): UsersRemoteDataSource {
        return UsersRemoteDataSourceImpl(ioDispatcher, githubApi)
    }

    @Provides
    fun provideSyncUsersUseCase(usersRepository: UsersRepository): SyncUsersUseCase =
        SyncUsersUseCase(usersRepository)

    @Provides
    fun provideGetUsersUseCase(usersRepository: UsersRepository): GetUsersUseCase =
        GetUsersUseCase(usersRepository)

    @Provides
    fun provideResourceProvider(@ApplicationContext context: Context): ResourceProvider =
        ResourceProviderImpl(context)

    @Provides
    @IoDispatcher
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}


