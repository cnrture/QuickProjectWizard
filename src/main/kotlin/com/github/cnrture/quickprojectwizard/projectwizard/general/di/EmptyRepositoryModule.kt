package com.github.cnrture.quickprojectwizard.projectwizard.general.di

fun emptyMainRepositoryModule(packageName: String) = """
package $packageName.di
    
import $packageName.data.repository.MainRepositoryImpl
import $packageName.domain.repository.MainRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindMainRepository(repositoryImpl: MainRepositoryImpl): MainRepository
}
""".trimIndent()
