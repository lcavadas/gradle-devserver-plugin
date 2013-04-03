package com.agentparadigm.devserver

import org.gradle.api.Plugin
import org.gradle.api.Project

class GradleDevServer implements Plugin<Project> {
  def void apply(Project project) {
    project.apply plugin: 'java'
    project.extensions.create('devserver', DevServerExtension, project)

    project.task('devserver') << {
      new DevServer(project.devserver.port, project.devserver.rootPath, project.devserver.workers, project.devserver.timeout)
    }

    project.tasks.getByName("devserver").dependsOn project.tasks.getByName("build")
  }
}