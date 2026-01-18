package com.github.cnrture.quickprojectwizard.projectwizard.general.di

import com.github.cnrture.quickprojectwizard.data.NetworkLibrary

fun emptyNetworkModule(packageName: String, selectedNetworkLibrary: NetworkLibrary, isKoin: Boolean = false): String {
    return when (selectedNetworkLibrary) {
        NetworkLibrary.Retrofit -> if (isKoin) emptyRetrofitModuleKoin(packageName) else emptyRetrofitModule(packageName)
        NetworkLibrary.Ktor -> if (isKoin) emptyKtorModuleKoin(packageName) else emptyKtorModule(packageName)
        NetworkLibrary.Ktorfit -> if (isKoin) emptyKtorfitModuleKoin(packageName) else emptyKtorfitModule(packageName)
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

private fun emptyKtorfitModule(packageName: String) = """
package $packageName.di

import $packageName.data.source.remote.MainService
import $packageName.data.source.remote.createMainService
import de.jensklingenberg.ktorfit.Ktorfit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }

    @Provides
    fun provideKtorfit(httpClient: HttpClient): Ktorfit {
        return Ktorfit.Builder()
            .baseUrl("https://api.example.com/")
            .httpClient(httpClient)
            .build()
    }

    @Provides
    fun provideMainService(ktorfit: Ktorfit): MainService {
        return ktorfit.createMainService()
    }
}
"""

private fun emptyKtorfitModuleKoin(packageName: String) = """
package $packageName.di

import $packageName.data.source.remote.MainService
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }
    
    single {
        Ktorfit.Builder()
            .baseUrl("https://api.example.com/")
            .httpClient(get())
            .build()
    }
    
    single { get<Ktorfit>().create<MainService>() }
}
"""
