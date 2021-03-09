/**
 * $Author: wuym $
 * $Rev: 155 $
 * $Date:: 2012-07-09 15:08:37#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.util;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import org.apache.commons.logging.Log;

import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;

/**
 * <p>Title: T1开发框架</p>
 * <p>Description: 字符串处理工具类。</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 * @since CTP2.0
 */
public class StringUtil {

    private static final Log LOGGER = CtpLogFactory.getLog(StringUtil.class);

    private StringUtil(){
    	
    }
    /**
     * 如果是null字符串, 则返回""字符串
     *
     * @param str
     *            String
     * @return String
     */
    public static String filterNull(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * 如果是null对象, 则返回""字符串
     *
     * @param obj
     *            Object
     * @return String
     * @deprecated
     */
    @Deprecated
    public static String filterNullObject(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return obj.toString();
        }
    }

    /**
     * 传入的字符串如果为null，则返回""字符串，否则原样返回。
     *
     * @param str
     *            要处理的字符串
     * @return 结果字符串
     */
    public static String stringToString(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * 转换XML特殊字符。
     *
     * @param val
     *            要转换的字符串
     * @return 转换后的字符串
     */
    public static String deal(String val) {

        val = replace(val, "\"", "’");
        val = replace(val, ">", "’");
        val = replace(val, "<", "’");
        return val;
    }

    /**
     * Replace substrings of one string with another string and return altered
     * string.
     *
     * @param original
     *            input string
     * @param oldString
     *            the substring section to replace
     * @param newString
     *            the new substring replacing old substring section
     * @return converted string
     */
    public static String replace(final String original, final String oldString, final String newString) {
        return replace(original, oldString, newString, 0);
    }

    /**
     * Replace substrings of one string with another string and return altered
     * string.
     *
     * @param original
     *            input string
     * @param oldString
     *            the substring section to replace
     * @param newString
     *            the new substring replacing old substring section
     * @param counts
     *            how many times the replace happen, 0 for all.
     * @return converted string
     */
    public static String replace(final String original, final String oldString, final String newString, final int counts) {
        if (original == null || oldString == null || newString == null) {
            return "";
        }
        if (counts < 0)
            throw new IllegalArgumentException("parameter counts can not be negative");

        final StringBuilder sb = new StringBuilder();

        int end = original.indexOf(oldString);
        int start = 0;
        final int stringSize = oldString.length();

        int currentCount = 0;
        while (end != -1) {
            if (counts == 0 || currentCount < counts) {
                sb.append(original.substring(start, end));
                sb.append(newString);
                start = end + stringSize;
                end = original.indexOf(oldString, start);
                currentCount++;
            } else
                break;
        }

        end = original.length();
        sb.append(original.substring(start, end));

        return sb.toString();
    }

    /**
     * 把由delim分割的字符串分裂并形成字符串数组。 例如： String sourceString =
     * "string1;string2;string3"; String[] result =
     * StringUtility.split(sourceString, ";");
     * 则result是由字符串"string1","string2"和"string3"组成的数组。
     *
     * @param sourceString
     *            要分裂的字符串
     * @param delim
     *            分隔符
     * @return 分裂并组合后的字符串数组
     */
    public static String[] split(String sourceString, String delim) {
        if (sourceString == null || delim == null)
            return new String[0];
        StringTokenizer st = new StringTokenizer(sourceString, delim);
        List stringList = new ArrayList();
        for (; st.hasMoreTokens(); stringList.add(st.nextToken()))
            ;
        return (String[]) (stringList.toArray(new String[stringList.size()]));
        // TODO 此方法当delim为多字符的字符串时有问题，她只按delim的首字符分隔
        // 故提供split(String str, String delimiters, boolean skipDelim)
    }

    /**
     * added by chenjie 把由delim分割的字符串分裂并形成字符串数组，并将分割的字符串两边的空格去除。
     *
     * @param src
     * @param delim
     * @return
     */
    public static String[] splitAndTrim(String src, String delim) {
        if (src == null || delim == null)
            return new String[0];
        StringTokenizer st = new StringTokenizer(src, delim);
        List stringList = new ArrayList();
        for (; st.hasMoreTokens(); stringList.add(st.nextToken().trim()))
            ;
        return (String[]) (stringList.toArray(new String[stringList.size()]));
    }

    /**
     * 把以delimiters分隔的字符串分裂成字符串数组。
     *
     * @param str
     *            要分裂的字符串
     * @param delimiters
     *            分隔字符串
     * @param skipDelim
     *            当分隔字符串连续出现时或者以分隔字符串开始、结束时，true跳过空字符串，false输出空字符串
     * @return 分裂后的字符串数组
     */
    public static String[] split(String str, String delimiters, boolean skipDelim) {
        // TODO 还有一些情况没有处理：
        // 1、当分裂字符串里有""出现，可能""之间内容当作一个整体，不可分隔。
        // 2、当出现第一种情况时，若分隔字符串里本身也存在""，
        // 象上述复杂情况可以考虑一个PowerfulTokenizer类（扩展java.util.StringTokenizer）来解析
        if (str == null || delimiters == null) {
            return new String[0];
        }
        List strList = new ArrayList();
        int start = 0;
        int end = str.length();

        while (start <= end) {
            int delimIdx = str.indexOf(delimiters, start);
            if (delimIdx < 0) {
                String tok = str.substring(start);
                if (!skipDelim || !"".equals(tok)) {
                    strList.add(tok);
                }
                start = end + delimiters.length();
            } else {
                String tok = str.substring(start, delimIdx);
                if (!skipDelim || !"".equals(tok)) {
                    strList.add(tok);
                }
                start = delimIdx + delimiters.length();
            }
        }
        return (String[]) strList.toArray(new String[strList.size()]);
    }

    /**
     * 首字母大写
     * @param str
     * @return
     */
    public static String firstCharUp(String str) {
        return str.replaceFirst("" + str.charAt(0), ("" + str.charAt(0)).toUpperCase());
    }

    /**
     * 首字母小写
     */
    public static String firstCharLower(String str) {
        return str.replaceFirst("" + str.charAt(0), ("" + str.charAt(0)).toLowerCase());
    }

    /**
     * @param clazz 目标类型
     * @param str
     * @return	目标对象
     */
    public static Object convertStringToOther(Class clazz, String str, String dateFormat) throws Exception {
        if(clazz.isAssignableFrom(Date.class)){
            if (dateFormat == null || "".equals(dateFormat))
                return DateUtil.parse(str);
            else
                return DateUtil.parse(str, dateFormat);
        } else {
            Class[] parameterTypes = new Class[] { str.getClass() };
            Constructor c = clazz.getConstructor(parameterTypes);
            Object o = c.newInstance(new Object[] { str });
            return o;
        }

    }

    /**
     * @param obj
     *            要判断的是否为空的对象
     * @param info
     *            如果该对象为空抛出的提示信息
     */
    public static void judgeNull(final Object obj, final String info) throws BusinessException {
        if (obj == null || "".equals(obj.toString().trim())) {
            throw new BusinessException(info);
        }
    }

    public static byte[] compress(String s) {
        byte[] input = null;

        input = s.getBytes();

        // Create the compressor with highest level of compression
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);

        // Give the compressor the data to compress
        compressor.setInput(input);
        compressor.finish();

        // Create an expandable byte array to hold the compressed data.
        // You cannot use an array that's the same size as the orginal because
        // there is no guarantee that the compressed data will be smaller than
        // the uncompressed data.
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // Compress the data
        byte[] buf = new byte[1024];
//        boolean done = false;
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
//            if (count < buf.length) {
//                done = true;
//            }
            if (count > 0)
                bos.write(buf, 0, count);
        }
        try {
            bos.close();
        } catch (IOException e) {
        	LOGGER.warn(e.getLocalizedMessage(),e);
        }

