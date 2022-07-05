package com.powercore.hydrahome.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseDelegateMultiAdapter;
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.powercore.hydrahome.R;
import com.powercore.hydrahome.net.entity.rsp.BleBean;
import com.ruffian.library.widget.RLinearLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlueListAdapter extends BaseDelegateMultiAdapter<BleBean, BaseViewHolder> {
    public static final int ONE = 1;
    public static final int TWO = 2;
    OnClickCallBack callBack;
    Context context;

    public BlueListAdapter(Context context) {
        super();
        this.context = context;
        // 方式一，使用匿名内部类，进行如下两步：
        // 第一步，设置代理
        setMultiTypeDelegate(new BaseMultiTypeDelegate<BleBean>() {
            @Override
            public int getItemType(@NotNull List<? extends BleBean> data, int position) {
                switch (position % 2) {
                    case 0:
                        return ONE;
                    case 1:
                        return TWO;
                    default:
                        break;
                }
                return 0;
            }
        });
        // 第二部，绑定 item 类型
        getMultiTypeDelegate()
                .addItemType(ONE, R.layout.item_ble_one_view)
                .addItemType(TWO, R.layout.item_ble_two_view);

    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BleBean bleBean) {
        RLinearLayout item = helper.findView(R.id.ll_item);
        if (bleBean.getChecked()) {
            item.getHelper().setBackgroundColorNormal(context.getResources().getColor(R.color.color_F9D5D5));
            item.getHelper().setBorderColorNormal(context.getResources().getColor(R.color.theme_color));
        } else {
            item.getHelper().setBackgroundColorNormal(context.getResources().getColor(R.color.white));
            item.getHelper().setBorderColorNormal(context.getResources().getColor(R.color.white));
        }
        TextView bleName = helper.findView(R.id.ble_name);
        bleName.setText(bleBean.getScanResult().getDevice().getName().replace("BLUFI_","")+"");


     /*   switch (helper.getItemViewType()) {
            case ONE:

                break;
            case TWO:

                break;
            default:
                break;
        }*/
    }

    //步骤1:定义一个回调接口
    public interface OnClickCallBack {
        void click(int position, BleBean chargingBean);


    }

    //步骤2:设置监听器
    public void setOnClickCallBack(OnClickCallBack callBack) {
        this.callBack = callBack;
    }
}
