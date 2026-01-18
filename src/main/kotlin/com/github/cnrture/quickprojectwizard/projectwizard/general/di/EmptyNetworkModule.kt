package com.github.cnrture.quickprojectwizard.projectwizard.general.di

import com.github.cnrture.quickprojectwizard.data.NetworkLibrary

fun emptyNetworkModule(packageName: String, selectedNetworkLibrary: NetworkLibrary, isKoin: Boolean = false): String {
    return when (selectedNetworkLibrary) {
        NetworkLibrary.Retrofit -> if (isKoin) emptyRetrofitModuleKoin(packageName) else emptyRetrofitModule(packageName)
        NetworkLibrary.Ktor -> if (isKoin) emptyKtorModuleKoin(packageName) else emptyKtorModule(packageName)
        NetworkLibrary.None -> ""
    }
}

private fun emptyRetrofitModule(packageName: String) = """
package $packageName.di

import $packageName.data.source.remote.MainService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideMainService(retrofit: Retrofit): MainService {
        return retrofit.create(MainService::class.java)
    }
}
"""

private fun emptyKtorModule(packageName: String) = """
package $packageName.di

import $packageName.data.source.remote.MainService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideMainService(): MainService = MainService()
}
"""

private fun emptyRetrofitModuleKoin(packageName: String) = """
package $packageName.di

import $packageName.data.source.remote.MainService
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    single { get<Retrofit>().create(MainService::class.java) }
}
"""

private fun emptyKtorModuleKoin(packageName: String) = """
package $packageName.di

import $packageName.data.source.remote.MainService
import org.koin.dsl.module

val networkModule = module {
    single { MainService() }
}
"""
