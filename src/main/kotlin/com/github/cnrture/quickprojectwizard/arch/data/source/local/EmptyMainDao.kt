package com.github.cnrture.quickprojectwizard.arch.data.source.local

fun emptyMainDao(packageName: String) = """
package $packageName.data.source.local

import androidx.room.Dao

@Dao
interface MainDao
"""
