package com.rocca23.kmp

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asTypeName
import org.koin.core.module.Module
import java.util.*
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
        val statement = elements.joinToString(
            prefix = "listOf(\n",
            postfix = "\n)",
            separator = ",\n",
            transform = {
                processingEnv.elementUtils.getPackageOf(it).toString() + "." +
                        it.simpleName.run {
                            substring(3 until indexOf('$')).decapitalize(Locale.ROOT)
                        }
            }
        )
        try {
            FileSpec.builder("org.koin.generated", "KoinModules")
                .addProperty(
                    PropertySpec.builder("koinModules", LIST.plusParameter(Module::class.asTypeName()))
                        .initializer(statement)
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