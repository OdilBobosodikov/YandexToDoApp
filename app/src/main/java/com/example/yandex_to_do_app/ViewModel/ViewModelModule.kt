package com.example.yandex_to_do_app.ViewModel

import com.example.yandex_to_do_app.interfaces.ToDoItemRepository
import dagger.Module
import dagger.Provides

@Module
object ViewModelModule {

    @Provides
    fun provideToDoViewModel(repository: ToDoItemRepository): ToDoViewModel {
        return ToDoViewModel(repository)
    }
}