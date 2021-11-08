package com.github.thencuber.intellijlinterplugin.inspection;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;

public class LiteralAnnotator extends AbstractAnnotator {
    private java.util.regex.Pattern Pattern;

    public LiteralAnnotator(String Note, String Severity, String RegexPatternString) {
        super(Note,Severity);
        this.Pattern = java.util.regex.Pattern.compile(RegexPatternString);
    }

    public Boolean checkMatch(PsiElement input) {
        // Ensure the Psi Element is an expression
        if (!(input instanceof PsiLiteralExpression)) {
            return false;
        }

        // Ensure the Psi element contains a string
        PsiLiteralExpression literalExpression = (PsiLiteralExpression) input;
        String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
        if (value == null) {
            return false;
        }
        return value.matches(this.Pattern.pattern());
    }
}