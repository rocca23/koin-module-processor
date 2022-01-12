package com.rocca23.kmp

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid

class KoinModuleProcessorVisitor : KSVisitorVoid() {

    val koinModuleNames = mutableListOf<String>()

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        property.qualifiedName?.let {
            koinModuleNames += it.asString()
        }
    }
}