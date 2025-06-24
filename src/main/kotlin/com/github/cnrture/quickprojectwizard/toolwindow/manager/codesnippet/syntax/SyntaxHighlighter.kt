package com.github.cnrture.quickprojectwizard.toolwindow.manager.codesnippet.syntax

import com.github.cnrture.quickprojectwizard.toolwindow.manager.codesnippet.model.CodeSnippetTheme

object SyntaxHighlighter {

    fun highlight(code: String, language: String, theme: CodeSnippetTheme): String {
        // For now, return the code as-is since we handle highlighting in the preview component
        // This could be extended to use a proper syntax highlighting library in the future
        return code
    }

    fun getLanguageKeywords(language: String): List<String> {
        return when (language.lowercase()) {
            "kotlin" -> listOf(
                "abstract", "actual", "annotation", "as", "break", "by", "catch", "class",
                "companion", "const", "constructor", "continue", "crossinline", "data",
                "delegate", "do", "dynamic", "else", "enum", "expect", "external", "false",
                "final", "finally", "for", "fun", "get", "if", "import", "in", "infix",
                "init", "inline", "inner", "interface", "internal", "is", "lateinit",
                "noinline", "null", "object", "open", "operator", "out", "override",
                "package", "private", "protected", "public", "reified", "return", "sealed",
                "set", "super", "suspend", "tailrec", "this", "throw", "true", "try",
                "typealias", "typeof", "val", "var", "vararg", "when", "where", "while"
            )

            "java" -> listOf(
                "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
                "class", "const", "continue", "default", "do", "double", "else", "enum",
                "extends", "final", "finally", "float", "for", "goto", "if", "implements",
                "import", "instanceof", "int", "interface", "long", "native", "new", "null",
                "package", "private", "protected", "public", "return", "short", "static",
                "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while", "true", "false"
            )

            "javascript", "typescript" -> listOf(
                "abstract", "arguments", "await", "boolean", "break", "byte", "case", "catch",
                "char", "class", "const", "continue", "debugger", "default", "delete", "do",
                "double", "else", "enum", "eval", "export", "extends", "false", "final",
                "finally", "float", "for", "function", "goto", "if", "implements", "import",
                "in", "instanceof", "int", "interface", "let", "long", "native", "new",
                "null", "package", "private", "protected", "public", "return", "short",
                "static", "super", "switch", "synchronized", "this", "throw", "throws",
                "transient", "true", "try", "typeof", "var", "void", "volatile", "while",
                "with", "yield", "async"
            )

            "python" -> listOf(
                "False", "None", "True", "and", "as", "assert", "async", "await", "break",
                "class", "continue", "def", "del", "elif", "else", "except", "finally",
                "for", "from", "global", "if", "import", "in", "is", "lambda", "nonlocal",
                "not", "or", "pass", "raise", "return", "try", "while", "with", "yield"
            )

            "go" -> listOf(
                "break", "case", "chan", "const", "continue", "default", "defer", "else",
                "fallthrough", "for", "func", "go", "goto", "if", "import", "interface",
                "map", "package", "range", "return", "select", "struct", "switch", "type",
                "var", "bool", "byte", "complex64", "complex128", "error", "float32",
                "float64", "int", "int8", "int16", "int32", "int64", "rune", "string",
                "uint", "uint8", "uint16", "uint32", "uint64", "uintptr", "true", "false",
                "iota", "nil", "append", "cap", "close", "complex", "copy", "delete",
                "imag", "len", "make", "new", "panic", "print", "println", "real", "recover"
            )

            "rust" -> listOf(
                "as", "async", "await", "break", "const", "continue", "crate", "dyn", "else",
                "enum", "extern", "false", "fn", "for", "if", "impl", "in", "let", "loop",
                "match", "mod", "move", "mut", "pub", "ref", "return", "self", "Self",
                "static", "struct", "super", "trait", "true", "type", "unsafe", "use",
                "where", "while", "abstract", "become", "box", "do", "final", "macro",
                "override", "priv", "typeof", "unsized", "virtual", "yield", "try"
            )

            else -> emptyList()
        }
    }
}