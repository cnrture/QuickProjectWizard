package com.github.cnrture.quickprojectwizard.projectwizard.xmlarch.ui

import com.github.cnrture.quickprojectwizard.data.DILibrary

fun emptyMainFragment(packageName: String, screen: String, selectedDILibrary: DILibrary): String {
    return when (selectedDILibrary) {
        DILibrary.Hilt -> hilt(packageName, screen)
        DILibrary.Koin -> koin(packageName, screen)
        DILibrary.None -> withoutDI(packageName, screen)
    }
}

private fun hilt(packageName: String, screen: String) = """
package $packageName.ui.${screen.lowercase()}

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import $packageName.common.collect
import $packageName.databinding.Fragment${screen}Binding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ${screen}Fragment : Fragment() {

    private var _binding: Fragment${screen}Binding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<${screen}ViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = Fragment${screen}Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {}

        collectState()
    }

    private fun collectState() {
        with(binding) {
            viewModel.uiState.collect(viewLifecycleOwner) { state ->

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
"""

private fun koin(packageName: String, screen: String) = """
package $packageName.ui.${screen.lowercase()}

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import $packageName.common.collect
import $packageName.databinding.Fragment${screen}Binding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ${screen}Fragment : Fragment() {

    private var _binding: Fragment${screen}Binding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<${screen}ViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = Fragment${screen}Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {}

        collectState()
    }

    private fun collectState() {
        with(binding) {
            viewModel.uiState.collect(viewLifecycleOwner) { state ->

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
"""

private fun withoutDI(packageName: String, screen: String) = """
package $packageName.ui.${screen.lowercase()}

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import $packageName.common.collect
import $packageName.databinding.Fragment${screen}Binding

class ${screen}Fragment : Fragment() {

    private var _binding: Fragment${screen}Binding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<${screen}ViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = Fragment${screen}Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {}

        collectState()
    }

    private fun collectState() {
        with(binding) {
            viewModel.uiState.collect(viewLifecycleOwner) { state ->

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
"""
