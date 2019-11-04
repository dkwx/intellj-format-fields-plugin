package com.dk.pyramid.assist;

import com.intellij.psi.PsiField;

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
}
