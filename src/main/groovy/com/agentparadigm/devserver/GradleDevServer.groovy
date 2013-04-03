package com.agentparadigm.devserver

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class GradleDevServer implements Plugin<Project> {
    def void apply(Project project) {
        project.logger.warn("Starting devserver plugin in $project")
        project.apply plugin: 'java'

        project.extensions.create('devserver', DevServerExtension, project)
        project.extensions.devserver = new DevServer()

        if (!project.configurations.asMap['devserver']) {
            project.logger.warn("Found confs ")

            project.configurations.add('devserver')
        }
        project.tasks.add(name: 'devserver', type: DefaultTask, {configuration = project.extensions.devserver})

        Task buildTask = project.tasks.getByName("build")
        Task devServerTask = project.tasks.getByName("devserver")
        devServerTask.dependsOn buildTask;
    }
}