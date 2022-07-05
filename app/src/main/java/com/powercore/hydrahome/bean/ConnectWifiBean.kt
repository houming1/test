package com.powercore.hydrahome.bean

import com.squareup.moshi.JsonClass

/**
 *
 *  author : JiangKun
 *  e-mail : jiangkuikui001@gmail.com
 *  time   : 2022/01/19
 *  desc   :
 *
 */
@JsonClass(generateAdapter = true)
class ConnectWifiBean(var ssid: String, var pwd: String)