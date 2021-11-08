package com.github.thencuber.intellijlinterplugin.startup;

import com.github.thencuber.intellijlinterplugin.inspection.AnnotatorService;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class PluginStartupActivity implements com.intellij.openapi.startup.StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        System.out.println("Project plugin loaded. Starting initialization...");
        AnnotatorService projectService = project.getService(AnnotatorService.class);
        projectService.updateAnnotators();
        System.out.println("Initializing complete.");
    }
}
