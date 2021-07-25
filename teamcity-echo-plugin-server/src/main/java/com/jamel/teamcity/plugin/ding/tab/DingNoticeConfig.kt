package com.jamel.teamcity.plugin.ding.tab

data class DingNoticeConfig(
    val dingNoticeEnabled: String = "false",
    val url: String = "",
    val keyword: String = "",
    val sign: String = "",
    val ipAddress: String = "",
) {

    companion object {
        const val projectExternalId = "projectExternalId"
        const val dingNoticeEnabled = "dingNoticeEnabled"
        const val url = "url"
        const val keyword = "keyword"
        const val sign = "sign"
        const val ipAddress = "ipAddress"
      
        val disabled = DingNoticeConfig(
            dingNoticeEnabled = "false",
            url = "",
            keyword = "",
            sign = "",
            ipAddress = "",
        )

        fun fromMap(map: Map<String, String>): DingNoticeConfig {
            return DingNoticeConfig(
                dingNoticeEnabled = map.getOrDefault(dingNoticeEnabled, "false"),
                url = map.getOrDefault(url, ""),
                keyword = map.getOrDefault(keyword, ""),
                sign = map.getOrDefault(sign, ""),
                ipAddress = map.getOrDefault(ipAddress, ""),
            )
        }
    }

    fun isEnabled(): Boolean = dingNoticeEnabled.toBoolean()

    fun toMap(): Map<String, String> {
        return mapOf(
            Pair(Companion.dingNoticeEnabled, dingNoticeEnabled),
            Pair(Companion.url, url),
            Pair(Companion.keyword, keyword),
            Pair(Companion.sign, sign),
            Pair(Companion.ipAddress, ipAddress),
        )
    }
}
