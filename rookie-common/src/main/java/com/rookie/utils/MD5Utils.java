package com.rookie.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;

public class MD5Utils {

    /**
     *
     * @Title: MD5Utils.java
     * @Package com.rookie.utils
     * @Description: 对字符串进行md5加密
     */
    public static String getMD5Str(String strValue) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String newstr = Base64.encodeBase64String(md5.digest(strValue.getBytes()));
        return newstr;
    }

    /**
     * 测试加密功能是否正常
     * @param args
     */
    public static void main(String[] args) {
        try {
            String md5 = getMD5Str("rookie");
            System.out.println(md5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
