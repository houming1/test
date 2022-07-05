package com.powercore.hydrahome.ui.activity.bluetooth

import androidx.lifecycle.MutableLiveData
import com.fdf.base.base.BaseViewModel

class ChargeWhiteListEditViewModel :BaseViewModel() {
    var wifiName = MutableLiveData<String>("")
    var password = MutableLiveData<String>("")
    var passwordVisible = MutableLiveData<Boolean>(false)
    var isRemember= MutableLiveData<Boolean>(false)
}