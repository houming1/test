package com.powercore.hydrahome.ui.activity.changepassword

import androidx.lifecycle.MutableLiveData
import com.fdf.base.base.BaseViewModel

class ChangePassworViewModel : BaseViewModel() {
    var oldPassword = MutableLiveData<String>()
    var newPassword = MutableLiveData<String>()
    var reenterNewPassword = MutableLiveData<String>()
}