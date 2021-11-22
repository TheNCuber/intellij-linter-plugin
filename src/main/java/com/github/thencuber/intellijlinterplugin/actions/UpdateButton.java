package com.github.thencuber.intellijlinterplugin.actions;

import com.github.thencuber.intellijlinterplugin.inspection.AnnotatorService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class UpdateButton extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        // The duplicated code has to stay in here, because otherwise speed exceptions occur
        Project MyProject = e.getProject();
        String basePath = MyProject.getBasePath();
        Path configPath = Paths.get(basePath, ".idea/linter.xml");
        e.getPresentation().setEnabledAndVisible(Files.exists(configPath));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project MyProject = e.getProject();
        Document configFile = FileDocumentManager.getInstance().getDocument(Objects.requireNonNull(VirtualFileManager.getInstance().findFileByNioPath(getConfigPath(MyProject))));
        FileDocumentManager.getInstance().saveDocument(configFile);
        AnnotatorService projectService = MyProject.getService(AnnotatorService.class);
        System.out.println("Linter update requested...");
        projectService.updateAnnotators();
        System.out.println("Linter update completed.");
    }

    private Path getConfigPath(Project p) {
        String basePath = p.getBasePath();
        return Paths.get(basePath, ".idea/linter.xml");
    }
}
