package com.powercore.hydrahome.net

import com.powercore.hydrahome.net.entity.rsp.*
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

/**
 *    Created by Administrator on 2021/11/25.
 *    Desc :
 */
interface ApiService {

    companion object {
        //const val BASE_URL = "https://cloud.cnpowercore.com:8091/"
        const val BASE_URL = "https://170s2247n7.51mypc.cn/"
    }

    //*****************************************************************************
    //*  登录注册
    //*****************************************************************************
    //刷新token
    @POST("/v1/api/refresh")
    fun refreshToken(): Call<JSONObject>

    //校验用户名
    @GET("app/user/validAppUsername")
    suspend fun validUserName(@Query("username") name: String): BaseResponse<Any>

    //注册
    @POST("app/user/updateSelfInfo")
    suspend fun updateSelfInfo(@Body body: RequestBody): BaseResponse<Any>

    //登录app
    @POST("/login/validateApp")
    suspend fun loginByPwd(@Body body: RequestBody): BaseResponse<LoginResult>

    //验证码
    @POST("app/user/getCode")
    suspend fun sendCode(@Body body: RequestBody): BaseResponse<Any>

    //忘记密码
    @POST("app/user/forgetPassword")
    suspend fun forgetPwd(@Body body: RequestBody): BaseResponse<Any>

    //忘记密码 发送邮箱验证码
    @GET("app/user/getEmailCode")
    suspend fun getEmailCode(@Query("email") email: String): BaseResponse<Any>


    //获取家庭充电桩列表
    @POST("/hydra/hydraHomeHouseholdChargeBox/getHouseholdChargeBoxList")
    suspend fun getHomeChargeBox(@Body requestBody: RequestBody): BaseResponse<MutableList<HomeChargeBox>>

    /*-------------------------home相关--------------------------*/
    //获取home列表
    @POST("/hydra/hydraHomeHousehold/getHouseholdList")
    suspend fun getHomeList(@Body requestBody: RequestBody): BaseResponse<MutableList<HomeListBean>>

    //获取家庭的homePk
    @GET("/home/home/getHomePk")
    suspend fun getHomePk(): BaseResponse<String>

    //删除家庭
    @POST("/hydra/hydraHomeHousehold/deleteHousehold")
    suspend fun delHome(@Body requestBody: RequestBody): BaseResponse<Any>

    //添加家庭
    @POST("/hydra/hydraHomeHousehold/addHousehold")
    suspend fun addHome(@Body requestBody: RequestBody): BaseResponse<Any>

    //家庭设置
    @POST("/hydra/hydraHomeHousehold/getHouseholdInfo")
    suspend fun getHomeSetting(@Body requestBody: RequestBody): BaseResponse<HomeSettingRsp>

    //新增家庭成员验证用户是否已存在
    @POST("/home/home/verify")
    suspend fun homeVerify(@Body requestBody: RequestBody): BaseResponse<VerifyRsp>

    //家庭设置页面中设置家庭功率
    @POST("/home/homeSetting/updateSetting")
    suspend fun updateSetting(@Body requestBody: RequestBody): BaseResponse<Any>

    //家庭设置页面中修改家庭名称/背景图/note
    @POST("/home/home/update")
    suspend fun update(@Body requestBody: RequestBody): BaseResponse<Any>

    //家庭设置页面,删除家庭成员
    @POST("/home/homeMember/delete")
    suspend fun delete(@Body requestBody: RequestBody): BaseResponse<Any>

    //家庭设置页面,添加家庭成员
    @POST("/home/homeMember/addMember")
    suspend fun addMember(@Body requestBody: RequestBody): BaseResponse<HomeSettingRsp.VerifyMemberRsp>

    //家庭设置页面,添加家庭成员
    @POST("/hydra/hydraHomeHousehold/updateHousehold")
    suspend fun updateHousehold(@Body requestBody: RequestBody): BaseResponse<Any>

