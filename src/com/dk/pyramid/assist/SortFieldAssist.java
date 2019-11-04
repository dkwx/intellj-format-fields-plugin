package com.dk.pyramid.assist;

import com.dk.pyramid.model.SelectSortModel;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiExtensibleClass;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author : kai.dai
 * @date : 2019-11-04 10:42
 */
public class SortFieldAssist {

    /**
     * 排序前后是否一致
     *
     * @param list   排序后
     * @param fields 排序前
     */
    public static boolean isSameAfterSort(List<PsiField> list, PsiField[] fields) {
        for (int i = 0; i < fields.length; i++) {
            if (!list.get(i).equals(fields[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取排序依据
     * 目前根据最后一行的长度排序，并且去除首位空白
     *
     * @param f 属性
     * @return 权重
     */
    public static int getSortCondition(PsiField f) {
        String[] strs = f.getText().split("\n");
        return strs[strs.length - 1].trim().length();
    }

    @NotNull
    public static SelectSortModel getSelectSortModel(PsiClass currentClass, int start, int end, List<PsiField> sortFieldList) {
        SelectSortModel sortModel;
        PsiField location = null;
        // 先从头往后找
        int min = Integer.MAX_VALUE;
        for (PsiField psiField : sortFieldList) {
            int temp = start - psiField.getTextRange().getEndOffset();
            if (temp > 0 && temp < min) {
                location = psiField;
                min = temp;
            }
        }
        // 如果没找到，从后往前找
        if (location == null) {
            min = Integer.MAX_VALUE;
            for (PsiField psiField : sortFieldList) {
                int temp = psiField.getTextRange().getStartOffset() - end;
                if (temp > 0 && temp < min) {
                    location = psiField;
                    min = temp;
                }
            }
            if (location == null) {
                PsiMethod[] psiMethods = currentClass.getAllMethods();
                if (ArrayUtils.isEmpty(psiMethods)) {
                    // 如果没有属性，则找第一个方法，加在它前面
                    sortModel = new SelectSortModel(start, end, SelectSortModel.InsertType.ADD, null);
                } else {
                    List<PsiMethod> psiMethodList = Arrays.asList(psiMethods);
                    if (currentClass instanceof PsiExtensibleClass) {
                        psiMethodList = ((PsiExtensibleClass) currentClass).getOwnMethods();
                    }
                    if (CollectionUtils.isNotEmpty(psiMethodList)) {
                        sortModel = new SelectSortModel(start, end, SelectSortModel.InsertType.ADD_BEFORE, psiMethodList.get(0));
                    } else {
                        sortModel = new SelectSortModel(start, end, SelectSortModel.InsertType.ADD, null);
                    }
                }
            } else {
                sortModel = new SelectSortModel(start, end, SelectSortModel.InsertType.ADD_BEFORE, location);
            }
        } else {
            sortModel = new SelectSortModel(start, end, SelectSortModel.InsertType.ADD_AFTER, location);
        }
        return sortModel;
    }
}
