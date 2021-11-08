package com.github.thencuber.intellijlinterplugin.actions;

import com.github.thencuber.intellijlinterplugin.inspection.AnnotatorService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UpdateButton extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        Project MyProject = e.getProject();
        String basePath = MyProject.getBasePath();
        Path configPath = Paths.get(basePath, ".idea/linter.xml");
        e.getPresentation().setEnabledAndVisible(Files.exists(configPath));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project MyProject = e.getProject();
        AnnotatorService projectService = MyProject.getService(AnnotatorService.class);
        System.out.println("Linter update requested...");
        projectService.updateAnnotators();
        System.out.println("Linter update completed.");
    }

}
