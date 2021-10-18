package com.github.thencuber.intellijlinterplugin.startup;

import com.github.thencuber.intellijlinterplugin.inspection.MasterAnnotator;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.lang.LanguageAnnotators;
import com.intellij.lang.LanguageExtensionPoint;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.PluginDescriptor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.extensions.DefaultPluginDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PluginStartupActivity implements com.intellij.openapi.startup.StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        System.out.println("Project plugin loaded");
        ArrayList<AnnotatorInformation> AnnotatorInformationList = readConfig(project);

        if(AnnotatorInformationList.size() > 0) {
            for (int i = 0; i < AnnotatorInformationList.size(); i++) {
                PluginDescriptor MyPluginDescriptor = new DefaultPluginDescriptor(PluginId.getId("com.github.thencuber.intellijlinterplugin"), MasterAnnotator.class.getClassLoader());
                LanguageExtensionPoint<Annotator> extension = new LanguageExtensionPoint<Annotator>("JAVA", new MasterAnnotator(AnnotatorInformationList.get(i).Severity, AnnotatorInformationList.get(i).Pattern, AnnotatorInformationList.get(i).Note));
                extension.setPluginDescriptor(MyPluginDescriptor);
                ApplicationManager.getApplication().getExtensionArea().getExtensionPoint(LanguageAnnotators.EP_NAME).registerExtension(extension,Disposer.newDisposable());
                System.out.println("Annotator registered");
            }
        }
    }

    public class AnnotatorInformation {
        public java.util.regex.Pattern Pattern;
        public String Note;
        public HighlightSeverity Severity;

        AnnotatorInformation(String RegexPatternString, String Note, String Severity) {
            this.Pattern = java.util.regex.Pattern.compile(RegexPatternString);
            this.Note = Note;
            switch (Severity) {
                case "ERROR":
                    this.Severity = HighlightSeverity.ERROR;
                    break;
                case "WARNING":
                    this.Severity = HighlightSeverity.WARNING;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + Severity);
            }
        }

        public Boolean checkMatch(String input) {
            return input.matches(this.Pattern.pattern());
        }
    }

    private ArrayList<AnnotatorInformation> readConfig(@NotNull final Project project) {
        // Read config if possible
        String basePath = project.getBasePath();
        Path configPath = Paths.get(basePath, ".idea/linter.xml");
        DaemonCodeAnalyzer.getInstance(project).restart();
        if (Files.exists(configPath)) {
            // System.out.println("Found: " + configPath.toString());
            VirtualFile configFile = VirtualFileManager.getInstance().findFileByNioPath(configPath);
            try {
                // DEBUG: Print full config file
                // System.out.println(VfsUtil.loadText(configFile));

                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();

                Document document = builder.parse(configFile.getInputStream());
                document.getDocumentElement().normalize();
                ArrayList<AnnotatorInformation> Annotators = new ArrayList<AnnotatorInformation>();
                NodeList linters = document.getDocumentElement().getElementsByTagName("linter");
                for (int i = 0; i < linters.getLength(); i++) {
                    Element currentLinter = (Element) linters.item(i);
                    String currentSeverity = currentLinter.getElementsByTagName("severity").item(0).getTextContent();
                    String currentNote = currentLinter.getElementsByTagName("note").item(0).getTextContent();
                    String currentType = ((Element) currentLinter.getElementsByTagName("literal").item(0)).getAttribute("type");
                    String currentMatcherContent = currentLinter.getElementsByTagName("literal").item(0).getTextContent();
                    Annotators.add(new AnnotatorInformation(currentMatcherContent,currentNote,currentSeverity));
                }
                return Annotators;
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<AnnotatorInformation>();
            }
        } else {
            System.out.println("Did not find " + configPath.toString());
            return new ArrayList<AnnotatorInformation>();
        }
    }
}
