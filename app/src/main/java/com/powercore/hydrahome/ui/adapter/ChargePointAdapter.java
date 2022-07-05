package com.powercore.hydrahome.ui.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseDelegateMultiAdapter;
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.powercore.hydrahome.R;
import com.powercore.hydrahome.net.entity.rsp.ChargingBean;
import com.powercore.hydrahome.net.entity.rsp.HomeChargeBox;
import com.ruffian.library.widget.RTextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChargePointAdapter extends BaseDelegateMultiAdapter<HomeChargeBox, BaseViewHolder> {
    public static final int ONLINE = 1;
    public static final int OFFLINE = 2;
    public static final int CHARGING = 3;

    OnClickCallBack callBack;

    public ChargePointAdapter() {
        super();
        // 方式一，使用匿名内部类，进行如下两步：
        // 第一步，设置代理
        setMultiTypeDelegate(new BaseMultiTypeDelegate<HomeChargeBox>() {
            @Override
            public int getItemType(@NotNull List<? extends HomeChargeBox> data, int position) {
                if (data.get(position).getConnectorStatus().equals("Offline")) {
                    return OFFLINE;
                } else {
                    if (data.get(position).getConnectorStatus().equals("Charging")) {
                        return CHARGING;
                    } else {
                        return ONLINE;
                    }

                }
            }
        });
        // 第二部，绑定 item 类型
        getMultiTypeDelegate()
                .addItemType(ONLINE, R.layout.item_online_view)
                .addItemType(OFFLINE, R.layout.item_offline_view)
                .addItemType(CHARGING, R.layout.item_charging_view);

    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, HomeChargeBox homeChargeBox) {
        TextView name = helper.findView(R.id.name);
        TextView type = helper.findView(R.id.type);
        TextView charging_profile = helper.findView(R.id.charging_profile);
        RTextView options = helper.findView(R.id.options);
        name.setText("Zodiac - " + homeChargeBox.getChargeBoxId());
        type.setText(homeChargeBox.getConnectorType() + " ｜ " + homeChargeBox.getPower() + "kW  ｜ Socket only");
        helper.findView(R.id.item_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.clickItem(helper.getLayoutPosition(), homeChargeBox);
            }
        });
        helper.findView(R.id.options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.clickOptions(helper.getLayoutPosition(), homeChargeBox);
            }
        });
        helper.findView(R.id.detele).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.clickDetele(helper.getLayoutPosition(), homeChargeBox);
            }
        });

        switch (helper.getItemViewType()) {
            case ONLINE:
            case OFFLINE:
                helper.findView(R.id.start_charging_session).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callBack.click(helper.getLayoutPosition(), homeChargeBox);
                    }
                });
                break;
            case CHARGING:

                ImageView powerIv = helper.findView(R.id.power_iv);
                AnimationDrawable animationDrawable;
                powerIv.setImageResource(R.drawable.battery_anim);
                animationDrawable = ((AnimationDrawable) powerIv.getDrawable());
                animationDrawable.start();


                break;
            default:
                break;
        }
    }

    //步骤1:定义一个回调接口
    public interface OnClickCallBack {
        void click(int position, HomeChargeBox chargingBean);

        void clickItem(int position, HomeChargeBox chargingBean);

        void clickOptions(int position, HomeChargeBox chargingBean);

        void clickDetele(int position, HomeChargeBox chargingBean);

    }

    //步骤2:设置监听器
    public void setOnClickCallBack(OnClickCallBack callBack) {
        this.callBack = callBack;
    }
}
