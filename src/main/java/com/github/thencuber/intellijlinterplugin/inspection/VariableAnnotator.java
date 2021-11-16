package com.github.thencuber.intellijlinterplugin.inspection;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiVariable;

public class VariableAnnotator extends AbstractAnnotator {
    private java.util.regex.Pattern Pattern;
    private String Type;

    public VariableAnnotator(String Note, String Severity, String NameRegexPattern, String Type) {
        super(Note,Severity);
        if(NameRegexPattern != null) {
            this.Pattern = java.util.regex.Pattern.compile(NameRegexPattern);
        } else {
            this.Pattern = null;
        }
        this.Type = Type;
    }

    public Boolean checkMatch(PsiElement input) {
        if (!(input instanceof PsiVariable)) {
            return false;
        }

        PsiVariable variable = (PsiVariable) input;
        if(this.Type != null) {
            if(!(variable.getType().equalsToText(this.Type))) {
                return false;
            }
        }

        if(this.Pattern != null) {
            return variable.getName().matches(this.Pattern.pattern());
        }

        return true;
    }
}