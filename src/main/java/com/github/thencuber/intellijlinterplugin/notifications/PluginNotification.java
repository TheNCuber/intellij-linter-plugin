package com.github.thencuber.intellijlinterplugin.notifications;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;

public class PluginNotification extends Notification {
    public PluginNotification(boolean isSuccessful, String message) {
        super(NotificationGroupManager.getInstance().getNotificationGroup("X-Platform Java Linter").getDisplayId(), "-", "-", (isSuccessful ? NotificationType.INFORMATION : NotificationType.ERROR));
        if(isSuccessful) {
            setIcon(AllIcons.RunConfigurations.ToolbarPassed);
            setTitle("X-Platform Java Linter update successful");
            setContent(message);
        }
        else {
            setIcon(AllIcons.RunConfigurations.ToolbarError);
            setTitle("X-Platform Java Linter update failed");
            setContent(message);
        }
    }
    public PluginNotification(boolean isSuccessful) {
        super(NotificationGroupManager.getInstance().getNotificationGroup("X-Platform Java Linter").getDisplayId(), "-", "-", (isSuccessful ? NotificationType.INFORMATION : NotificationType.ERROR));
        if(isSuccessful) {
            setIcon(AllIcons.RunConfigurations.ToolbarPassed);
            setTitle("X-Platform Java Linter update successful");
            setContent("X-Platform Java Linter successfully updated the linting rules.");
        }
        else {
            setIcon(AllIcons.RunConfigurations.ToolbarError);
            setTitle("X-Platform Java Linter update failed");
            setContent("X-Platform Java Linter was unable to update the linting rules.");
        }
    }
}
