package com.jamel.teamcity.plugin.ding

import com.jamel.teamcity.plugin.ding.tab.*
import jetbrains.buildServer.serverSide.*
import jetbrains.buildServer.serverSide.buildLog.MessageAttrs
import jetbrains.buildServer.util.EventDispatcher
import java.util.*

/**
 * 构建队列监听器
 * @since 2021/07/11 01:33
 * @author gaojun
 **/
class InQueueEventListener(eventDispatcher: EventDispatcher<BuildServerListener>, val webLinks: WebLinks, val server: SBuildServer): BuildServerAdapter() {
    init {
        eventDispatcher.addListener(this)
    }
    private val configStore = DingNoticeConfigStore()

    override fun buildTypeAddedToQueue(build: SQueuedBuild) {
        getVcsInfo(build.buildType).let { (userName, branch) ->
            DingNoticeTask(
                DingNoticeDTO(
                buildName = build.buildType.projectName,
                buildTypeId = build.buildType.externalId,
                serverUrl = server.rootUrl,
                buildNumber = build.buildTypeId,
                buildId = build.buildType.buildNumbers.buildCounter,
                status = BuildStatus.TO_QUEUE,
                userName = userName,
                branch = branch,
                config = configStore.findOwn(build.buildType.project)
            )
            ).send()
        }
    }

    override fun buildStarted(build: SRunningBuild) {
        val log = build.buildLog
        val config = configStore.findOwn(build.buildType!!.project)
        log.message("config.url = ${config.url} --- config.sign = ${config.sign}", null, MessageAttrs.attrs())
        build.buildType?.let {
            try {
                dingNotify(build, it.project, BuildStatus.START, computeDuration(build.queuedDate))
                log.message("钉钉通知成功: 开始构建", null, MessageAttrs.attrs())
            } catch (e: Exception) {
                log.message("钉钉通知失败,【${e.message}】", null, MessageAttrs.attrs())
            }
        } ?: log.message("开始构建(未找到构建类型)", null, MessageAttrs.attrs())
    }

    override fun buildFinished(build: SRunningBuild) {
        val log = build.buildLog
        log.message("status = ${build.buildStatus} --- statusDescriptor = ${build.statusDescriptor}", null, MessageAttrs.attrs())
        build.buildType?.let {
            try {
                dingNotify(build, it.project, if (build.buildStatus.isSuccessful) BuildStatus.SUCCESS else BuildStatus.FAIL, computeDuration(build.clientStartDate))
                log.message("钉钉通知成功: 构建结束", null, MessageAttrs.attrs())
            } catch (e: Exception) {
                log.message("钉钉通知失败,【${e.message}】", null, MessageAttrs.attrs())
            }
        } ?: log.message("构建结束(未找到构建类型)", null, MessageAttrs.attrs())
    }

    override fun buildInterrupted(build: SRunningBuild) {
        val log = build.buildLog
        log.message("status = ${build.buildStatus} --- statusDescriptor = ${build.statusDescriptor}", null, MessageAttrs.attrs())
        build.buildType?.let {
            try {
                dingNotify(build, it.project, BuildStatus.INTERRUPTED, computeDuration(build.clientStartDate))
                log.message("钉钉通知成功: 构建被中断", null, MessageAttrs.attrs())
            } catch (e: Exception) {
                log.message("钉钉通知失败,【${e.message}】", null, MessageAttrs.attrs())
            }
        } ?: log.message("构建被中断(未找到构建类型)", null, MessageAttrs.attrs())
    }

    private fun dingNotify(build: SRunningBuild, project: SProject, status: BuildStatus, duration: String) {
        getVcsInfo(build).let { (userName, branch) ->
            DingNoticeTask(
                DingNoticeDTO(
                    buildName = project.name,
                    buildTypeId = build.buildTypeExternalId,
                    serverUrl = server.rootUrl,
                    buildNumber = build.buildNumber,
                    buildId = build.buildId,
                    status = status,
                    duration = duration,
                    userName = userName,
                    branch = branch,
                    config = configStore.findOwn(project)
                )
            ).send()
        }
    }

    private fun getVcsInfo(build: SRunningBuild): Pair<String?, String?> {
        build.vcsRootEntries.firstOrNull()?.let {
            return Pair(it.vcsRoot.getProperty("username"), it.vcsRoot.getProperty("branch"))
        } ?: return Pair(null, null)
    }

    private fun getVcsInfo(build: SBuildType): Pair<String?, String?> {
        build.vcsRootEntries.firstOrNull()?.let {
            return Pair(it.vcsRoot.getProperty("username"), it.vcsRoot.getProperty("branch"))
        } ?: return Pair(null, null)
    }

    fun computeDuration(startTime: Date?): String {
        startTime ?: return ""
        val duration = (System.currentTimeMillis() / 1000) - (startTime.time / 1000)
        return if (duration < 60) {
            "$duration sec "
        } else if (duration in 61..3599) {
            val m = duration / 60
            val s = duration % 60
            "$m sec $s sec "
        } else {
            val h = duration / 3600
            val m = duration % 3600 / 60
            val s = duration % 3600 % 60
            if (h != 0L) "$h hour $m min $s sec " else "$m min $s sec "
        }
    }
}