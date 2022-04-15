package cn.leo.lint.checker

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue

/**
 * @author : ling luo
 * @date : 2022/4/15
 * @description : Android keep reg
 */
class AndroidKeepRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(AndroidKeepDetector.ISSUE)
}