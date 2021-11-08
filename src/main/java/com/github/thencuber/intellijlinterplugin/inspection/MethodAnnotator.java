package com.github.thencuber.intellijlinterplugin.inspection;

import com.intellij.psi.*;

import java.util.ArrayList;

public class MethodAnnotator extends AbstractAnnotator {
    private java.util.regex.Pattern Pattern;
    private String ReturnType;
    private ArrayList<String> ModifierList;
    private ArrayList<String> ParameterList;

    public MethodAnnotator(String Note, String Severity, String NameRegexPattern, String ReturnType, ArrayList<String> ModifierList, ArrayList<String> ParameterList) {
        super(Note, Severity);
        if(NameRegexPattern != null) {
            this.Pattern = java.util.regex.Pattern.compile(NameRegexPattern);
        } else {
            this.Pattern = null;
        }
        this.ReturnType = ReturnType;
        this.ModifierList = ModifierList;
        this.ParameterList = ParameterList;
    }

    public Boolean checkMatch(PsiElement input) {
        if (!(input instanceof PsiMethod)) {
            return false;
        }

        PsiMethod method = (PsiMethod) input;
        if(this.ReturnType != null) {
            if(!(method.getReturnType().equalsToText(this.ReturnType))) {
                return false;
            }
        }

        if(this.Pattern != null) {
            if(!(method.getName().matches(this.Pattern.pattern()))) {
                return false;
            }
        }

        if(this.ModifierList != null) {
            PsiModifierList MethodModifierList = method.getModifierList();
            for (String s : this.ModifierList) {
                if (!MethodModifierList.hasModifierProperty(s)) {
                    return false;
                }
            }
        }

        if(this.ParameterList != null) {
            PsiParameter[] MethodParameters = method.getParameterList().getParameters();
            if(MethodParameters.length != this.ParameterList.size()) {
                return false;
            }
            for (int i = 0; i < MethodParameters.length; i++) {
                if(!(MethodParameters[i].getType().equalsToText(this.ParameterList.get(i)))) {
                    return false;
                }
            }
        }
        return true;
    }
}
