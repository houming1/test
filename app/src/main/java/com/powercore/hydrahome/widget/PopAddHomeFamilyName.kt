package com.powercore.hydrahome.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.fdf.base.ext.getString
import com.fdf.base.ext.toast
import com.lxj.xpopup.core.BottomPopupView
import com.powercore.hydrahome.R
import com.powercore.hydrahome.databinding.PopAddHomeFamilyNameBinding
import com.powercore.hydrahome.ui.activity.family.add.NewHouseholdViewModel


/**
 *    Created by Administrator on 2021/12/16.
 *    Desc :
 */
class PopAddHomeFamilyName(context: Context) : BottomPopupView(context) {


    private var mViewModel: NewHouseholdViewModel? = null
    private lateinit var bind: PopAddHomeFamilyNameBinding
    private var inputEndBlock: (String) -> Unit = {}

    override fun getImplLayoutId() = R.layout.pop_add_home_family_name

    fun setViewModel(vm: NewHouseholdViewModel) {
        mViewModel = vm
    }

    fun setEndInputBlock(inputEndBlock: (String) -> Unit) {
        this.inputEndBlock = inputEndBlock
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        bind.vm = mViewModel
        bind.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnConfirm.setOnClickListener {
                mViewModel?.addHomeParams!!.value!!.apply {
                    if (householdName.isBlank()) {
                        getString(R.string.txt_please_enter_a_family_name).toast()
                        return@setOnClickListener
                    }
                    if (householdName.length>25){
                        "Family name cannot exceed 25 characters ".toast()
                        return@setOnClickListener
                    }
                }
                inputEndBlock(mViewModel?.addHomeParams?.value!!.householdName)
                dismiss()
            }
        }
    }
}