package com.github.cnrture.quickprojectwizard.projectwizard.xmlarch.ui

import com.github.cnrture.quickprojectwizard.data.DILibrary

fun emptyActivityXML(
    packageName: String,
    selectedDILibrary: DILibrary,
    dataDiDomainPresentationUiPackages: Boolean,
): String {
    return when {
        !dataDiDomainPresentationUiPackages -> withoutHilt(packageName)
        selectedDILibrary == DILibrary.Hilt -> hilt(packageName)
        selectedDILibrary == DILibrary.Koin -> withoutHilt(packageName)
        else -> withoutHilt(packageName)
    }
}

private fun hilt(packageName: String) = """
package $packageName.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import $packageName.R
import $packageName.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
""".trimIndent()

private fun withoutHilt(packageName: String) = """
package $packageName.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import $packageName.R
import $packageName.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
""".trimIndent()
