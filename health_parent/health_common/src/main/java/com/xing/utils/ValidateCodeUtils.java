package com.xing.utils;

import java.util.Random;

/**
 * generate validation code
 */
public class ValidateCodeUtils {
    /**
     * @param length between 4 and 6
     * @return
     */
    public static Integer generateValidateCode(int length){
        Integer code =null;
        if(length == 4){
            code = new Random().nextInt(9999);// max random number is 9999
            if(code < 1000){
                code = code + 1000;// random number should be 4 digits
            }
        }else if(length == 6){
            code = new Random().nextInt(999999);
            if(code < 100000){
                code = code + 100000;
            }
        }else{
            throw new RuntimeException("Validation code length should be 4 or 6.");
        }
        return code;
    }

    public static String generateValidateCode4String(int length){
        Random rdm = new Random();
        String hash1 = Integer.toHexString(rdm.nextInt());
        String capstr = hash1.substring(0, length);
        return capstr;
    }
}