        // Get the compressed data
        byte[] compressedData = bos.toByteArray();
        return compressedData;
    }

    public static String decompress(byte[] compressedData) {
        // Create the decompressor and give it the data to compress
        Inflater decompressor = new Inflater();
        decompressor.setInput(compressedData);

        // Create an expandable byte array to hold the decompressed data
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // Decompress the data
        byte[] buf = new byte[1024];
        boolean done = false;
        while (!done) {
            try {
                int count = decompressor.inflate(buf);
                if (count < buf.length) {
                    done = true;
                }
                if (count > 0) {
                    bos.write(buf, 0, count);
                }
            } catch (DataFormatException e) {
            	LOGGER.error(e.getLocalizedMessage(),e);
                return null;
            }
            Thread.currentThread().yield();
        }

        try {
            bos.close();
        } catch (IOException e) {
        	LOGGER.warn(e.getLocalizedMessage(),e);
        }

        // Get the decompresse data
        try {
            return bos.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 检查字符串是否为空或是特定空值
     * @param data
     * @deprecated "建议使用Apache Common lang的StringUtils.isEmpty"
     * @return
     */
    @Deprecated
    public static boolean checkNull(String data) {
        if ((data == null) || ("".equals(data)) || ("null".equalsIgnoreCase(data)))
            return true;
        return false;
    }

    /**
     * 将异常堆栈转换为字符串
     *
     * @param t 异常对象
     * @return 异常堆栈字符串
     */
    public static String toString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 合并字符串数组为一个字符串
     * 各元素间用 ，分割
     * @param strArray 数组
     * @return 空数组或者长度为0的数组，返回空串
     */
    public static String arrayToString(String[] strArray) {
        return arrayToString(strArray, ",");
    }

    /**
     * 合并字符串数组为一个字符串
     * @param strArray 数组
     * @param label 元素间区隔符
     * @return 空数组或者长度为0的数组，返回空串
     */
    public static String arrayToString(String[] strArray, String label) {
        if (strArray == null || strArray.length == 0) {
            return "";
        }
        StringBuilder returnValue = new StringBuilder("");
        for (int i = 0; i < strArray.length; i++) {
            returnValue.append(strArray[i] + (i == strArray.length - 1 ? "" : label));
        }
        return returnValue.toString();
    }
    
    /**
     *  将汉字转换为全拼
     */
	public static String getPingYin(String src) {
		if(Strings.isBlank(src)){
			return "";
		}
		char[] t1 = null;
		t1 = src.toCharArray();
		String[] t2 = new String[t1.length];
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				// 判断是否为汉字字符
				if (java.lang.Character.toString(t1[i]).matches(
						"[\\u4E00-\\u9FA5]+")) {
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
					t4 += t2[0];
				} else
					t4 += java.lang.Character.toString(t1[i]);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return t4;
	}

	/**
	 * 返回中文的首字母
	 * @param str
	 * @return
	 */
	public static String getPinYinHeadChar(String str) {
		if(Strings.isBlank(str)){
			return "";
		}
		String convert = "";
		try {
			for (int j = 0; j < str.length(); j++) {	
				char word = str.charAt(j);
				String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
				if (pinyinArray != null) {
					convert += pinyinArray[0].charAt(0);
				} else if (!java.lang.Character.toString(word).matches(
						"[\\u4E00-\\u9FA5]+")) {
					convert += word;
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return convert;
	}
	
	/**
	 *    只返回开头的第一个字母
	 * @param str
	 * @return
	 */
	public static String getFirstPinYinHeadChar(String str) {
		if(Strings.isBlank(str)){
			return "";
		}
		String convert = "";
		try {
			char word = str.charAt(0);
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null) {
				convert = java.lang.Character.toString(pinyinArray[0].charAt(0));
			} else if (!java.lang.Character.toString(word).matches(
					"[\\u4E00-\\u9FA5]+")) {
				convert = java.lang.Character.toString(word);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return convert;
	}
	
	/**
	 * 获取密码强度
	 * @param passwd
	 * @return
	 */
	public static String getPasswdStrong(String passwd){
		String result = "4";
		if(Strings.isBlank(passwd)){
			return result;
		}
		
		try {
			SingletonInvocable instance = SingletonInvocable.getInstance();
			Invocable invoke = instance.getScriptInvocable();
			if(invoke!=null){
				result = invoke.invokeFunction("getPwdStrongForLoginPage", passwd).toString();
			}
				
		} catch (Exception e) {
			LOGGER.error("获取密码强度出错，默认密码强度为最强！",e);
		} 
		return result;
		
	}
	
	
    /**
          * 验证所有的身份证的合法性
     * 
     * @param idcard
          *  身份证
     * @return 合法返回true，否则返回false
     */
    public static boolean isValidatedIdcard(String idcard) {
        if (idcard == null || "".equals(idcard)) {
            return false;
        }
        int s=15;
        if (idcard.length() == s) {
            return validate15IDCard(idcard);
        }
        int s1=18;
        if(idcard.length()==s1) {
            return validate18Idcard(idcard);
        }
        return false;
    }
    private static String[] cityCode = { "11", "12", "13", "14", "15", "21",
            "22", "23", "31", "32", "33", "34", "35", "36", "37", "41", "42",
            "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62",
            "63", "64", "65", "71", "81", "82", "91" };

    private static int power[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5,
            8, 4, 2 };
    private static boolean validate18Idcard(String idcard) {
        if (idcard == null) {
            return false;
        }
 
        // 非18位为假
        int s=18;
        if (idcard.length() != s) {
            return false;
        }
        // 获取前17位
        String idcard17 = idcard.substring(0, 17);
 
        // 前17位全部为数字
        if (!isDigital(idcard17)) {
            return false;
        }
 
        String provinceid = idcard.substring(0, 2);
        // 校验省份
        if (!checkProvinceid(provinceid)) {
            return false;
        }
 
        // 校验出生日期
        String birthday = idcard.substring(6, 14);
 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
 
        try {
            Date birthDate = sdf.parse(birthday);
            String tmpDate = sdf.format(birthDate);
            // 出生年月日不正确
            if (!tmpDate.equals(birthday)) {
                return false;
            }
 
        } catch (Exception e1) {
            return false;
        }
 
        // 获取第18位
        String idcard18Code = idcard.substring(17, 18);
 
        char c[] = idcard17.toCharArray();
 
        int bit[] = converCharToInt(c);
 
        int sum17 = 0;
 
        sum17 = getPowerSum(bit);
 
        // 将和值与11取模得到余数进行校验码判断
        String checkCode = getCheckCodeBySum(sum17);
        if (null == checkCode) {
            return false;
        }
        // 将身份证的第18位与算出来的校码进行匹配，不相等就为假
        if (!idcard18Code.equalsIgnoreCase(checkCode)) {
            return false;
        }
        return true;
    }
    private static boolean validate15IDCard(String idcard) {
        if (idcard == null) {
            return false;
        }
        // 非15位为假
        int s=15;
        if (idcard.length() != s) {
            return false;
        }
 
        // 15全部为数字
        if (!isDigital(idcard)) {
            return false;
        }
 
        String provinceid = idcard.substring(0, 2);
        // 校验省份
        if (!checkProvinceid(provinceid)) {
            return false;
        }
 
        String birthday = idcard.substring(6, 12);
 
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
 
        try {
            Date birthDate = sdf.parse(birthday);
            String tmpDate = sdf.format(birthDate);
            // 身份证日期错误
            if (!tmpDate.equals(birthday)) {
                return false;
            }
 
        } catch (Exception e1) {
            return false;
        }
 
        return true;
    }
    private static boolean checkProvinceid(String provinceid) {
        for (String id : cityCode) {
            if (id.equals(provinceid)) {
                return true;
            }
        }
        return false;
    }
    private static boolean isDigital(String str) {
        return str.matches("^[0-9]*$");
    }
    private static int getPowerSum(int[] bit) {
 
        int sum = 0;
 
        if (power.length != bit.length) {
            return sum;
        }
 
        for (int i = 0; i < bit.length; i++) {
            for (int j = 0; j < power.length; j++) {
                if (i == j) {
                    sum = sum + bit[i] * power[j];
                }
            }
        }
        return sum;
    }
    private static String getCheckCodeBySum(int sum17) {
        String checkCode = null;
        switch (sum17 % 11) {
        case 10:
            checkCode = "2";
            break;
        case 9:
            checkCode = "3";
            break;
        case 8:
            checkCode = "4";
            break;
        case 7:
            checkCode = "5";
            break;
        case 6:
            checkCode = "6";
            break;
        case 5:
            checkCode = "7";
            break;
        case 4:
            checkCode = "8";
            break;
        case 3:
            checkCode = "9";
            break;
        case 2:
            checkCode = "x";
            break;
        case 1:
            checkCode = "0";
            break;
        case 0:
            checkCode = "1";
            break;
        default:
        }
        return checkCode;
    }
    private static int[] converCharToInt(char[] c) throws NumberFormatException {
        int[] a = new int[c.length];
        int k = 0;
        for (char temp : c) {
            a[k++] = Integer.parseInt(String.valueOf(temp));
        }
        return a;
    }
	
}

  class SingletonInvocable {
    private static SingletonInvocable instance;
    private static Invocable invoke = null;
    private SingletonInvocable(){

    }
    public static synchronized SingletonInvocable getInstance(){
        if(instance==null){
            instance=new SingletonInvocable();
        	ScriptEngineManager manager = new ScriptEngineManager();   
    		ScriptEngine engine = manager.getEngineByName("javascript");  
    		String jsFileName = SystemEnvironment.getApplicationFolder() + "/common/js/passwdcheck.js";   // 读取js文件   
    		FileReader reader = null;
    		try {
    			reader = new FileReader(jsFileName);
    			engine.eval(reader);   
    			if(engine instanceof Invocable) {
    				invoke = (Invocable)engine;
    			}   
    			reader.close();  
    		} catch (Exception e) {
    		}
    		
        }
        return instance;
    }
    
    public Invocable getScriptInvocable(){
    	return invoke;
    }
}