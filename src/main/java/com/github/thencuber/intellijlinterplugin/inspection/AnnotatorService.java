package com.github.thencuber.intellijlinterplugin.inspection;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

@Service
public final class AnnotatorService {

    private final Project MyProject;
    private ArrayList<AbstractAnnotator> AnnotatorList;

    public AnnotatorService(Project project) {
        MyProject = project;
        AnnotatorList = new ArrayList<>();
    }

    public ArrayList<AbstractAnnotator> getAnnotatorList() {
        return this.AnnotatorList;
    }

    public void updateAnnotators() {
        // Read config if possible
        String basePath = MyProject.getBasePath();
        if(basePath == null) {
            return;
        }
        Path configPath = Paths.get(basePath, ".idea/linter.xml");
        if (Files.exists(configPath)) {
            System.out.println("Updating annotators using config file: " + configPath);
            VirtualFile configFile = VirtualFileManager.getInstance().findFileByNioPath(configPath);
            if(configFile == null) {
                return;
            }
            try {
                // DEBUG: Print full config file
                // System.out.println(VfsUtil.loadText(configFile));

                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();

                Document document = builder.parse(configFile.getInputStream());
                document.getDocumentElement().normalize();
                ArrayList<AbstractAnnotator> Annotators = new ArrayList<>();
                NodeList linters = document.getDocumentElement().getElementsByTagName("linter");
                for (int i = 0; i < linters.getLength(); i++) {
                    Element currentLinter = (Element) linters.item(i);
                    String currentSeverity = currentLinter.getElementsByTagName("severity").item(0).getTextContent();
                    String currentNote = currentLinter.getElementsByTagName("note").item(0).getTextContent();
                    Element currentMatcher = (Element) currentLinter.getElementsByTagName("matcher").item(0);
                    String currentMatcherType = currentMatcher.getAttribute("type");
                    switch (currentMatcherType) {
                        case "literal":
                            Annotators.add(composeLiteralAnnotator(currentNote, currentSeverity, currentMatcher.getElementsByTagName("literal").item(0)));
                            break;
                        case "variable":
                            Annotators.add(composeVariableAnnotator(currentNote, currentSeverity, currentMatcher.getElementsByTagName("variable").item(0)));
                            break;
                        case "method":
                            Annotators.add(composeMethodAnnotator(currentNote, currentSeverity, currentMatcher.getElementsByTagName("method").item(0)));
                            break;
                        case "class":
                            Annotators.add(composeClassAnnotator(currentNote, currentSeverity, currentMatcher.getElementsByTagName("class").item(0)));
                        default: break;
                    }

                }
                AnnotatorList = Annotators;
                DaemonCodeAnalyzer.getInstance(MyProject).restart();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Did not find linter.xml config file at " + configPath);
        }
    }

    private LiteralAnnotator composeLiteralAnnotator(String Note, String Severity, Node Matcher) {
        String currentMatcherContent = Matcher.getTextContent();
        return new LiteralAnnotator(Note,Severity,currentMatcherContent);
    }

    private VariableAnnotator composeVariableAnnotator(String Note, String Severity, Node Matcher) {
        String currentVariableName = null;
        if(!(Matcher.getTextContent() == null || Objects.equals(Matcher.getTextContent(), ""))) {
            currentVariableName = Matcher.getTextContent();
        }
        String currentVariableType = null;
        if(((Element) Matcher).hasAttribute("type")) {
            currentVariableType = ((Element) Matcher).getAttribute("type");
        }
        return new VariableAnnotator(Note,Severity,currentVariableName,currentVariableType);
    }

    private MethodAnnotator composeMethodAnnotator(String Note, String Severity, Node Matcher) {
        String currentMethodReturnType = null;
        if(((Element) Matcher).hasAttribute("returnType")) {
            currentMethodReturnType = ((Element) Matcher).getAttribute("returnType");
        }
        String currentMethodName = null;
        if(((Element) Matcher).hasAttribute("name")) {
            currentMethodName = ((Element) Matcher).getAttribute("name");
        }
        ArrayList<String> currentMethodModifierList = null;
        if(((Element) Matcher).getElementsByTagName("modifier").getLength() > 0) {
            currentMethodModifierList = new ArrayList<>();
            NodeList XMLModifierList = ((Element) Matcher).getElementsByTagName("modifier");
            for (int j = 0; j < XMLModifierList.getLength(); j++) {
                if(!(Objects.equals(XMLModifierList.item(j).getTextContent(),""))) {
                    currentMethodModifierList.add(XMLModifierList.item(j).getTextContent());
                }
            }
        }
        ArrayList<String> currentMethodParameterList = null;
        if(((Element) Matcher).getElementsByTagName("parameter").getLength() > 0) {
            currentMethodParameterList = new ArrayList<>();
            NodeList XMLParameterList = ((Element) Matcher).getElementsByTagName("parameter");
            for (int j = 0; j < XMLParameterList.getLength(); j++) {
                if(((Element) XMLParameterList.item(j)).hasAttribute("type")) {
                    currentMethodParameterList.add(((Element) XMLParameterList.item(j)).getAttribute("type"));
                }
            }
        }
        return new MethodAnnotator(Note,Severity,currentMethodName,currentMethodReturnType,currentMethodModifierList,currentMethodParameterList);
    }

    private ClassAnnotator composeClassAnnotator(String Note, String Severity, Node Matcher) {
        String currentClassName = null;
        if(((Element) Matcher).hasAttribute("name")) {
            currentClassName = ((Element) Matcher).getAttribute("name");
        }
        String currentSuperClassName = null;
        if(((Element) Matcher).hasAttribute("superClass")) {
            currentSuperClassName = ((Element) Matcher).getAttribute("superClass");
        }
        ArrayList<String> currentClassInterfacesList = null;
        if(((Element) Matcher).getElementsByTagName("interface").getLength() > 0) {
            currentClassInterfacesList = new ArrayList<>();
            NodeList XMLInterfacesList = ((Element) Matcher).getElementsByTagName("interface");
            for (int j = 0; j < XMLInterfacesList.getLength(); j++) {
                if(((Element) XMLInterfacesList.item(j)).hasAttribute("name")) {
                    currentClassInterfacesList.add(((Element) XMLInterfacesList.item(j)).getAttribute("name"));
                }
            }
        }
        ArrayList<MethodAnnotator> currentClassMethodsList = null;
        if(((Element) Matcher).getElementsByTagName("method").getLength() > 0) {
            currentClassMethodsList = new ArrayList<>();
            NodeList XMLMethodsList = ((Element) Matcher).getElementsByTagName("method");
            for (int j = 0; j < XMLMethodsList.getLength(); j++) {
                currentClassMethodsList.add(composeMethodAnnotator("INNER ANNOTATOR - NOTE IGNORED", "INFORMATION", XMLMethodsList.item(j)));
            }
        }
        return new ClassAnnotator(Note,Severity,currentClassName,currentSuperClassName,currentClassInterfacesList,currentClassMethodsList);
    }
}