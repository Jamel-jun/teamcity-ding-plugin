package com.jamel.teamcity.plugin.ding.tab

import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.serverSide.ProjectManager
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Responsible for saving the configuration
 */
class DingNoticeEditProjectController(sBuildServer: SBuildServer, webControllerManager: WebControllerManager, private val projectManager: ProjectManager) : BaseController(sBuildServer) {

    private val configStore = DingNoticeConfigStore()
    private val controllerPath = "/admin/deploys-edit-project.html"

    init {
        webControllerManager.registerController(controllerPath, this)
    }

    override fun doHandle(request: HttpServletRequest, response: HttpServletResponse): ModelAndView? {

        val config = DingNoticeConfig(
            dingNoticeEnabled = request.getParameter(DingNoticeConfig.dingNoticeEnabled),
            url = request.getParameter(DingNoticeConfig.url),
            keyword = request.getParameter(DingNoticeConfig.keyword),
            sign = request.getParameter(DingNoticeConfig.sign),
            ipAddress = request.getParameter(DingNoticeConfig.ipAddress),
        )

        val projectExternalId = request.getParameter(DingNoticeConfig.projectExternalId)
        val project = projectManager.findProjectByExternalId(projectExternalId)

        if (project != null) {
            configStore.store(project, config)
        }

        return null
    }

}
