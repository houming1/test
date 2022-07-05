package com.powercore.hydrahome.ui.activity.setting

import com.fdf.base.base.BaseViewModel
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData


class SettingViewModel : BaseViewModel() {
    val cache = MutableLiveData<String>()
    val version = MutableLiveData<String>()

}