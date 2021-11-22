package com.github.thencuber.intellijlinterplugin.inspection;

import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.Objects;

public class ClassAnnotator extends AbstractAnnotator {
    private java.util.regex.Pattern Pattern;
    private String SuperClass;
    private ArrayList<String> InterfacesList;
    private ArrayList<MethodAnnotator> MethodsList;

    public ClassAnnotator(String Note, String Severity, String NameRegexPattern, String SuperClass, ArrayList<String> InterfacesList, ArrayList<MethodAnnotator> MethodsList) {
        super(Note, Severity);
        if(NameRegexPattern != null) {
            this.Pattern = java.util.regex.Pattern.compile(NameRegexPattern);
        } else {
            this.Pattern = null;
        }
        this.SuperClass = SuperClass;
        this.InterfacesList = InterfacesList;
        this.MethodsList = MethodsList;
    }

    public Boolean checkMatch(PsiElement input) {
        if (!(input instanceof PsiClass)) {
            return false;
        }

        PsiClass inputClass = (PsiClass) input;

        if(this.Pattern != null) {
            if(!(Objects.requireNonNull(inputClass.getName()).matches(this.Pattern.pattern()))) {
                return false;
            }
        }

        if(this.SuperClass != null) {
            if(!(Objects.equals(Objects.requireNonNull(inputClass.getSuperClass()).getName(), this.SuperClass))) {
                return false;
            }
        }

        if(this.InterfacesList != null) {
            PsiJavaCodeReferenceElement[] ImplementedInterfaces = Objects.requireNonNull(inputClass.getImplementsList()).getReferenceElements();
            for (PsiJavaCodeReferenceElement s : ImplementedInterfaces) {
                if(!InterfacesList.contains(s.getQualifiedName())) {
                    return false;
                }
            }
        }

        if(this.MethodsList != null) {
            for(PsiMethod classMethod : inputClass.getAllMethods()) {
                if(classMethod instanceof  PsiCompiledElement) {
                    continue;
                }
                boolean methodMatchesDescription = false;
                for (MethodAnnotator MethodDescription: MethodsList) {
                    if(MethodDescription.checkMatch(classMethod)) {
                        methodMatchesDescription = true;
                        break;
                    }
                }
                if(!methodMatchesDescription) {
                    return false;
                }
            }
        }

        return true;
    }
}
