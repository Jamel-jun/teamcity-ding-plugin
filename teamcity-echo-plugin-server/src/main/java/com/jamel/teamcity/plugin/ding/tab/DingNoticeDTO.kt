package com.jamel.teamcity.plugin.ding.tab

data class DingNoticeDTO(
    val buildName: String,
    val buildTypeId: String,
    val serverUrl: String,
    val buildNumber: String,
    val buildId: Long,
    val status: BuildStatus,
    val duration: String = "",
    val userName: String? = "",
    val branch: String? = "",
    val config: DingNoticeConfig
) {
    fun getProjectUrl(): String = "${serverUrl}/admin/editBuild.html?id=buildType:$buildTypeId"

    fun getTaskUrl(): String = "${serverUrl}/viewLog.html?buildId=$buildId"
}
