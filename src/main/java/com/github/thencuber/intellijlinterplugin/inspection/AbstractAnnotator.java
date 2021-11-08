package com.github.thencuber.intellijlinterplugin.inspection;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;

public abstract class AbstractAnnotator {
    public String Note;
    public HighlightSeverity Severity;

    public AbstractAnnotator(String Note, String Severity) {
        this.Note = Note;
        switch (Severity) {
            case "ERROR":
                this.Severity = HighlightSeverity.ERROR;
                break;
            case "WARNING":
                this.Severity = HighlightSeverity.WARNING;
                break;
            case "INFORMATION":
                this.Severity = HighlightSeverity.INFORMATION;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + Severity);
        }
    }

    public abstract Boolean checkMatch(PsiElement input);
}
