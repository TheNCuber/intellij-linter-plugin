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

//        if(AnnotatorInformationList.size() > 0) {
//            for (int i = 0; i < AnnotatorInformationList.size(); i++) {
//                PluginDescriptor MyPluginDescriptor = new DefaultPluginDescriptor(PluginId.getId("com.github.thencuber.intellijlinterplugin"), MasterAnnotator.class.getClassLoader());
//                LanguageExtensionPoint<Annotator> extension = new LanguageExtensionPoint<Annotator>("JAVA", new MasterAnnotator());
//                extension.setPluginDescriptor(MyPluginDescriptor);
//                ApplicationManager.getApplication().getExtensionArea().getExtensionPoint(LanguageAnnotators.EP_NAME).registerExtension(extension,Disposer.newDisposable());
//                System.out.println("Annotator registered");
//            }
//        }
    }
}
