package com.fenghks.business.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {

	public static final String EMPTY_SRING = "";
	public static final String NULL_STRING = "null";
	private static final String DEFAULT_SPLIT = "\\s|,|\\|";

	public static String toString(Object value) {
		return String.valueOf(value);
	}

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input) || "null".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 验证字符串是否为不为�?
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 验证是否为数字字符串
	 */
	public  static boolean isNumric(String str){
		Pattern pattern = Pattern.compile("^[0-9.]+$");
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() ){
			return false;
		}
		return true;
	}

	/**
	 * 默认空格，�?，�?，�?|�?
	 * 
	 * @param string
	 * @return
	 */
	public static String[] split(String string) {
		if (isEmpty(string)) {
			return null;
		}
		return string.split(DEFAULT_SPLIT);
	}

	public static String[] split(String string, String split) {
		if (isEmpty(string)) {
			return null;
		}
		if (isEmpty(split)) {
			return new String[] { string };
		}
		return string.split(split);
	}

	public static String filter(String str, String filterStr) {
		if (isEmpty(str))
			return EMPTY_SRING;
		if (isEmpty(filterStr)) {
			return str;
		}
		return str.replaceAll(filterStr, EMPTY_SRING);
	}

	/**
	 * 验证邮箱是否有效
	 * 
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		String format = "\\p{Alpha}\\w{3,15}[@][a-z0-9]{1,}[.]\\p{Lower}{2,}";

		if (email.matches(format)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 验证手机号是否有效
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);

		return m.matches();
	}

	/**
	 * 设置一串字符不同的样式
	 * 
	 * @param text
	 *            文本
	 * @param from
	 *            开始项，从第0项算起
	 * @param to
	 *            结束项
	 * @param dip
	 *            字号，0为不设置，下同
	 * @param color
	 *            颜色
	 * @param style
	 *            粗细
	 * @return Spannable
	 */
	public static Spannable getSpecicalText(String text, int from, int to, int dip, int color, int style) {
		Spannable span = new SpannableString(text);
		if (0 != dip) {
			span.setSpan(new AbsoluteSizeSpan(dip, true), from, to, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if (0 != style) {
			span.setSpan(new StyleSpan(style), from, to, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if (0 != color) {
			span.setSpan(new ForegroundColorSpan(color), from, to, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return span;
	}

}
