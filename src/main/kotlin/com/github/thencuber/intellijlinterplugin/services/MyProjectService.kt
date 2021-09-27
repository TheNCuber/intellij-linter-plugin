package com.github.thencuber.intellijlinterplugin.services

import com.intellij.openapi.project.Project
import com.github.thencuber.intellijlinterplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
