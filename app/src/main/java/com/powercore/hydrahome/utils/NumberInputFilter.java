package com.powercore.hydrahome.utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

/**
 * author : JiangKun
 * e-mail : jiangkuikui001@gmail.com
 * time   : 2022/04/01
 * desc   :
 */
public class NumberInputFilter implements InputFilter {

    private Pattern mPattern;

    public NumberInputFilter(int precision, int scale) {
        String pattern = "^\\-?(\\d{0," + (precision - scale) + "}|\\d{0," + (precision - scale) + "}\\.\\d{0," + scale + "})$";
        this.mPattern = Pattern.compile(pattern);

    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned destination, int destinationStart, int destinationEnd) {
        if (end > start) {
            // adding: filter
            // build the resulting text
            String destinationString = destination.toString();
            String resultingTxt = destinationString.substring(0, destinationStart) + source.subSequence(start, end) + destinationString.substring(destinationEnd);
            // return null to accept the input or empty to reject it
            return resultingTxt.matches(this.mPattern.toString()) ? null : "";
        }
        // removing: always accept
        return null;
    }

}