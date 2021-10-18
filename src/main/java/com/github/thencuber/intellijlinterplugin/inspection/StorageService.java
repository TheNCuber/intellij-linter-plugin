package com.github.thencuber.intellijlinterplugin.inspection;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;

@Service
public final class StorageService {

    private final Project myProject;
    private ArrayList<AnnotatorInformation> AnnotatorList;

    public StorageService(Project project) {
        myProject = project;
    }

    public void setAnnotatorInformation(ArrayList<AnnotatorInformation> AnnotatorList) {
        this.AnnotatorList = AnnotatorList;
    }

    public ArrayList<AnnotatorInformation> getAnnotatorList() {
        return this.AnnotatorList;
    }
}