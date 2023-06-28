package cn.leo.lint.checker

import com.android.ide.common.resources.ResourceRepository
import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UElement
import org.jetbrains.uast.ULiteralExpression
import java.util.regex.Pattern


class HardcodedTextDetector : Detector(), Detector.UastScanner {

    companion object {
        // 注册该检查器
        val ISSUE = Issue.create(
            "HardcodedText",
            "Avoid using hardcoded texts",
            "Hardcoded texts make it difficult to maintain and localize the app.",
            Category.CORRECTNESS, 5, Severity.WARNING,
            Implementation(HardcodedTextDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
    override fun getApplicableUastTypes(): List<Class<out UElement?>> {
        return listOf(ULiteralExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitLiteralExpression(expression: ULiteralExpression) {
                handleExpression(context, expression)
            }
        }
    }

    private fun handleExpression(context: JavaContext, expression: ULiteralExpression) {
        val value :String? = expression.value as? String
        if (value!= null && value.matches("([\\u4e00-\\u9fa5]+[^\"]*)".toRegex())) {
            val resourceName = findResourceName(context, value) ?: value.hashCode().toString()
            val suggestedValue = "ResUtils.getString(R.string.$resourceName)"
            val lintFix = LintFix.create()
                .replace()
                .text(expression.asSourceString())
                .with(suggestedValue)
                .range(context.getLocation(expression.uastParent ?: expression))
                .build()
            context.report(
                ISSUE,
                expression,
                context.getLocation(expression),
                "Avoid using hardcoded texts",
                lintFix
            )
        }
    }

    private fun findResourceName(context: JavaContext, value: String): String? {
        val client = context.client
        val resources: ResourceRepository =
            client.getResourceRepository(
                context.project,
                includeModuleDependencies = true,
                includeLibraries = false
            ) ?: return null
        val stringItems = resources.allResources
        for (item in stringItems) {
            if (item.source != null && item.resourceValue?.value == value) {
                return item.name
            }
        }
        return null
    }

}