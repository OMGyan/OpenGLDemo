package com.yx.airhockey2.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Author by YX, Date on 2020/6/4.
 */
public class TextResourceReader {
    
    /**
     * 读取raw文件
     * @param context
     * @param rawId 文件id
     * @return
     */
    public static String readRawText(Context context, int rawId){
        InputStream inputStream = context.getResources().openRawResource(rawId);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer sb = new StringBuffer();
        String line;
        try {
            while ((line = bufferedReader.readLine())!=null){
                sb.append(line).append("\n");
            }
            bufferedReader.close();
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

}
