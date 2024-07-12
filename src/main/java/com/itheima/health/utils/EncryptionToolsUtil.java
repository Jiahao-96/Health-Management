package com.itheima.health.utils;

import com.itheima.health.common.PasswordMethodConst;
import com.itheima.health.exception.BusinessRuntimeException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;


@Component
@Slf4j
@Data
@ConfigurationProperties(prefix = "health.pbkdf2")
public  class EncryptionToolsUtil {

    private String salt;
    private Integer iterations;
    private Integer keyLength;
    private String sceretKey;



    /**
     * 校验密码
     * @param password
     * @param passwordInDatabase
     * @return
     */
    public  Boolean checkPassword(String password,String passwordInDatabase)  {

        //获取加密类型
        String encryptType = passwordInDatabase.substring(passwordInDatabase.indexOf("{"), passwordInDatabase.lastIndexOf("}")+1);
        //截取用户真正的密码
        String truePassword = passwordInDatabase.substring(passwordInDatabase.lastIndexOf("}") + 1);

        try {

            //辨别加密方式
            switch (encryptType){
                case PasswordMethodConst.MD5: return md5Check(password,truePassword);
                case PasswordMethodConst.BCRYPT: return bcryptCheck(password,truePassword);
                case PasswordMethodConst.PBKDF2: return pbkdf2Check(password,truePassword);
            }

        } catch (Exception e) {
            log.info("密码比对发生错误");
            log.error("{}",e);
            throw new BusinessRuntimeException("密码比对错误");
        }

        return false;
    }



    /**
     *  MD5加密
     * @param password
     * @param truePassword
     * @return
     */
    public  Boolean  md5Check(String password,String truePassword){
        log.info("进行md5比对");
        return truePassword.equals(DigestUtils.md5DigestAsHex(password.getBytes()));
    }


    /**
     *  Bcrypt加密校验
     * @param password
     * @param truePassword
     * @return
     */
    public  Boolean bcryptCheck(String password,String truePassword){
        log.info("进行bcrypt比对");
        return BCrypt.checkpw(password,truePassword);
    }


    /**
     * pbkdf2加密校验
     * @param password
     * @param truePassword
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public  Boolean pbkdf2Check(String password,String truePassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.info("进行pbkdf2比对");

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),salt.getBytes(),iterations,keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(sceretKey);
        byte[] hashedPassWord =  keyFactory.generateSecret(spec).getEncoded();

        //以base64的形式输出密文
        String base64HashedPassword = Base64.getEncoder().encodeToString(hashedPassWord);

        return truePassword.equals(base64HashedPassword);
    }





}
