/*
 * Filename TestEditTap.java 2021年07月24日
 * Copyright © Ehome Co.Ltd. All Rgiths Reserved.
 * @author gaojun
 */
package com.jamel.teamcity.plugin.ding.tab

import jetbrains.buildServer.controllers.admin.projects.EditProjectTab
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PluginDescriptor
import javax.servlet.http.HttpServletRequest

/**
 * @author gaojun
 * @since 2021/07/24 08:23
 */
class DingNoticeEditProjectTap(pagePlaces: PagePlaces, pluginDescriptor: PluginDescriptor) :
    EditProjectTab(
        pagePlaces,
        "demoPluginName",
        pluginDescriptor.getPluginResourcesPath(jspPath),
        "钉钉机器人通知") {

    companion object {
        const val jspPath = "ding-edit-project-tab.jsp"
    }

    private val configStore = DingNoticeConfigStore()

    override fun fillModel(model: MutableMap<String, Any>, request: HttpServletRequest) {
        super.fillModel(model, request)

        val project = getProject(request) ?: return

        model[DingNoticeConfig.projectExternalId] = project.externalId

        val config = configStore.findOwn(project)
        if (config.isEnabled()) {
            model.putAll(config.toMap())
        }
    }
}