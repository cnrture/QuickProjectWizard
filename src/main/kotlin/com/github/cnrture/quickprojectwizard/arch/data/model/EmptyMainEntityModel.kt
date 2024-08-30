package com.github.cnrture.quickprojectwizard.arch.data.model

fun emptyMainEntityModel(packageName: String) = """
package $packageName.data.model
            
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MainEntityModel(
    @PrimaryKey
    val id: Int,
    val name: String,
)
""".trimIndent()
