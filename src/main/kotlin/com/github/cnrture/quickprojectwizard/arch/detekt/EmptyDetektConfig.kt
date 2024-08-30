package com.github.cnrture.quickprojectwizard.arch.detekt

fun emptyDetektConfig() = """
formatting:
  NoWildcardImports:
    active: false
  FinalNewline:
    active: false

style:
  WildcardImport:
    active: false
  MagicNumber:
    active: true
    excludes: ['**/theme/**']

naming:
  FunctionNaming:
    active: true
    ignoreAnnotated: ['Composable']
""".trimIndent()
