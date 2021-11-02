package com.github.thencuber.intellijlinterplugin.startup;

import com.github.thencuber.intellijlinterplugin.inspection.AnnotatorService;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FileListener implements BulkFileListener {
    private MessageBusConnection connection;
    private static Logger LOG = Logger.getInstance(FileListener.class);

    public FileListener() {
        connection = ApplicationManager.getApplication().getMessageBus().connect();
        connection.subscribe(VirtualFileManager.VFS_CHANGES, this);
    }

    @Override
    public void before(@NotNull List<? extends VFileEvent> events) {
        for (VFileEvent event : events) {
            VirtualFile eventFile = event.getFile();

            if (eventFile != null && !eventFile.isDirectory()) {
                System.out.println("File changed: " + eventFile.getName());
            }
        }
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        for (VFileEvent event : events) {
            VirtualFile eventFile = event.getFile();

            if (eventFile != null && !eventFile.isDirectory() && eventFile.getName().equals("linter.xml")) {
                for (Project project : openProjects) {
                    AnnotatorService projectService = project.getService(AnnotatorService.class);
                    System.out.println("Requesting to update annotators");
                    projectService.updateAnnotators();
                    System.out.println("Succeded to update annotators");
                }
            }
        }
    }
}