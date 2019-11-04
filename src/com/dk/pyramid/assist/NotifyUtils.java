package com.dk.pyramid.assist;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;

import java.awt.*;

/**
 * @author : kai.dai
 * @date : 2019-11-04 10:44
 */
public class NotifyUtils {

    public static void notifyInfo(Object anAction, Project project, String content) {
        Notification notification = new Notification(String.valueOf(anAction.hashCode()), "FormatFieldWithPyramidAction", content, NotificationType.INFORMATION);
        Notifications.Bus.notify(notification, project);
    }

    public static void notifyWarn(Object anAction, Project project, String content) {
        Notification notification = new Notification(String.valueOf(anAction.hashCode()), "FormatFieldWithPyramidAction", content, NotificationType.WARNING);
        Notifications.Bus.notify(notification, project);
    }

    public static void notifyError(Object anAction, Project project, String content) {
        Notification notification = new Notification(String.valueOf(anAction.hashCode()), "FormatFieldWithPyramidAction", content, NotificationType.ERROR);
        Notifications.Bus.notify(notification, project);
    }

    public static void showPopupBalloon(final Editor editor, final String result) {
        ApplicationManager.getApplication().invokeLater(() -> {
            JBPopupFactory factory = JBPopupFactory.getInstance();
            factory.createHtmlTextBalloonBuilder(result, null, new JBColor(new Color(186, 238, 186), new Color(73, 117, 73)), null)
                    .setFadeoutTime(3000)
                    .createBalloon()
                    .show(factory.guessBestPopupLocation(editor), Balloon.Position.below);
        });
    }
}
