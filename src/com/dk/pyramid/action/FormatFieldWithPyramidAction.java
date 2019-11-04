package com.dk.pyramid.action;

import com.dk.pyramid.assist.ElementOptAssist;
import com.dk.pyramid.assist.NotifyUtils;
import com.dk.pyramid.assist.SortFieldAssist;
import com.dk.pyramid.model.SelectSortModel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author kai.dai
 */
public class FormatFieldWithPyramidAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (null == editor) {
            NotifyUtils.notifyWarn(e, project, "当前环境无法格式化；\n The current environment cannot be formatted");
            return;
        }
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (null == psiFile) {
            NotifyUtils.notifyWarn(this, project, "无效操作(文件空); \n Invalid operation (empty file)");
            return;
        }
        if (!(psiFile instanceof PsiJavaFile)) {
            NotifyUtils.notifyWarn(this, project, "非java文件; \n Not a Java file");
            return;
        }
        SelectionModel selectionModel = editor.getSelectionModel();
        try {
            if (StringUtils.isBlank(selectionModel.getSelectedText())) {
                // 全部格式化
                formatAllClasses(project, editor);
            } else {
                // 格式化选中的
                int start = selectionModel.getSelectionStart();
                int end = selectionModel.getSelectionEnd();
                formatAllClasses(project, editor, start, end);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            NotifyUtils.notifyError(e, project, "发生异常 exception:" + ex.toString());
        }

    }

    private void formatAllClasses(Project project, Editor editor) {
        List<PsiClass> allClassList = getAllPsiClasses(project, editor);
        int sum = allClassList.stream().mapToInt((c) -> sortPsiClass(project, c)).sum();
        if (sum == 0) {
            NotifyUtils.showPopupBalloon(editor, "原文件已经是排好序的了。\n The original file is already sorted");
            return;
        }
        NotifyUtils.showPopupBalloon(editor, "文件排序完成。\n File sorting complete");
    }

    private void formatAllClasses(Project project, Editor editor, int start, int end) {
        List<PsiClass> allClassList = getAllPsiClasses(project, editor);
        int sum = allClassList.stream().mapToInt((c) -> sortPsiClass(project, c, start, end)).sum();
        if (sum == 0) {
            NotifyUtils.showPopupBalloon(editor, "原选中内容已经排好序了。\n The original selection is sorted");
            return;
        }
        NotifyUtils.showPopupBalloon(editor, "选中内容排序完成。\n The sorting of selected content is complete");
    }

    @NotNull
    private List<PsiClass> getAllPsiClasses(Project project, Editor editor) {
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        PsiJavaFile javaFile = (PsiJavaFile) psiFile;
        PsiClass[] classes = javaFile.getClasses();
        List<PsiClass> allClassList = Lists.newArrayList();
        Arrays.stream(classes).forEach((c) -> {
            allClassList.add(c);
            if (!ArrayUtils.isEmpty(c.getAllInnerClasses())) {
                allClassList.addAll(Arrays.asList(c.getAllInnerClasses()));
            }
        });
        return allClassList;
    }


    private int sortPsiClass(Project project, PsiClass currentClass) {
        PsiField[] fields = currentClass.getAllFields();
        if (ArrayUtils.isEmpty(fields)) {
            return 0;
        }
        // List的底层结构数组直接被替换，所以排序后顺序会变
        List<PsiField> sortFieldList = Arrays.asList(fields);
        sortFieldList = filterBlankField(sortFieldList);
        // 重新给其赋值
        return sortByFields(project, currentClass, sortFieldList, null);
    }

    private int sortPsiClass(Project project, PsiClass currentClass, int start, int end) {
        PsiField[] fields = currentClass.getAllFields();
        if (ArrayUtils.isEmpty(fields)) {
            return 0;
        }
        // List的底层结构数组直接被替换，所以排序后顺序会变
        List<PsiField> sortFieldList = Arrays.asList(fields);
        sortFieldList = filterBlankField(sortFieldList);
        if (CollectionUtils.isEmpty(sortFieldList)) {
            return 0;
        }
        // 获取排序依据
        SelectSortModel sortModel = getSelectSortModel(currentClass, start, end, sortFieldList);
        sortFieldList.stream().mapToInt((f) -> (start - f.getTextRange().getEndOffset()));

        sortFieldList = sortFieldList.stream().
                filter((f) -> f.getTextRange().getEndOffset() <= end && f.getTextRange().getEndOffset() >= start)
                .collect(Collectors.toList());

        return sortByFields(project, currentClass, sortFieldList, sortModel);
    }

    @NotNull
    private SelectSortModel getSelectSortModel(PsiClass currentClass, int start, int end, List<PsiField> sortFieldList) {
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
                // 如果没有属性，则找第一个方法，加在它前面
                if (!ArrayUtils.isEmpty(psiMethods)) {
                    sortModel = new SelectSortModel(start, end, SelectSortModel.InsertType.ADD_BEFORE, psiMethods[0]);
                } else {
                    sortModel = new SelectSortModel(start, end, SelectSortModel.InsertType.ADD, location);
                }
                sortModel = new SelectSortModel(start, end, SelectSortModel.InsertType.ADD, location);
            } else {
                sortModel = new SelectSortModel(start, end, SelectSortModel.InsertType.ADD_BEFORE, location);
            }
        } else {
            sortModel = new SelectSortModel(start, end, SelectSortModel.InsertType.ADD_AFTER, location);
        }
        return sortModel;
    }


    private int sortByFields(Project project, PsiClass currentClass, List<PsiField> sortFieldList, SelectSortModel sortModel) {
        if (CollectionUtils.isEmpty(sortFieldList)) {
            return 0;
        }
        PsiField[] fields = sortFieldList.toArray(new PsiField[0]);
        Collections.sort(sortFieldList, Comparator.comparingInt((f) -> {
            // 以最后一行的长度排序
            return SortFieldAssist.getSortCondition(f);
        }));
        if (SortFieldAssist.isSameAfterSort(sortFieldList, fields)) {
            return 0;
        }
        List<PsiElement> copySortFieldList = sortFieldList.stream().map(PsiField::copy).collect(Collectors.toList());
        // 删除旧的
        sortFieldList.forEach((f) -> ElementOptAssist.deleteElement(project, f));
        if (null == sortModel || sortModel.getInsertType() == SelectSortModel.InsertType.ADD) {
            // 添加新的
            copySortFieldList.forEach((f) -> ElementOptAssist.addElement(project, currentClass, f));
        } else {
            // 如果是往后追加，则倒序追加
            if (sortModel.getInsertType() == SelectSortModel.InsertType.ADD_AFTER) {
                for (int i = copySortFieldList.size() - 1; i >= 0; i--) {
                    ElementOptAssist.addAfterElement(project, currentClass, copySortFieldList.get(i), sortModel.getLocationElement());
                }
            } else {
                // 往前追加，则正序
                copySortFieldList.forEach((f) -> ElementOptAssist.addBeforeElement(project, currentClass, f, sortModel.getLocationElement()));
            }
        }

        return 1;
    }


    @NotNull
    private List<PsiField> filterBlankField(List<PsiField> sortFieldList) {
        // 过滤@Slf4j注解生成的属性
        return sortFieldList.stream().filter((f) -> StringUtils.isNotBlank(f.getText())).collect(Collectors.toList());
    }

}
