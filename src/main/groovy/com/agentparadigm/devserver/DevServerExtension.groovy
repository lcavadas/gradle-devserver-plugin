package com.agentparadigm.devserver

import org.gradle.api.Project;

/**
 * Extension class for configuring the DevServer plugin.
 */
class DevServerExtension {

    /**
     * Path from which the files should be served
     */
    String rootPath

    /**
     * Port used by the devserver
     */
    int port

    /**
     * Number of worker threads used by the devserver
     */
    int workers

    private Project project

    /**
     * Constructor for the extension.  It needs a project handle to set the sets
     * sensible defaults.
     * @param project the Gradle project that owns the extension.
     */
    DevServerExtension(Project project) {
        project.logger.info "creating extension"

        this.project = project

        port = 8080
        rootPath = ${project.buildDir.path}
        workers = 5
    }
}