package com.powercore.hydrahome.net.entity.req

import com.powercore.hydrahome.net.entity.rsp.User
import com.squareup.moshi.JsonClass

/**
 * 登录
 */
@JsonClass(generateAdapter = true)
data class LoginVo(
    var account: String = "",
    var password: String = ""
)

/**
 * 注册
 */
@JsonClass(generateAdapter = true)
data class RegisterVo(
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var note: String = "",//邮箱验证码
    var areaCode: String = "",
    var phone: String = "",
    var birthday: String = "",
    var sex: String = "",
    var checkedPassword: String = ""
)

/**
 * 忘记密码
 */
@JsonClass(generateAdapter = true)
data class ForgetPwdVo(
    var email: String = "",
    var code: String = "",
    var password: String = "",
    var checkedPassword: String = ""
)

/**
 * 获取家庭充电桩
 */
@JsonClass(generateAdapter = true)
data class GetHomeChargeBoxVo(var hydraHomeHouseholdPk: String = "")

/**
 * 添加家庭
 */
@JsonClass(generateAdapter = true)
data class AddHomeVo(
    // var homePk: String = "",
    //var homeName: String = "",
    // var note: String = "",
    //  var maxPower: String = "",//单位kw
    //   var members: MutableList<String> = mutableListOf(),
//    @Transient
//    var showKwh:Boolean= maxPower.isNotEmpty()

    var hydraHomeHouseholdPk: String = "",
    var dayRate: String = "",
    var dayStartTime: String = "",
    var dayStopTime: String = "",
    var delay: String = "",
    var householdMaxLoad: String = "",
    var householdName: String = "",
    var nightRate: String = "",
    var nightStartTime: String = "",
    var nightStopTime: String = "",
    var note: String = "",
    var userPks: MutableList<String> = mutableListOf()
)

/**
 * 获取家庭充电桩
 */
@JsonClass(generateAdapter = true)
data class DefaultVo(var name: String = "aa")

/**
 * 远程启动
 *
 * @property connectorPk
 */
@JsonClass(generateAdapter = true)
data class RemoteStartVo(
    var hydraHomeHouseholdChargeBoxPk: String = "",
    var startMode: String = "",
    var type: String = "",
    var value: String = ""
)

/**
 * 添加 profile
 *
 * @property connectorPk
 */
@JsonClass(generateAdapter = true)
data class AddProfileVo(
    var hydraHomeHouseholdPk: String = "",
    var hydraHomeHouseholdProfileDayStart: String = "",
    var hydraHomeHouseholdProfileDayStop: String = "",
    var hydraHomeHouseholdProfileName: String = "",
    var hydraHomeHouseholdProfileNightStart: String = "",
    var hydraHomeHouseholdProfileNightStop: String = "",
    var hydraHomeHouseholdProfileWeeks: MutableList<Int> = mutableListOf(),
    var hydraHomeHouseholdProfilePk: String = ""
)

/**
 * 删除 profile
 *
 * @property connectorPk
 */
@JsonClass(generateAdapter = true)
data class ProfilePkVo(
    var hydraHomeHouseholdProfilePk: String = ""
)
/**
 * 绑定 profile
 *
 * @property connectorPk
 */
@JsonClass(generateAdapter = true)
data class AssignChargeBoxProfileVo(
    var hydraHomeHouseholdChargeBoxPk: String = "",
    var hydraHomeHouseholdProfilePk: String = "",
)

/**
 *
 *
 * @property connectorPk
 */
@JsonClass(generateAdapter = true)
data class HydraHomeHouseholdChargeBoxPkVo(
    var hydraHomeHouseholdChargeBoxPk: String = ""
)

/**
 *
 * 限制充电桩功率大小
 * @property connectorPk
 */
@JsonClass(generateAdapter = true)
data class LimitPowerVo(
    var transactionPk: Long? = 0,
    var unit: String = "",
    var value: String = ""
)

/**
 * 充电数据统计
 */
@JsonClass(generateAdapter = true)
data class TransactionStatsVo(
    var type: String = "",//必须；"个人: person, 家庭: household"
    var cycle: String = "",//必须;"week或者month",
    var date: String = "",//必须;"如果cycle是周, 那么日期必须是某天的周一开始周日结束, 格式: yyyy-MM-dd~yyyy-MM-dd. 如果cycle是月, 那么只传某年某月即可, 格式: yyyy-MM",
    var hydraHomeHouseholdPk: String=""//可选;如果target="Home",改字段必须传,且不能为空,如果target="personal",该字段不传.
)

/**
 * 获取家庭充电桩
 */
@JsonClass(generateAdapter = true)
data class DateVo(var startTime: String = "",var stopTime: String = "")

/**
 * 充电记录
 */
@JsonClass(generateAdapter = true)
data class TransactionListVo(var hydraHomeHouseholdPk: String = "",
                             var pageNum: Int = 0,
                             var startDate: String = "",
                             var stopDate: String = "",
                             var type: String = "",)

/**
 * 退出登录
 *
 *
 */
@JsonClass(generateAdapter = true)
data class Logout(var user: User? = null)

/**
 * 上传头像
 *
 *
 */
@JsonClass(generateAdapter = true)
data class Image(val img: String? = null)

/**
 * 上传头像
 *
 *
 */
@JsonClass(generateAdapter = true)
data class SubmitFeedbackVo(var chargePointModel: String? = null,
                            var content: String? = null,
                            var email: String? = null,
                            var firstName: String? = null,
                            var surname: String? = null)

