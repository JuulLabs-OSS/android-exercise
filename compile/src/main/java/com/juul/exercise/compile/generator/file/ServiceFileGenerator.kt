package com.juul.exercise.compile.generator.file

import com.juul.exercise.compile.Parameters
import com.juul.exercise.compile.build
import com.juul.exercise.compile.generator.code.GetServiceExtrasClassCodeGenerator
import com.juul.exercise.compile.generator.code.ParameterizedIntentClassCodeGenerator
import com.juul.exercise.compile.generator.code.addFrom
import com.juul.exercise.compile.isSubtypeOf
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.asClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

class ServiceFileGenerator(
    private val environment: ProcessingEnvironment
) : FileGenerator {
    override fun isGeneratorFor(element: Element): Boolean = element is TypeElement &&
            element.isSubtypeOf(environment, "android.app.Service")

    override fun generate(element: Element): FileSpec {
        check(element is TypeElement)
        val parameters = Parameters(environment, element)
        val targetClass = element.asClassName()
        return FileSpec.build(targetClass.packageName, "${targetClass.simpleName}Exercise") {
            if (!element.modifiers.contains(Modifier.ABSTRACT)) {
                addFrom(ParameterizedIntentClassCodeGenerator(element, targetClass, parameters))
            }
            addFrom(GetServiceExtrasClassCodeGenerator(element, targetClass, parameters))
        }
    }
}
