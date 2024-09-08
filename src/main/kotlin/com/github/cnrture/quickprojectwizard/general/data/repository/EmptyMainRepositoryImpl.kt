package com.github.cnrture.quickprojectwizard.general.data.repository

fun emptyMainRepositoryImpl(
    packageName: String,
    roomState: Boolean,
    serviceState: Boolean,
    hiltState: Boolean,
): String {
    return when {
        roomState && serviceState && hiltState -> serviceRoomHilt(packageName)
        roomState && serviceState -> serviceRoom(packageName)
        roomState && hiltState -> roomHilt(packageName)
        roomState -> room(packageName)
        serviceState && hiltState -> serviceHilt(packageName)
        serviceState -> service(packageName)
        hiltState -> hilt(packageName)
        else -> empty(packageName)
    }
}

private fun serviceRoomHilt(packageName: String) = """
    package $packageName.data.repository
            
    import $packageName.data.source.local.MainRoomDB
    import $packageName.data.source.remote.MainService
    import $packageName.domain.repository.MainRepository
    import javax.inject.Inject
            
    class MainRepositoryImpl @Inject constructor(
        private val mainService: MainService,
        private val mainRoomDB: MainRoomDB,
    ) : MainRepository
""".trimIndent()

private fun serviceRoom(packageName: String) = """
    package $packageName.data.repository
            
    import $packageName.data.source.local.MainRoomDB
    import $packageName.data.source.remote.MainService
    import $packageName.domain.repository.MainRepository
            
    class MainRepositoryImpl(
        private val mainService: MainService,
        private val mainRoomDB: MainRoomDB,
    ) : MainRepository
""".trimIndent()

private fun roomHilt(packageName: String) = """
    package $packageName.data.repository
            
    import $packageName.data.local.MainRoomDB
    import $packageName.domain.repository.MainRepository
    import javax.inject.Inject
            
    class MainRepositoryImpl @Inject constructor(
        private val mainRoomDB: MainRoomDB,
    ) : MainRepository
""".trimIndent()

private fun room(packageName: String) = """
    package $packageName.data.repository
            
    import $packageName.data.local.MainRoomDB
    import $packageName.domain.repository.MainRepository
            
    class MainRepositoryImpl(
        private val mainRoomDB: MainRoomDB,
    ) : MainRepository
""".trimIndent()

private fun serviceHilt(packageName: String) = """
    package $packageName.data.repository
            
    import $packageName.data.remote.MainService
    import $packageName.domain.repository.MainRepository
    import javax.inject.Inject
            
    class MainRepositoryImpl @Inject constructor(
        private val mainService: MainService,
    ) : MainRepository
""".trimIndent()

private fun service(packageName: String) = """
    package $packageName.data.repository
            
    import $packageName.data.remote.MainService
    import $packageName.domain.repository.MainRepository
            
    class MainRepositoryImpl(
        private val mainService: MainService,
    ) : MainRepository
""".trimIndent()

private fun hilt(packageName: String) = """
    package $packageName.data.repository
            
    import $packageName.domain.repository.MainRepository
    import javax.inject.Inject
            
    class MainRepositoryImpl @Inject constructor() : MainRepository
""".trimIndent()

private fun empty(packageName: String) = """
    package $packageName.data.repository
            
    import $packageName.domain.repository.MainRepository
            
    class MainRepositoryImpl : MainRepository
""".trimIndent()
