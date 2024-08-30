package com.github.cnrture.quickprojectwizard.arch.di

import com.github.cnrture.quickprojectwizard.NetworkLibrary

fun emptyNetworkModule(packageName: String, selectedNetworkLibrary: NetworkLibrary): String {
    return when (selectedNetworkLibrary) {
        NetworkLibrary.Retrofit -> emptyRetrofitModule(packageName)
        NetworkLibrary.Ktor -> emptyKtorModule(packageName)
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
