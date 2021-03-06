package com.juul.exercise

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ExerciseProcessorActivityTests : ExerciseProcessorTests() {

    @Test
    fun `test activity generation with no extras`() {
        val result = compile(
            kotlin(
                "NoExtrasActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import com.juul.exercise.annotations.Exercise
                
                @Exercise
                class NoExtrasActivity : Activity()
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = result.getGeneratedFile("NoExtrasActivityExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.String

            fun bundleForNoExtrasActivity(context: Context): Bundle = bundleOf()

            fun bundleForNoExtrasActivity(packageName: String): Bundle = bundleOf()

            class NoExtrasActivityIntent : Intent {
              constructor(context: Context) : super() {
                setClassName(context, "com.juul.exercise.tests.NoExtrasActivity")
              }
            
              constructor(packageName: String) : super() {
                setClassName(packageName, "com.juul.exercise.tests.NoExtrasActivity")
              }
            }
            
            class NoExtrasActivityParams(
              private val instance: NoExtrasActivity
            )
            
            val NoExtrasActivity.extras: NoExtrasActivityParams
              get() = NoExtrasActivityParams(this)
            """
        )
    }

    @Test
    fun `test activity generation with abstract superclass and subclass`() {
        val result = compile(
            kotlin(
                "SuperclassActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("fromSuperclass", Int::class))
                abstract class SuperclassActivity : Activity()
                """
            ),
            kotlin(
                "SubclassActivity.kt",
                """
                package com.juul.exercise.tests
                
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("fromSubclass", String::class))
                class SubclassActivity : SuperclassActivity()
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val superclassFile = result.getGeneratedFile("SuperclassActivityExercise.kt")
        assertThat(superclassFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import kotlin.Int
            
            class SuperclassActivityParams(
              private val instance: SuperclassActivity
            ) {
              val fromSuperclass: Int
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.fromSuperclass") as Int
            }
            
            val SuperclassActivity.extras: SuperclassActivityParams
              get() = SuperclassActivityParams(this)
            """
        )

        val subclassFile = result.getGeneratedFile("SubclassActivityExercise.kt")
        assertThat(subclassFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            
            fun bundleForSubclassActivity(
              context: Context,
              fromSuperclass: Int,
              fromSubclass: String
            ): Bundle = bundleOf(
              "${"$"}{context.packageName}.fromSuperclass" to fromSuperclass,
              "${"$"}{context.packageName}.fromSubclass" to fromSubclass
            )

            fun bundleForSubclassActivity(
              packageName: String,
              fromSuperclass: Int,
              fromSubclass: String
            ): Bundle = bundleOf(
              "${"$"}{packageName}.fromSuperclass" to fromSuperclass,
              "${"$"}{packageName}.fromSubclass" to fromSubclass
            )

            class SubclassActivityIntent : Intent {
              constructor(
                context: Context,
                fromSuperclass: Int,
                fromSubclass: String
              ) : super() {
                setClassName(context, "com.juul.exercise.tests.SubclassActivity")
                replaceExtras(bundleForSubclassActivity(
                  context.packageName,
                  fromSuperclass,
                  fromSubclass
                ))
              }
            
              constructor(
                packageName: String,
                fromSuperclass: Int,
                fromSubclass: String
              ) : super() {
                setClassName(packageName, "com.juul.exercise.tests.SubclassActivity")
                replaceExtras(bundleForSubclassActivity(
                  packageName,
                  fromSuperclass,
                  fromSubclass
                ))
              }
            }
            
            class SubclassActivityParams(
              private val instance: SubclassActivity
            ) {
              val fromSuperclass: Int
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.fromSuperclass") as Int
            
              val fromSubclass: String
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.fromSubclass") as String
            }
            
            val SubclassActivity.extras: SubclassActivityParams
              get() = SubclassActivityParams(this)
            """
        )
    }

    @Test
    fun `test activity generation with generics`() {
        val result = compile(
            kotlin(
                "ListActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("listOfInt", List::class, Int::class))
                class ListActivity : Activity()
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = result.getGeneratedFile("ListActivityExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            import kotlin.Suppress
            import kotlin.collections.List
            
            fun bundleForListActivity(context: Context, listOfInt: List<Int>): Bundle = bundleOf(
              "${"$"}{context.packageName}.listOfInt" to listOfInt
            )

            fun bundleForListActivity(packageName: String, listOfInt: List<Int>): Bundle = bundleOf(
              "${"$"}{packageName}.listOfInt" to listOfInt
            )
 
            class ListActivityIntent : Intent {
              constructor(context: Context, listOfInt: List<Int>) : super() {
                setClassName(context, "com.juul.exercise.tests.ListActivity")
                replaceExtras(bundleForListActivity(
                  context.packageName,
                  listOfInt
                ))
              }
            
              constructor(packageName: String, listOfInt: List<Int>) : super() {
                setClassName(packageName, "com.juul.exercise.tests.ListActivity")
                replaceExtras(bundleForListActivity(
                  packageName,
                  listOfInt
                ))
              }
            }
            
            class ListActivityParams(
              private val instance: ListActivity
            ) {
              val listOfInt: List<Int>
                @Suppress("UNCHECKED_CAST")
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.listOfInt") as List<Int>
            }
            
            val ListActivity.extras: ListActivityParams
              get() = ListActivityParams(this)
            """
        )
    }

    @Test
    fun `test activity generation with optionals`() {
        val result = compile(
            kotlin(
                "OptionalsActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("optionalInt", Int::class, optional = true))
                class OptionalsActivity : Activity()
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = result.getGeneratedFile("OptionalsActivityExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            
            fun bundleForOptionalsActivity(context: Context, optionalInt: Int? = null): Bundle = bundleOf(
              "${"$"}{context.packageName}.optionalInt" to optionalInt
            )
            
            fun bundleForOptionalsActivity(packageName: String, optionalInt: Int? = null): Bundle = bundleOf(
              "${"${'$'}"}{packageName}.optionalInt" to optionalInt
            )
            
            class OptionalsActivityIntent : Intent {
              constructor(context: Context, optionalInt: Int? = null) : super() {
                setClassName(context, "com.juul.exercise.tests.OptionalsActivity")
                replaceExtras(bundleForOptionalsActivity(
                  context.packageName,
                  optionalInt
                ))
              }
            
              constructor(packageName: String, optionalInt: Int? = null) : super() {
                setClassName(packageName, "com.juul.exercise.tests.OptionalsActivity")
                replaceExtras(bundleForOptionalsActivity(
                  packageName,
                  optionalInt
                ))
              }
            }
            
            class OptionalsActivityParams(
              private val instance: OptionalsActivity
            ) {
              val optionalInt: Int?
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.optionalInt") as Int?
            
              fun optionalInt(default: Int): Int =
                  (instance.intent?.extras?.get("${"$"}{instance.packageName}.optionalInt") as? Int?) ?: default
            }
            
            val OptionalsActivity.extras: OptionalsActivityParams
              get() = OptionalsActivityParams(this)
            """
        )
    }

    @Test
    fun `test activity generation with stubs`() {
        val result = compile(
            kotlin(
                "StubbedActivityStub.kt",
                """
                package com.juul.exercise.tests
                
                import com.juul.exercise.annotations.AsStub
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("stubbed", Int::class))
                @AsStub("com.juul.exercise.tests", "StubbedActivity")
                object StubbedActivityStub
                """
            ),
            kotlin(
                "StubbedActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.FromStub
                
                @Exercise
                @FromStub(StubbedActivityStub::class)
                class StubbedActivity : Activity()
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val stubFile = result.getGeneratedFile("StubbedActivityExerciseStubs.kt")
        assertThat(stubFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            
            fun bundleForStubbedActivity(context: Context, stubbed: Int): Bundle = bundleOf(
              "${"$"}{context.packageName}.stubbed" to stubbed
            )
            
            fun bundleForStubbedActivity(packageName: String, stubbed: Int): Bundle = bundleOf(
              "${"$"}{packageName}.stubbed" to stubbed
            )
            
            class StubbedActivityIntent : Intent {
              constructor(context: Context, stubbed: Int) : super() {
                setClassName(context, "com.juul.exercise.tests.StubbedActivity")
                replaceExtras(bundleForStubbedActivity(
                  context.packageName,
                  stubbed
                ))
              }
            
              constructor(packageName: String, stubbed: Int) : super() {
                setClassName(packageName, "com.juul.exercise.tests.StubbedActivity")
                replaceExtras(bundleForStubbedActivity(
                  packageName,
                  stubbed
                ))
              }
            }
            """
        )

        val paramsFile = result.getGeneratedFile("StubbedActivityExercise.kt")
        assertThat(paramsFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import kotlin.Int
            
            class StubbedActivityParams(
              private val instance: StubbedActivity
            ) {
              val stubbed: Int
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.stubbed") as Int
            }
            
            val StubbedActivity.extras: StubbedActivityParams
              get() = StubbedActivityParams(this)
            """
        )
    }

    @Test
    fun `test activity generation with parceler`() {
        val result = compile(
            kotlin(
                "ParcelerActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import android.os.Parcel
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                import kotlinx.android.parcel.Parceler
                
                data class ThirdPartyType(val value: String)
                
                object ThirdPartyTypeParceler : Parceler<ThirdPartyType> {
                    override fun create(parcel: Parcel) = ThirdPartyType(checkNotNull(parcel.readString()))
                    
                    override fun ThirdPartyType.write(parcel: Parcel, flags: Int) {
                        parcel.writeString(this.value)
                    }
                }
                
                @Exercise(
                    Extra("requiredValue", ThirdPartyType::class, parceler = ThirdPartyTypeParceler::class),
                    Extra("optionalValue", ThirdPartyType::class, optional = true, parceler = ThirdPartyTypeParceler::class)
                )
                class ParcelerActivity : Activity()
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = result.getGeneratedFile("ParcelerActivityExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests

            import android.content.Context
            import android.content.Intent
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import com.juul.exercise.runtime.createFromMarshalledBytes
            import com.juul.exercise.runtime.createFromMarshalledBytesOrNull
            import com.juul.exercise.runtime.writeToMarshalledBytes
            import com.juul.exercise.runtime.writeToMarshalledBytesOrNull
            import kotlin.ByteArray
            import kotlin.String
            
            fun bundleForParcelerActivity(
              context: Context,
              requiredValue: ThirdPartyType,
              optionalValue: ThirdPartyType? = null
            ): Bundle = bundleOf(
              "${"$"}{context.packageName}.requiredValue" to
                  ThirdPartyTypeParceler.writeToMarshalledBytes(requiredValue),
              "${"$"}{context.packageName}.optionalValue" to
                  ThirdPartyTypeParceler.writeToMarshalledBytesOrNull(optionalValue)
            )
            
            fun bundleForParcelerActivity(
              packageName: String,
              requiredValue: ThirdPartyType,
              optionalValue: ThirdPartyType? = null
            ): Bundle = bundleOf(
              "${"$"}{packageName}.requiredValue" to ThirdPartyTypeParceler.writeToMarshalledBytes(requiredValue),
              "${"$"}{packageName}.optionalValue" to
                  ThirdPartyTypeParceler.writeToMarshalledBytesOrNull(optionalValue)
            )

            class ParcelerActivityIntent : Intent {
              constructor(
                context: Context,
                requiredValue: ThirdPartyType,
                optionalValue: ThirdPartyType? = null
              ) : super() {
                setClassName(context, "com.juul.exercise.tests.ParcelerActivity")
                replaceExtras(bundleForParcelerActivity(
                  context.packageName,
                  requiredValue,
                  optionalValue
                ))
              }
            
              constructor(
                packageName: String,
                requiredValue: ThirdPartyType,
                optionalValue: ThirdPartyType? = null
              ) : super() {
                setClassName(packageName, "com.juul.exercise.tests.ParcelerActivity")
                replaceExtras(bundleForParcelerActivity(
                  packageName,
                  requiredValue,
                  optionalValue
                ))
              }
            }
            
            class ParcelerActivityParams(
              private val instance: ParcelerActivity
            ) {
              val requiredValue: ThirdPartyType
                get() {
                  val data = instance.intent?.extras?.get("${"$"}{instance.packageName}.requiredValue") as ByteArray
                  return ThirdPartyTypeParceler.createFromMarshalledBytes(data)
                }
            
              val optionalValue: ThirdPartyType?
                get() {
                  val data = instance.intent?.extras?.get("${"$"}{instance.packageName}.optionalValue") as ByteArray?
                  return ThirdPartyTypeParceler.createFromMarshalledBytesOrNull(data)
                }
            
              fun optionalValue(default: ThirdPartyType): ThirdPartyType {
                val data = instance.intent?.extras?.get("${"$"}{instance.packageName}.optionalValue") as ByteArray?
                return ThirdPartyTypeParceler.createFromMarshalledBytesOrNull(data) ?: default
              }
            }
            
            val ParcelerActivity.extras: ParcelerActivityParams
              get() = ParcelerActivityParams(this)
            """
        )
    }
}
