package com.github.thencuber.intellijlinterplugin.inspection;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MasterAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        // Get config
        ArrayList<AbstractAnnotator> myAnnotators = readConfig(element.getProject());

        // Ensure we have at least one annotator
        if (myAnnotators.size() < 1) {
            return;
        }

        // Check if one of the annotators matches
        boolean foundMatch = false;
        AbstractAnnotator matchingAnnotator = null;
        for (AbstractAnnotator currentAnnotator : myAnnotators) {
            if (currentAnnotator.checkMatch(element)) {
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

    private ArrayList<AbstractAnnotator> readConfig(Project project) {
        AnnotatorService projectService = project.getService(AnnotatorService.class);
        return projectService.getAnnotatorList();
    }
}