package com.rocca23.kmp

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import org.koin.core.module.Module
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.FilerException
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class KoinModuleProcessor : AbstractProcessor() {

    private var fileWritten = false

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (roundEnv.processingOver() || roundEnv.errorRaised() || fileWritten) {
            return true
        }
        val elements = roundEnv.getElementsAnnotatedWith(KoinModule::class.java)
        if (elements.isEmpty()) {
            return true
        }
        val statement = elements.joinToString(
            prefix = "listOf(\n",
            postfix = "\n)",
            separator = ",\n",
            transform = { element ->
                processingEnv.elementUtils.getPackageOf(element).toString() + "." +
                        element.simpleName.run {
                            substring(3 until indexOf('$')).replaceFirstChar { it.lowercase() }
                        }
            }
        )
        try {
            val modulePrefix = processingEnv.options?.get("kmp.module")
            val propertyName = if (modulePrefix != null) {
                "${modulePrefix}KoinModules"
            } else {
                "koinModules"
            }
            FileSpec.builder("org.koin.generated", "KoinModules")
                .addProperty(
                    PropertySpec.builder(propertyName, LIST.plusParameter(Module::class.asTypeName()))
                        .initializer(statement)
                        .addModifiers(KModifier.INTERNAL)
                        .build()
                )
                .build()
                .writeTo(processingEnv.filer)
            fileWritten = true
        } catch (e: FilerException) {
            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, e.localizedMessage)
        }
        return true
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(KoinModule::class.java.canonicalName)
    }
}