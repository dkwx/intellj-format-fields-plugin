package com.dk.pyramid.model;

import com.intellij.psi.PsiElement;

/**
 * @author : kai.dai
 * @date : 2019-11-04 14:35
 */
public class SelectSortModel {
    /**
     * 插入类型
     * 0 add
     * 1 add after
     * -1 add before
     */
    private InsertType insertType;

    private int endLine;
    private int startLine;

    private PsiElement locationElement;

    public SelectSortModel(int startLine, int endLine, InsertType insertType, PsiElement locationElement) {
        this.startLine = startLine;
        this.endLine = endLine;
        this.insertType = insertType;
        this.locationElement = locationElement;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public InsertType getInsertType() {
        return insertType;
    }

    public void setInsertType(InsertType insertType) {
        this.insertType = insertType;
    }

    public PsiElement getLocationElement() {
        return locationElement;
    }

    public void setLocationElement(PsiElement locationElement) {
        this.locationElement = locationElement;
    }

    public static enum InsertType {
        ADD,
        ADD_AFTER,
        ADD_BEFORE;
    }
}
