package com.github.cnrture.quickprojectwizard.projectwizard.util

import com.intellij.ide.BrowserUtil
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

object NotificationUtil {
    fun showInfo(title: String, message: String) {
        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup("QPW Notification Group")
            .createNotification(
                title = title,
                content = message,
                type = NotificationType.INFORMATION,
            )
        notification.addAction(
            object : AnAction("Contact Developer") {
                override fun actionPerformed(e: AnActionEvent) {
                    BrowserUtil.browse("https://bento.me/canerture")
                }
            }
        )
        notification.addAction(
            object : AnAction("Open Plugin Page") {
                override fun actionPerformed(e: AnActionEvent) {
                    BrowserUtil.browse("https://plugins.jetbrains.com/plugin/25221-quickprojectwizard?noRedirect=true")
                }
            }
        )
        notification.notify(null)
    }
}