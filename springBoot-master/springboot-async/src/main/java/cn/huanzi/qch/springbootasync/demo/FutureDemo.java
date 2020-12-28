package cn.huanzi.qch.springbootasync.demo;

import java.util.*;
import java.util.concurrent.*;

public class FutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //创建线程池
        ExecutorService es = Executors.newFixedThreadPool(10);//Executors.newSingleThreadExecutor();
        try {
            List<Future> futureList = new ArrayList<Future>();
            for (int i = 0; i < 36; i++) {
           /* //创建Callable对象任务
            CallableDemo calTask=new CallableDemo(i);
            //提交任务并获取执行结果
            Future<Map> future =es.submit(calTask);*/
                //futureList.add(future);

                int finalI = i;
                Future<Map<String, Object>> ft1 = es.submit(new Callable<Map<String, Object>>() {
                    int ind = finalI;

                    @Override
                    public Map<String, Object> call() throws Exception {
                        System.out.println("CallableDemo task is doing.. ind:" + ind);
                        Map<String, Object> result = new HashMap<>();
                        result.put("curTime", new Date());
                        result.put("ind23", ind);
                        if (ind == 34) {
                            Thread.sleep(10_000);
                            throw new Exception("test exception "+ind);
                        }
                        return result;
                    }
                });
                futureList.add(ft1);

                if (i % 10 == 0) {
                    System.out.println("i:" + i + "-----------666666666------------------------futureList.size():" + futureList.size());
                    for (Future future : futureList) {
                        if (future.get() != null) {
                            //输出获取到的结果
                            System.out.println("future.get()-->" + future.get());
                        } else {
                            //输出获取到的结果
                            System.out.println("future.get()未获取到结果");
                        }
                    }
                    futureList.clear();
                }
            }
            if (!futureList.isEmpty()) {
                System.out.println("-----------666666666------------------------futureList.size():" + futureList.size());
                for (Future future : futureList) {
                    if (future.get() != null) {
                        //输出获取到的结果
                        System.out.println("future.get()-->" + future.get());
                    } else {
                        //输出获取到的结果
                        System.out.println("future.get()未获取到结果");
                    }
                }
            }
        }finally {
            //关闭线程池
            es.shutdown();
        }
        try {
            Thread.sleep(2000);
            System.out.println("主线程在执行其他任务");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("主线程在执行完成");
    }
    public static void main3(String[] args) {
        //创建线程池
        ExecutorService es = Executors.newFixedThreadPool(10);//Executors.newSingleThreadExecutor();
        List<Future> futureList = new ArrayList<Future>();
        for(int i=0;i<10;i++){
           /* //创建Callable对象任务
            CallableDemo calTask=new CallableDemo(i);
            //提交任务并获取执行结果
            Future<Map> future =es.submit(calTask);*/
            //futureList.add(future);

            int finalI = i;
            Future<Map<String, Object>> ft1 = es.submit(new Callable<Map<String, Object>>() {
                int ind = finalI;
                @Override
                public Map<String, Object> call() throws Exception {
                    System.out.println("CallableDemo task is doing..");
                    Map<String,Object> result = new HashMap<>();
                    result.put("curTime",new Date());
                    result.put("ind23",ind);
                    if(ind==6){
                        Thread.sleep(10_000);
                        throw new Exception("test exception");
                    }
                    return result;
                }
            });
            futureList.add(ft1);
        }
        //关闭线程池
        es.shutdown();
        try {
            Thread.sleep(2000);
            System.out.println("主线程在执行其他任务");
            for(Future future:futureList){
                if(future.get()!=null){
                    //输出获取到的结果
                    System.out.println("future.get()-->"+future.get());
                }else{
                    //输出获取到的结果
                    System.out.println("future.get()未获取到结果");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("主线程在执行完成");
    }
    public static void main2(String[] args) {
        //创建线程池
        ExecutorService es = Executors.newFixedThreadPool(10);//Executors.newSingleThreadExecutor();
        List<Future> futureList = new ArrayList<Future>();
        for(int i=0;i<10;i++){
            //创建Callable对象任务
            CallableDemo calTask=new CallableDemo(i);
            //提交任务并获取执行结果
            Future<Map> future =es.submit(calTask);
            futureList.add(future);
            
        }
        //关闭线程池
        es.shutdown();
        try {
            Thread.sleep(2000);
            System.out.println("主线程在执行其他任务");
            for(Future future:futureList){
                if(future.get()!=null){
                    //输出获取到的结果
                    System.out.println("future.get()-->"+future.get());
                }else{
                    //输出获取到的结果
                    System.out.println("future.get()未获取到结果");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("主线程在执行完成");
    }

}
