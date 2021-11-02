package com.github.thencuber.intellijlinterplugin.inspection;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Service
public final class AnnotatorService {

    private final Project MyProject;
    private ArrayList<AnnotatorInformation> AnnotatorList;

    public AnnotatorService(Project project) {
        MyProject = project;
        AnnotatorList = new ArrayList<AnnotatorInformation>();
    }

    public ArrayList<AnnotatorInformation> getAnnotatorList() {
        return this.AnnotatorList;
    }

    public boolean updateAnnotators() {
        // Read config if possible
        String basePath = MyProject.getBasePath();
        Path configPath = Paths.get(basePath, ".idea/linter.xml");
        DaemonCodeAnalyzer.getInstance(MyProject).restart();
        if (Files.exists(configPath)) {
            System.out.println("Updating annotators using config file: " + configPath.toString());
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
                AnnotatorList = Annotators;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("Did not find linter.xml config file at " + configPath);
            return false;
        }
    }
}