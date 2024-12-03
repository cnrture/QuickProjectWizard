package com.github.cnrture.quickprojectwizard.general.di

fun emptyLocalModule(packageName: String) = """
package $packageName.di

import android.content.Context
import androidx.room.Room
import $packageName.data.source.local.MainDao
import $packageName.data.source.local.MainRoomDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): MainRoomDB {
        return Room.databaseBuilder(
            context,
            MainRoomDB::class.java,
            MainRoomDB::class.simpleName
        ).build()
    }

    @Provides
    fun provideMainDao(database: MainRoomDB): MainDao = database.mainDao()
}
"""
