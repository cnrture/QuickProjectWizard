package com.github.cnrture.quickprojectwizard.projectwizard.general.di

fun emptyMainRepositoryModule(
    packageName: String,
    isKoin: Boolean = false,
    hasNetwork: Boolean = false,
    hasLocal: Boolean = false,
) = if (isKoin) {
    val constructorParams = buildList {
        if (hasNetwork) add("mainService = get()")
        if (hasLocal) add("mainDao = get()")
    }.joinToString(", ")
    """
package $packageName.di
    
import $packageName.data.repository.MainRepositoryImpl
import $packageName.domain.repository.MainRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<MainRepository> { MainRepositoryImpl($constructorParams) }
}
""".trimIndent()
} else {
    """
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
}
