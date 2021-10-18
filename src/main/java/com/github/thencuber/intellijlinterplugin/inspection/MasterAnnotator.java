// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.github.thencuber.intellijlinterplugin.inspection;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.jetbrains.annotations.NotNull;
import java.util.regex.Pattern;

public class MasterAnnotator implements Annotator {

    private final HighlightSeverity SEVERITY;
    private final Pattern PATTERN;
    private final String NOTE;

    public MasterAnnotator(HighlightSeverity severity, Pattern pattern, String note) {
        SEVERITY = severity;
        PATTERN = pattern;
        NOTE = note;
    }

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        // Ensure the Psi Element is an expression
        if (!(element instanceof PsiLiteralExpression)) {
            return;
        }

        // Ensure the Psi element contains a string and matches
        PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
        String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
        if ((value == null) || !value.matches(PATTERN.pattern())) {
            return;
        }

        // Define the text ranges (start is inclusive, end is exclusive)
        TextRange stringRange = TextRange.from(element.getTextRange().getStartOffset(), element.getTextLength());

        // highlight psi element
        holder.newAnnotation(SEVERITY, NOTE)
            .range(stringRange)
            .create();
    }
}