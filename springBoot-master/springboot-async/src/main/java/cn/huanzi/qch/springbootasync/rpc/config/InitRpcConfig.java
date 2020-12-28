package cn.huanzi.qch.springbootasync.rpc.config;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

//@Component
public class InitRpcConfig implements CommandLineRunner {
    @Autowired
    private ApplicationContext applicationContext;

    public static Map<String,Object> rpcServiceMap = new HashMap<>();

    @Override
    public void run(String... args) throws Exception {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Service.class);
        for (Object bean: beansWithAnnotation.values()){
            Class<?> clazz = bean.getClass();
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> inter : interfaces){
                rpcServiceMap.put(getClassName(inter.getName()),bean);
                //log.info("已经加载的服务:"+inter.getName());
                System.out.println("已经加载的服务:"+inter.getName());
            }
        }
    }

    private String getClassName(String beanClassName){
        String className = beanClassName.substring(beanClassName.lastIndexOf(".")+1);
        className = className.substring(0,1).toLowerCase() + className.substring(1);
        return className;
    }
}
