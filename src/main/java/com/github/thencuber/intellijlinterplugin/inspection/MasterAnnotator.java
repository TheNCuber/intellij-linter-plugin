// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.github.thencuber.intellijlinterplugin.inspection;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class MasterAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        // Read config
        ArrayList<AnnotatorInformation> myAnnotators = readConfig(element.getProject());

        // Ensure we have at least one annotator
        if (myAnnotators.size() < 1) {
            return;
        }

        // Ensure the Psi Element is an expression
        if (!(element instanceof PsiLiteralExpression)) {
            return;
        }

        // Ensure the Psi element contains a string
        PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
        String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
        if (value == null) {
            return;
        }

        // Check if one of the annotators matches
        boolean foundMatch = false;
        AnnotatorInformation matchingAnnotator = null;
        for (AnnotatorInformation currentAnnotator : myAnnotators) {
            if (currentAnnotator.checkMatch(value)) {
                foundMatch = true;
                matchingAnnotator = currentAnnotator;
                break;
            }
        }
        if(!foundMatch) {
            return;
        }

        // Define the text ranges (start is inclusive, end is exclusive)
        TextRange stringRange = TextRange.from(element.getTextRange().getStartOffset(), element.getTextLength());

        // highlight psi element
        holder.newAnnotation(matchingAnnotator.Severity, matchingAnnotator.Note)
            .range(stringRange)
            .create();
    }

    private ArrayList<AnnotatorInformation> readConfig(Project project) {
        StorageService projectService = project.getService(StorageService.class);
        return projectService.getAnnotatorList();
    }
}