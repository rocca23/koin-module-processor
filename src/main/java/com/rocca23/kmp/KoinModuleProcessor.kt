package com.rocca23.kmp

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import java.util.*

class KoinModuleProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(KoinModule::class.qualifiedName!!)
        if (!symbols.iterator().hasNext()) {
            logger.info("No annotated symbols found")
            return emptyList()
        }
        val visitor = KoinModuleProcessorVisitor()
        val dependencies = mutableSetOf<KSFile>()
        symbols.iterator().forEachRemaining { annotatedSymbol ->
            annotatedSymbol.containingFile?.let {
                dependencies += it
            }
            annotatedSymbol.accept(visitor, Unit)
        }
        logger.info("found ${dependencies.size} dependencies")
        val prefix = options["kmp.prefix"] ?: ""
        val packageName = "org.koin.generated"
        val fileName = "${prefix.capitalize()}KoinModules"
        val propertyName = if (prefix.isBlank()) "koinModules" else "${prefix}KoinModules"
        val statement = visitor.koinModuleNames.joinToString(
            prefix = "listOf(\n    ",
            postfix = "\n)",
            separator = ",\n    ",
        )
        val fileOutputStream = codeGenerator.createNewFile(
            dependencies = Dependencies(true, *dependencies.toTypedArray()),
            packageName = packageName,
            fileName = fileName
        )
        logger.info("Created file $packageName.$fileName.kt")
        fileOutputStream.use {
            it.write("package $packageName\n\n".toByteArray())
            it.write("internal val $propertyName = $statement".toByteArray())
        }
        logger.info("Processing ended")
        return emptyList()
    }

    private fun String.capitalize(): String {
        return replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}