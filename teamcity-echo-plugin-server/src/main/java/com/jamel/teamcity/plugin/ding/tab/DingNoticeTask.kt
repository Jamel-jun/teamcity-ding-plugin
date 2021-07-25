package com.jamel.teamcity.plugin.ding.tab

import cn.hutool.core.codec.Base64
import cn.hutool.core.util.URLUtil
import cn.hutool.crypto.digest.HMac
import cn.hutool.crypto.digest.HmacAlgorithm
import cn.hutool.http.ContentType
import cn.hutool.http.Header
import cn.hutool.http.HttpRequest
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil


/**
 *
 * @since 2021/07/11 00:53
 * @author gaojun
 **/
class DingNoticeTask(val dingNoticeDTO: DingNoticeDTO) {

    fun send() {
        val (buildName, buildTypeId, serverUrl, buildNumber, buildId, status, duration, userName, branch, config) = dingNoticeDTO
        if (false == config.isEnabled()) return
        val mdStr = """
            [$buildName](${dingNoticeDTO.getProjectUrl()})
            #### ![通过](https://tse2-mm.cn.bing.net/th/id/OIP-C.YztpcbbP5zaxcCP_MjFGxQHaHF?pid=ImgDet&rs=1)
            - 任务：[#$buildNumber](${dingNoticeDTO.getTaskUrl()})
            - 状态：<font color="${status.color}">${status.status}</font>
            - 持续时间：$duration
            - 执行人：Started by GitLab push by ${userName ?: ""}
            - 构建分支: ${branch ?: ""}${"\n"}
        """.trimIndent()

        val body = mapOf("msgtype" to "actionCard",
            Pair("actionCard",  mapOf(
                "title" to "瞅瞅你写的BUG！",
                "text" to mdStr,
                "hideAvatar" to "0",
                "btnOrientation" to "1",
                "btns" to listOf(mapOf("title" to "控制台", "actionURL" to dingNoticeDTO.getTaskUrl())))),
            "at" to mapOf(
                // 如果需要@某人，这里写他的手机号
                "atMobiles" to listOf("18974073482"),
                // 如果需要@所有人，这里写1
                "isAtAll" to 0))

        val dingRobotUrl = if (config.sign.isNotBlank()) {
            val timestamp = System.currentTimeMillis()
            val signData = HMac(HmacAlgorithm.HmacSHA256, config.sign.toByteArray()).digest("$timestamp\n${config.sign}")
            val sign = URLUtil.encode(Base64.encode(signData))
            "${config.url}&timestamp=$timestamp&sign=$sign"
        } else config.url
        HttpRequest.post(dingRobotUrl).body(JSONUtil.toJsonStr(body)).header(Header.CONTENT_TYPE, ContentType.JSON.value).timeout(3000).execute().body().let {
            var result = JSONObject(it)
            with(result.getStr("errmsg")) {
                when {
                    this.contains("keywords not in content") -> throw Exception("消息内容中不包含任何关键词")
                    this.contains("invalid timestamp") -> throw Exception("timestamp 无效")
                    this.contains("sign not matc") -> throw Exception("签名不匹配")
                    this.contains("not in whitelist") -> throw Exception("IP地址不在白名单")
                    else -> true
                }
            }
        }
    }
}