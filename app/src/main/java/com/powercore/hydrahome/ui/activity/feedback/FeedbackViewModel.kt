package com.powercore.hydrahome.ui.activity.feedback

import androidx.lifecycle.MutableLiveData
import com.fdf.base.base.BaseViewModel
import com.fdf.base.ext.isEmail
import com.fdf.base.ext.request
import com.fdf.base.ext.toast
import com.gk.net.ext.toRequestBody
import com.powercore.hydrahome.net.apiService
import com.powercore.hydrahome.net.entity.req.SubmitFeedbackVo

class FeedbackViewModel : BaseViewModel() {

    val firstName = MutableLiveData<String>()
    val surname = MutableLiveData<String>()
    val emailAddress = MutableLiveData<String>()
    val confirmChargePointModel = MutableLiveData<String>()
    val content = MutableLiveData<String>()

    fun submitFeedback() {
     var submitFeedbackVo= SubmitFeedbackVo()
        if (firstName.value.isNullOrBlank()){
            "Please enter first name".toast()
            return
        }
        if (surname.value.isNullOrBlank()){
            "Please enter  Surname".toast()
            return
        }
        if (emailAddress.value.isNullOrBlank()){
            "Please enter  Email address".toast()
            return
        }
        if (!emailAddress.value.toString().isEmail()){
            "Please enter the correct email address".toast()
            return
        }
        if (confirmChargePointModel.value.isNullOrBlank()){
            "Please enter Confirm Charge point model".toast()
            return
        }

        if (content.value.isNullOrBlank()){
            "Please provide us with your feedback".toast()
            return
        }
        submitFeedbackVo.firstName=firstName.value
        submitFeedbackVo.surname=surname.value
        submitFeedbackVo.email=emailAddress.value
        submitFeedbackVo.chargePointModel=confirmChargePointModel.value
        submitFeedbackVo.content=content.value
        request({ apiService.submitFeedback(submitFeedbackVo.toRequestBody()) },
            isShowLoad = true,
            listenerBuilder = {
                onSuccess = { data, _ ->

                }
            })
    }

}