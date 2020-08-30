package org.fcx.mytool.util;

import lombok.extern.slf4j.Slf4j;
import org.fcx.mytool.exception.MyException;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
public class MyUtil {
    /**
     * Base64 Url 解码，UTF-8 字符集
     * @param raw 密文
     * @return 明文
     */
    public static String base64UrlDecode(String raw) {
        byte[] bs = Base64.getUrlDecoder().decode(raw.trim());
        try {
            String decodeStr = new String(bs, "UTF-8");
            return decodeStr;
        } catch (UnsupportedEncodingException e){
            MyException mye = new MyException("base64解码生成String，字符集异常："+e.getMessage());
            mye.setStackTrace(e.getStackTrace());
            throw mye;
        }
    }

    /**
     * Base64 Basic 解码，UTF-8 字符集
     * @param raw 密文
     * @return 明文
     */
    public static String base64Decode(String raw) {
        byte[] bs = Base64.getDecoder().decode(raw.trim());
        try {
            String decodeStr = new String(bs, "UTF-8");
            return decodeStr;
        } catch (UnsupportedEncodingException e){
            MyException mye = new MyException("base64解码生成String，字符集异常："+e.getMessage());
            mye.setStackTrace(e.getStackTrace());
            throw mye;
        }
    }

    public static List<String> readRules(InputStream in){
        List<String> rules = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String temp = null;
            while ( (temp = br.readLine()) != null){
                temp = temp.trim();
                if(!StringUtils.isEmpty(temp) && !temp.startsWith("#")){
                    rules.add(temp);
                }
            }
        } catch (IOException ex) {
            log.error("read rules err:",ex);
        }
        return rules;
    }

}
