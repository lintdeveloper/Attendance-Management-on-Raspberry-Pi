/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Abbasogaji
 */
public class InputData {
    
    public static boolean validateUsername(String username){
         Pattern p = Pattern.compile("^[A-Za-z0-9]{5,}+$");
         return p.matcher(username).matches();
    }
    public static boolean validateName(String name){
         Pattern p = Pattern.compile("^[ A-Za-z]{3,}+$");
         return p.matcher(name).matches();
    }
    
    public static boolean validatePassword(String pwd){
         Pattern p = Pattern.compile("^[A-Za-z0-9]{5,}+$");
         return p.matcher(pwd).matches();
    }
    
    public static boolean validateMatricNumber(String matric){
        Pattern p = Pattern.compile("[0-9]{4}+/[1-2]{1}+/[A-Za-z0-9]{7}");
        return p.matcher(matric).matches();
    }
    
    public static boolean checkPasswords(String pwdOne, String pwdTwo){
        return pwdOne.equals(pwdTwo);        
    }

    public static boolean validateEmail(String email) {
        Pattern p = Pattern.compile("[a-zA-Z0-9._%-]+@[A-Za-z0-9._-]+\\.[A-Za-z]{2,4}");
        return p.matcher(email).matches();
    }

    public static boolean validateMobileNumber(String phone) {
        Pattern p = Pattern.compile("^[0]+[0-9]{10}+$");
        return p.matcher(phone).matches();
    }

    public static boolean validateCourseCode(String cc) {
        Pattern p = Pattern.compile("^[A-Za-z]{3}+[0-9]{3}+$");
        return p.matcher(cc).matches();    
    }
    public static double extractDouble(String txt){
        double data = 0;
        Pattern p = Pattern.compile("^[0-9.]+|[0-9]{2,}");
        Matcher cond = p.matcher(txt);
        if(cond.find()){
            if(cond.group().length()!=0){
                data =  Double.parseDouble(cond.group().trim());
            }
        }else{
             
        }
        return data;
    }
    public static int extractInt(String txt){
        int data = 0;
        Pattern p = Pattern.compile("[1-9]{1,}");
        Matcher cond = p.matcher(txt);
        if(cond.find()){
            if(cond.group().length()!=0){
                data =  Integer.parseInt(cond.group().trim());
            }
        }else{
             
        }
        return data;
    }
    
}
