package com.skillbox.socialnetwork.main.util;

import java.util.Random;

public class CodeGenerator {
    private static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int MIN_CODE_SIZE = 5;
    private static final int MAX_CODE_SIZE = 10;


    public static String codeGenerator(){
        Random random = new Random();
        int size = MIN_CODE_SIZE + random.nextInt(MAX_CODE_SIZE - MIN_CODE_SIZE);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size ; i++){
            sb.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));
        }
        return sb.toString();
    }
}
