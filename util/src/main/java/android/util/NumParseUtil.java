package android.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

// 数字转化 
@SuppressLint("DefaultLocale")
public class NumParseUtil {

    // 人民币数字格式转化为分 结果为字符串
    public static String parseString(String value) {
        double result = 0.00d;
        try {
            result = Double.parseDouble(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.format("%.0f", result * 100);
    }

    // 对float型进行四则运算时需要新型格式化
    public static String formatFloat(float value) {
        DecimalFormat d = new DecimalFormat("#0.00");//对 float
        return d.format(value);
    }

    // 对float型进行四则运算时需要新型格式化
    public static String formatFloat(String value) {
        DecimalFormat d = new DecimalFormat("#0.00");//对 float
        return d.format(parseFloat(value));
    }


    // value/100 两位小数字符 （人民币显示的数字格式）
    public static String parseStringWithTwoDecimals(String value) {
        String price = String.format("%.2f", parseDouble(value) / 100.00d);
        return getPrettyNumber(price);
    }

    // value/100 两位小数字符 （人民币显示的数字格式）
    public static String parseStringWithTwoDecimals(double value) {
        String price = String.format("%.2f", value / 100.00d);
        return getPrettyNumber(price);
    }

    // value/100 两位小数字符 （人民币显示的数字格式）
    public static String parseStringWithTwoDecimals(long value) {
        String price = String.format("%.2f", value / 100);
        return getPrettyNumber(price);
    }

    // value/100 两位小数字符 （人民币显示的数字格式）
    public static float parseTwoDecimalsToInt(String value) {
        return parseInt(value) / 100;
    }

    public static String parsePriceStringWithTwoDecimals(String value) {
        return String.format("%.2f", parseInt(value) / 100.00);
    }

    // 字符串转 long 整型
    public static long parseLong(String value) {
        return parseLong(value, 0L);
    }

    // 字符串转 long 整型
    public static long parseLong(String value, long defaultValue) {
        long resultValue = defaultValue;
        try {
            resultValue = Long.parseLong(value);
        } catch (Exception e) {
            e.printStackTrace();
            return resultValue;
        }
        return resultValue;
    }

    // 字符串转 double
    public static double parseDouble(String value, double defaultValue) {
        double resultValue;
        try {
            resultValue = Double.parseDouble(value);
        } catch (Exception e) {
            e.printStackTrace();
            resultValue = defaultValue;
        }
        return resultValue;
    }

    // 字符串转 double
    public static double parseDouble(String value) {
        return parseDouble(TextUtils.isEmpty(value) ? "0" : value, 0.0D);
    }

    // 字符串转 float
    public static float parseFloat(String value, float defaultValue) {
        float resultValue = defaultValue;
        try {
            resultValue = Float.parseFloat(value);
        } catch (Exception e) {
            e.printStackTrace();
            resultValue = defaultValue;
        }
        return resultValue;
    }


    // 字符串转 float
    public static float parseFloat(String value) {
        return parseFloat(value, 0.0F);
    }

    // 字符串转 float
    public static float parseFormatFloat(float value) {
        return parseFloat(formatFloat(value));
    }

    // 字符串转 int
    public static int parseInt(String value, int defaultValue) {
        int resultValue = defaultValue;
        try {
            resultValue = TextUtils.isEmpty(value) ? defaultValue : Integer.parseInt(value);
        } catch (Exception e) {
            e.printStackTrace();
            resultValue = defaultValue;
        }
        return resultValue;
    }

    // 字符串转 int
    public static int parseInt(String value) {
        return parseInt(value, 0);
    }

    // 字符串转 int
    public static boolean parseBoolean(String value, boolean defaultValue) {
        boolean resultValue = defaultValue;
        try {
            resultValue = TextUtils.isEmpty(value) ? defaultValue : Boolean.parseBoolean(value);
        } catch (Exception e) {
            e.printStackTrace();
            resultValue = defaultValue;
        }
        return resultValue;
    }

    // 字符串转 int
    public static boolean parseBoolean(String value) {
        return parseBoolean(value, false);
    }

    public static String parseStringWithoutTwoDecimals(String value) {
        String price = String.format("%d", parseInt(value) / 100);
        return getPrettyNumber(price);
    }

    public static String parseStringWithoutTwoDecimalsToInt(String value) {
        String price = String.format("%d", parseInt(value) / 100);
        return getPrettyNumber(price);
    }

    // 去除数字里多余的0
    public static String getPrettyNumber(String number) {
        String bigDecimalStr;
        try {//当number==NaN，会throw "Infinity or NaN",所以要catch
            bigDecimalStr = BigDecimal.valueOf(parseDouble(number)).stripTrailingZeros().toPlainString();
        } catch (NumberFormatException e) {
            bigDecimalStr = "-1";
        }
        if (TextUtils.isEmpty(bigDecimalStr)) {
            return "0";
        }
        if (bigDecimalStr.endsWith(".00") || bigDecimalStr.endsWith(".0")) {
            return bigDecimalStr.substring(0, bigDecimalStr.lastIndexOf("."));
        }
        return bigDecimalStr;
    }
}
