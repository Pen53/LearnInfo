package cn.huanzi.qch.springbootredis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;


@RestController
public class DemoController {
    @Autowired
    private StringRedisTemplate template;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 锁获取
     * @param key
     * @return
     */
    @RequestMapping("/redisLock/redis/get/{key}")
    private String getRedisLockRedisTemplate01(@PathVariable("key") String key){
        System.out.println("ThreadName:"+Thread.currentThread().getName()+",getRedisLockRedisTemplate01 lock is doing..");
        String lockKey = "redisLockDemo01";
        try {
            do {
                Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockKey, "uuid");
                //如果没有 获得锁，lockKey失效时间 -1 永不失效，所以下次获取锁永远获取不到，需要手动设置失效
                if (lock) {
                    System.out.println("获得锁 ThreadName:"+Thread.currentThread().getName()+",lock:"+lock);
                    try {
                        redisTemplate.opsForValue().set(lockKey,"uuid",20L,TimeUnit.SECONDS);//防止一个锁死锁（redis或程序意外停止，造成未释放锁） 设置锁最多存活20S
                        Thread.sleep(200_000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }else{
                    System.out.println("没有获得锁 ThreadName:"+Thread.currentThread().getName()+",lock:"+lock);
                    try {
                        Thread.sleep(5_000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } while (true);
        }finally {
            String verfiy = (String) redisTemplate.opsForValue().get(lockKey);
            System.out.println("释放锁 verfiy lockKey"+lockKey+",uuid:"+verfiy);
            redisTemplate.delete(lockKey);
        }

        return redisTemplate.opsForValue().get(key)==null?"":redisTemplate.opsForValue().get(key).toString();
    }
    /**
     * StringRedisTemplate 锁获取
     * @param key
     * @return
     */
    @RequestMapping("/redisLockRedisTemplate/redis/get2/{key}")
    private String getRedisLockRedisTemplate02(@PathVariable("key") String key){
        System.out.println("ThreadName:"+Thread.currentThread().getName()+",getRedisLockRedisTemplate02 lock is doing..");
        String lockKey = "redisLockDemo02";
        do{
            Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockKey, "uuid", 20L, TimeUnit.SECONDS);//获得锁 设置锁最多存活20S
            System.out.println("ThreadName:"+Thread.currentThread().getName()+",lock:"+lock);
            if(lock){
                System.out.println("ThreadName:"+Thread.currentThread().getName()+", 获得 lock:"+lock);
                try {
                    Thread.sleep(1_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }else{
                System.out.println("ThreadName:"+Thread.currentThread().getName()+", 没有获得 lock:"+lock);
            }
        }while (true);


        return redisTemplate.opsForValue().get(key)==null?"":redisTemplate.opsForValue().get(key).toString();
    }

    /**
     * StringRedisTemplate 锁获取
     * @param key
     * @return
     */
    @RequestMapping("/lockStringRedisTemplate/redis/get2/{key}")
    private String getLockStringRedisTemplate(@PathVariable("key") String key){
        System.out.println("ThreadName:"+Thread.currentThread().getName()+",lock is doing..");
        String lockKey = "demo";

        do{
            Boolean lock = template.opsForValue().setIfAbsent(lockKey, "uuid", 10, TimeUnit.SECONDS);//获得锁 设置锁最多存活10S
            System.out.println("ThreadName:"+Thread.currentThread().getName()+",lock:"+lock);
            if(lock){
                System.out.println("ThreadName:"+Thread.currentThread().getName()+", 获得 lock:"+lock);
                try {
                    Thread.sleep(20_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }else{
                System.out.println("ThreadName:"+Thread.currentThread().getName()+", 没有获得 lock:"+lock);
            }
        }while (true);


        return template.opsForValue().get(key);
    }

}
