package com.example.yandex_to_do_app.repository

import com.example.yandex_to_do_app.interfaces.ToDoApiService
import com.example.yandex_to_do_app.interfaces.ToDoItemRepository
import dagger.Module
import dagger.Provides

@Module
object RepositoryModule {
    @Provides
    fun provideToDoRepository(apiService: ToDoApiService): ToDoItemRepository {
        return ToDoItemRepositoryImp(apiService)
    }
}