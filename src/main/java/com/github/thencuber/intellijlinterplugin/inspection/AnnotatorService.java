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
        Path configPath = Paths.get(basePath, ".idea/linter.xml");
        if (Files.exists(configPath)) {
            System.out.println("Updating annotators using config file: " + configPath);
            VirtualFile configFile = VirtualFileManager.getInstance().findFileByNioPath(configPath);
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
                            String currentMatcherContent = currentMatcher.getElementsByTagName("literal").item(0).getTextContent();
                            Annotators.add(new LiteralAnnotator(currentNote,currentSeverity,currentMatcherContent));
                            break;
                        case "variable":
                            String currentVariableName = null;
                            if(!(currentMatcher.getElementsByTagName("variable").item(0).getTextContent() == null || Objects.equals(currentMatcher.getElementsByTagName("variable").item(0).getTextContent(), ""))) {
                                currentVariableName = currentMatcher.getElementsByTagName("variable").item(0).getTextContent();
                            }
                            String currentVariableType = null;
                            if(((Element) currentMatcher.getElementsByTagName("variable").item(0)).hasAttribute("type")) {
                                currentVariableType = ((Element) currentMatcher.getElementsByTagName("variable").item(0)).getAttribute("type");
                            }
                            Annotators.add(new VariableAnnotator(currentNote,currentSeverity,currentVariableName,currentVariableType));
                            break;
                        case "method":
                            String currentMethodReturnType = null;
                            if(((Element) currentMatcher.getElementsByTagName("method").item(0)).hasAttribute("returnType")) {
                                currentMethodReturnType = ((Element) currentMatcher.getElementsByTagName("method").item(0)).getAttribute("returnType");
                            }
                            String currentMethodName = null;
                            if(((Element) currentMatcher.getElementsByTagName("method").item(0)).hasAttribute("name")) {
                                currentMethodName = ((Element) currentMatcher.getElementsByTagName("method").item(0)).getAttribute("name");
                            }
                            ArrayList<String> currentMethodModifierList = null;
                            if(((Element) currentMatcher.getElementsByTagName("method").item(0)).getElementsByTagName("modifier").getLength() > 0) {
                                currentMethodModifierList = new ArrayList<>();
                                NodeList XMLModifierList = ((Element) currentMatcher.getElementsByTagName("method").item(0)).getElementsByTagName("modifier");
                                for (int j = 0; j < XMLModifierList.getLength(); j++) {
                                    if(!(Objects.equals(XMLModifierList.item(j).getTextContent(),""))) {
                                        currentMethodModifierList.add(XMLModifierList.item(j).getTextContent());
                                    }
                                }
                            }
                            ArrayList<String> currentMethodParameterList = null;
                            if(((Element) currentMatcher.getElementsByTagName("method").item(0)).getElementsByTagName("parameter").getLength() > 0) {
                                currentMethodParameterList = new ArrayList<>();
                                NodeList XMLParameterList = ((Element) currentMatcher.getElementsByTagName("method").item(0)).getElementsByTagName("parameter");
                                for (int j = 0; j < XMLParameterList.getLength(); j++) {
                                    if(((Element) XMLParameterList.item(j)).hasAttribute("type")) {
                                        currentMethodParameterList.add(((Element) XMLParameterList.item(j)).getAttribute("type"));
                                    }
                                }
                            }
                            Annotators.add(new MethodAnnotator(currentNote,currentSeverity,currentMethodName,currentMethodReturnType,currentMethodModifierList,currentMethodParameterList));
                            break;
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
}