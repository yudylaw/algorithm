package com.yudylaw.pearls;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author liuyu3@xiaomi.com
 * @since 2015年8月15日
 */

public class BitMap {

    static final int size = 1024 * 1024;
    
    static final int[] bitmap = new int[size];//4M
    
    public static void main(String[] args) {
        //TODO 用OOP来改造下BitMap
        initBitMap();
        loadToBigMap();
        printBitMap();
    }
    
    public static void initBitMap() {
        for (int i = 0; i < size; i++) {
            bitmap[i] = 0;
        }
    }
    
    public static void setToBitMap(int value) {
        for (int i = 0; i < size; i++) {
            if(i == value){
                bitmap[i] = value;
            }
        }
    }
    
    public static void printBitMap() {
        for (int i = 0; i < size; i++) {
            //TODO 优化
            if(i == 0 || bitmap[i] > 0){
                System.out.println(bitmap[i]);
            }
        }
    }
    
    public static void loadToBigMap() {

        BufferedReader reader = null;
        try {
            InputStream input = BitMap.class.getClassLoader().getResourceAsStream("bitmap.txt");
            reader = new BufferedReader(new InputStreamReader(input));
            String line = null;
            int size = 0;
            while((line = reader.readLine()) != null){
                try{
                    if(!StringUtils.isEmpty(line)){
                        int number = Integer.parseInt(line);
                        setToBitMap(number);
                        size++;
                    }
                }catch(Exception e){
                    System.out.println(line);
                }
            }
            System.out.println("numbers size " + size);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    
    }

}
