package cn.huanzi.qch.baseadmin.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * RSA加、解密算法工具类
 */
@Component
@Slf4j
public class RsaUtilRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(RsaUtilRedis.class);


    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    RedisTemplate redisTemplate;

    /**
     * 加密算法AES
     */
    private final String KEY_ALGORITHM = "RSA";

    /**
     * 算法名称/加密模式/数据填充方式
     * 默认：RSA/ECB/PKCS1Padding
     */
    private final String ALGORITHMS = "RSA/ECB/PKCS1Padding";

    /**
     * Map获取公钥的key
     */
    private final String PUBLIC_KEY = "publicKey";

    /**
     * Map获取私钥的key
     */
    private final String PRIVATE_KEY = "privateKey";

    /**
     * RSA最大加密明文大小
     */
    private final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private final int MAX_DECRYPT_BLOCK = 128;

    /**
     * RSA 位数 如果采用2048 上面最大加密和最大解密则须填写:  245 256
     */
    private final int INITIALIZE_LENGTH = 1024;

    /**
     * 后端RSA的密钥对(公钥和私钥)Map，由静态代码块赋值
     */
    private Map<String, Object> genKeyPair = new LinkedHashMap<>();

    /*static {
        try {
            genKeyPair.putAll(genKeyPair());
        } catch (Exception e) {
            //输出到日志文件中
            log.error(ErrorUtil.errorInfoToString(e));
        }
    }*/

    private boolean init = false;
    /**
     * 生成密钥对(公钥和私钥)
     */
    private Map<String, Object> genKeyPair() throws Exception {
        /*KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(INITIALIZE_LENGTH);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        //公钥
        keyMap.put(PUBLIC_KEY, publicKey);
        //私钥
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;*/
        if(!init){
            synchronized(genKeyPair) {
                if(!init) {
                    Map<String, Object> result = genKeyPairFromRedis();
                    genKeyPair.putAll(result);
                    init = true;
                }
            }
        }

        return genKeyPair;
    }

    private Map<String, Object>  genKeyPairFromRedis() throws Exception {

        //redis 获取锁
        Boolean result = redisTemplate.opsForValue().setIfAbsent("genKeyPairFromRedisLock", "1", 10, TimeUnit.SECONDS);
        if(result){
            System.out.println("redis 获取锁genKeyPairFromRedisLock ");
            //获取redis锁
            boolean publicKeyFlag = redisTemplate.hasKey(PUBLIC_KEY);
            boolean privateKeyFlag = redisTemplate.hasKey(PRIVATE_KEY);
            if(publicKeyFlag&&privateKeyFlag){
                Object publicKey = redisTemplate.opsForValue().get(PUBLIC_KEY);
                Object privateKey = redisTemplate.opsForValue().get(PRIVATE_KEY);

                Map<String, Object> keyMap = new HashMap<String, Object>(2);
                //公钥
                keyMap.put(PUBLIC_KEY, publicKey);
                //私钥
                keyMap.put(PRIVATE_KEY, privateKey);
                LOGGER.info("获得锁 redis锁 ,获取已有 publicKey信息 privateKey信息");
                return keyMap;

            }else{
                //redis不存在publicKey信息 或者 privateKey信息 重新设置

                KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
                keyPairGen.initialize(INITIALIZE_LENGTH);
                KeyPair keyPair = keyPairGen.generateKeyPair();
                RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
                RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

                //不存在就保存 存在就更新
                redisTemplate.opsForValue().set(PUBLIC_KEY, publicKey);
                redisTemplate.opsForValue().set(PRIVATE_KEY, privateKey);

                Map<String, Object> keyMap = new HashMap<String, Object>(2);
                //公钥
                keyMap.put(PUBLIC_KEY, publicKey);
                //私钥
                keyMap.put(PRIVATE_KEY, privateKey);
                System.out.println("获得锁 redis锁 ,设置 publicKey信息 privateKey信息");
                LOGGER.info("获得锁 redis锁 ,设置 publicKey信息 privateKey信息");
                return keyMap;
            }

        }else{
            System.out.println("redis 没有获取锁genKeyPairFromRedisLock ");
            //没有获得锁
            boolean publicKeyFlag = redisTemplate.hasKey(PUBLIC_KEY);
            boolean privateKeyFlag = redisTemplate.hasKey(PRIVATE_KEY);
            if(publicKeyFlag&&privateKeyFlag){
                Object publicKey = redisTemplate.opsForValue().get(PUBLIC_KEY);
                Object privateKey = redisTemplate.opsForValue().get(PRIVATE_KEY);

                Map<String, Object> keyMap = new HashMap<String, Object>(2);
                //公钥
                keyMap.put(PUBLIC_KEY, publicKey);
                //私钥
                keyMap.put(PRIVATE_KEY, privateKey);
                LOGGER.info("没有获得锁 redis锁 ,获取已有 publicKey信息 privateKey信息");
                return keyMap;

            }

            System.out.println("没有获得锁 redis锁 ,正在等待 设置 publicKey信息 privateKey信息");
            throw new Exception("没有获得锁 redis锁 ,正在等待 设置 publicKey信息 privateKey信息");
        }


    }

    /**
     * 私钥解密
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     */
    public  byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
        //base64格式的key字符串转Key对象
        Key privateK = KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey)));

        //设置加密、填充方式
        /*
            如需使用更多加密、填充方式，引入
            <dependency>
                <groupId>org.bouncycastle</groupId>
                <artifactId>bcprov-jdk16</artifactId>
                <version>1.46</version>
            </dependency>
            并改成
            Cipher cipher = Cipher.getInstance(ALGORITHMS ,new BouncyCastleProvider());
         */
        Cipher cipher = Cipher.getInstance(ALGORITHMS);
        cipher.init(Cipher.DECRYPT_MODE, privateK);

        //分段进行解密操作
        return encryptAndDecryptOfSubsection(encryptedData, cipher, MAX_DECRYPT_BLOCK);
    }

    /**
     * 公钥加密
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     */
    public byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        //base64格式的key字符串转Key对象
        Key publicK = KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(publicKey)));

        //设置加密、填充方式
        /*
            如需使用更多加密、填充方式，引入
            <dependency>
                <groupId>org.bouncycastle</groupId>
                <artifactId>bcprov-jdk16</artifactId>
                <version>1.46</version>
            </dependency>
            并改成
            Cipher cipher = Cipher.getInstance(ALGORITHMS ,new BouncyCastleProvider());
         */
        Cipher cipher = Cipher.getInstance(ALGORITHMS);
        cipher.init(Cipher.ENCRYPT_MODE, publicK);

        //分段进行加密操作
        return encryptAndDecryptOfSubsection(data, cipher, MAX_ENCRYPT_BLOCK);
    }

    /**
     * 获取私钥
     */
    public String getPrivateKey() throws Exception {
        genKeyPair();
        Key key = (Key) genKeyPair.get(PRIVATE_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 获取公钥
     */
    public String getPublicKey() throws Exception {
        genKeyPair();
        Key key = (Key) genKeyPair.get(PUBLIC_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 分段进行加密、解密操作
     */
    private byte[] encryptAndDecryptOfSubsection(byte[] data, Cipher cipher, int encryptBlock) throws Exception {
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > encryptBlock) {
                cache = cipher.doFinal(data, offSet, encryptBlock);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * encryptBlock;
        }
        out.close();
        return out.toByteArray();
    }
}
