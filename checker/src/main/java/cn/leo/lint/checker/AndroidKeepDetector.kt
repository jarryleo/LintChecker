package cn.leo.lint.checker

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.kotlin.KotlinUClass

class AndroidKeepDetector : Detector(), SourceCodeScanner {

    companion object {

        private const val tips = "data class must use annotation @Keep \n" +
                " 严重提醒，数据模型必须打上注解: @Keep "

        // 此处便是上面用到的 ISSUE，很容易理解相关参数的含义，此处不再赘述
        val ISSUE = Issue.create(
            "AndroidKeepLint",
            tips,
            tips,
            Category.LINT,
            9,
            Severity.ERROR,
            Implementation(AndroidKeepDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return DataClassVisitor(context)
    }

    class DataClassVisitor(private val context: JavaContext) : UElementHandler() {
        override fun visitClass(node: UClass) {
            if (node !is KotlinUClass) return
            val containsEqualHashCode = node.methods
                .map { it.name }
                .containsAll(listOf("equals", "hashCode"))
            if (containsEqualHashCode) {
                val hasAnnotation = node.hasAnnotation("androidx.annotation.Keep")
                if (!hasAnnotation) {
                    context.report(
                        ISSUE,
                        node,
                        context.getNameLocation(node),
                        tips,
                        LintFix.create()
                            .name("Add @Keep")
                            .replace()
                            .pattern("class ${node.name}")
                            .with("@Keep \n class ${node.name}")
                            .build()
                    )
                }
            }
        }

    }
}