    /***********************************************************************/
    //蓝牙
    /***********************************************************************/
    //蓝牙验证
    @POST("/hydra/hydraHomeHouseholdChargeBox/validAndAdd")
    suspend fun validBleAdd(@Body requestBody: RequestBody): BaseResponse<Any>
    //***********************************************************************
    //充电
    //***********************************************************************
    //远程启动
    @POST("/hydra/hydraHomeHouseholdChargeBox/remoteStart")
    suspend fun remoteStart(@Body requestBody: RequestBody): BaseResponse<Any>

    //停止充电
    @POST("/hydra/hydraHomeHouseholdChargeBox/remoteStop")
    suspend fun remoteStop(@Body requestBody: RequestBody): BaseResponse<String>

    //充电中数据
    @POST("/hydra/hydraHomeHouseholdTransaction/getChargingList")
    suspend fun getChargingData(@Body requestBody: RequestBody): BaseResponse<MutableList<ChargingBean>>

    //解绑充电桩
    @POST("/hydra/hydraHomeHouseholdChargeBox/deleteChargeBox")
    suspend fun deleteChargeBox(@Body requestBody: RequestBody): BaseResponse<Any>

    //重启充电桩
    @POST("/hydra/hydraHomeHouseholdChargeBox/rebootChargeBox")
    suspend fun rebootChargeBox(@Body requestBody: RequestBody): BaseResponse<Any>

    //限制充电桩的最大充电功率
    @POST("/hydra/hydraHomeHouseholdChargeBox/limitPower")
    suspend fun limitPower(@Body requestBody: RequestBody): BaseResponse<Any>



    //***********************************************************************
    //Charge Profile
    //***********************************************************************

    //添加Profile
    @POST("/hydra/hydraHomeHouseholdProfile/addProfile")
    suspend fun addProfile(@Body requestBody: RequestBody): BaseResponse<Any>
    //添加Profile
    @POST("/hydra/hydraHomeHouseholdProfile/getHouseholdProfileList")
    suspend fun getHouseholdProfileList(@Body requestBody: RequestBody): BaseResponse<MutableList<ProfileListRsp>>

    //获取 Profile列表
    @POST("/hydra/hydraHomeHouseholdProfile/getList")
    suspend fun getList(@Body requestBody: RequestBody): BaseResponse<MutableList<ProfileItemRsp>>

    //修改Profile
    @POST("/hydra/hydraHomeHouseholdProfile/updateProfile")
    suspend fun updateProfile(@Body requestBody: RequestBody): BaseResponse<Any>

    //删除Profile
    @POST("/hydra/hydraHomeHouseholdProfile/deleteProfile")
    suspend fun deleteProfile(@Body requestBody: RequestBody): BaseResponse<Any>

    //给充电桩绑定Profile
    @POST("/hydra/hydraHomeHouseholdChargeBox/assignChargeBoxProfile")
    suspend fun assignChargeBoxProfile(@Body requestBody: RequestBody): BaseResponse<Any>

    //***********************************************************************
    //transactionStats
    //***********************************************************************
    //统计
    @POST("/hydra/hydraHomeHouseholdTransaction/transactionStatistics")
    suspend fun transactionStats(@Body requestBody: RequestBody): BaseResponse<StatsPersonBean>

    //下载记录
    @POST("/ocpp/transaction/downloadHydraHomeTransaction")
    suspend fun downloadHydraHomeTransaction(@Body requestBody: RequestBody): BaseResponse<Any>

    //充电记录
    @POST("/hydra/hydraHomeHouseholdTransaction/getTransactionList")
    suspend fun getTransactionList(@Body requestBody: RequestBody): BaseResponse<ChargingRecordRsp>

    //***********************************************************************
    //个人中心
    //***********************************************************************
    //退出登录
    @POST("/authen/login/logout")
    suspend fun logout(@Body requestBody: RequestBody): BaseResponse<String>

    // 上传头像
    @POST("/pcApp/index/uploadAvatar")
    suspend fun updateAvatar(@Body requestBody: RequestBody): BaseResponse<AvatarUrlBean>

    // 上传头像
    @POST("/hydra/hydraHomeFeedback/submitFeedback")
    suspend fun submitFeedback(@Body requestBody: RequestBody): BaseResponse<String >

}