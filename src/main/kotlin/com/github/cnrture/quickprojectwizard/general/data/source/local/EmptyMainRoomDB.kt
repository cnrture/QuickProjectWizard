package com.github.cnrture.quickprojectwizard.general.data.source.local

fun emptyMainRoomDB(packageName: String) = """
package $packageName.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import $packageName.data.model.MainEntityModel

@Database(entities = [MainEntityModel::class], version = 1, exportSchema = false)
abstract class MainRoomDB : RoomDatabase() {
    abstract fun mainDao(): MainDao
}
"""
