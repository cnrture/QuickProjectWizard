package com.github.cnrture.quickprojectwizard.general.detekt

fun emptyDetektConfig() = """
console-reports:
  active: true
  exclude:
    - 'ProjectStatisticsReport'
    - 'ComplexityReport'
    - 'NotificationReport'
    - 'FileBasedFindingsReport'
    - 'LiteFindingsReport'

output-reports:
  active: true
  exclude:
    [ 'TxtOutputReport', 'XmlOutputReport', 'HtmlOutputReport' ]

formatting:
  FinalNewline:
    active: false
  NoWildcardImports:
    active: false

style:
  active: true
  WildcardImport:
    active: false
    excludes: ['**/test/**', '**/androidTest/**']
  MagicNumber:
    active: true
    excludes: ['**/theme/**']
  NewLineAtEndOfFile:
    active: false

naming:
  active: true
  FunctionNaming:
    active: true
    ignoreAnnotated: ['Composable']
  VariableNaming:
    active: true
    excludes: ['**/theme/**']
""".trimIndent()
