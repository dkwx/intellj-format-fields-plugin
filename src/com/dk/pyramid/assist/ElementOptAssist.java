package com.dk.pyramid.assist;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;

/**
 * @author : kai.dai
 * @date : 2019-11-04 10:42
 */
public class ElementOptAssist {
    public static void deleteElement(Project project, PsiElement element) {
        WriteCommandAction.runWriteCommandAction(project, () -> element.delete());
    }

    public static void addElement(Project project, PsiClass currentClass, PsiElement element) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            currentClass.add(element);
        });
    }

    public static void addAfterElement(Project project, PsiClass currentClass, PsiElement addElement, PsiElement locationElement) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            currentClass.addAfter(addElement, locationElement);
        });
    }

    public static void addBeforeElement(Project project, PsiClass currentClass, PsiElement addElement, PsiElement locationElement) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            currentClass.addBefore(addElement, locationElement);
        });
    }
}
