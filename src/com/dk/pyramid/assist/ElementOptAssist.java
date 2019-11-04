package com.dk.pyramid.assist;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;

/**
 * @author : kai.dai
 * @date : 2019-11-04 10:42
 */
public class ElementOptAssist {
    public static void deleteElement(PsiElement element) {
        element.delete();
    }

    public static void addElement(PsiClass currentClass, PsiElement element) {
        currentClass.add(element);
    }

    public static void addAfterElement(PsiClass currentClass, PsiElement addElement, PsiElement locationElement) {
        currentClass.addAfter(addElement, locationElement);
    }

    public static void addBeforeElement(PsiClass currentClass, PsiElement addElement, PsiElement locationElement) {
        currentClass.addBefore(addElement, locationElement);
    }

}