package com.github.thencuber.intellijlinterplugin.inspection;

import com.intellij.lang.annotation.HighlightSeverity;

public class AnnotatorInformation {
    public java.util.regex.Pattern Pattern;
    public String Note;
    public HighlightSeverity Severity;

    public AnnotatorInformation(String RegexPatternString, String Note, String Severity) {
        this.Pattern = java.util.regex.Pattern.compile(RegexPatternString);
        this.Note = Note;
        switch (Severity) {
            case "ERROR":
                this.Severity = HighlightSeverity.ERROR;
                break;
            case "WARNING":
                this.Severity = HighlightSeverity.WARNING;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + Severity);
        }
    }

    public Boolean checkMatch(String input) {
        return input.matches(this.Pattern.pattern());
    }
}