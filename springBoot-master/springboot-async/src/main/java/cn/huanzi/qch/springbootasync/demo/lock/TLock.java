package cn.huanzi.qch.springbootasync.demo.lock;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TLock {
    Lock lock = new ReentrantLock();
    int i ;
    public TLock(int i){
        this.i = i;
    }
    public void t1(int ind){
        System.out.println("ind:"+ind+",ThreadName=" + Thread.currentThread().getName() +",i:"+i+",is running");
        lock.lock();
        try{
            System.out.println("ind:"+ind+"ThreadName=" + Thread.currentThread().getName() +",i:"+i+" get lock.");
            int sleep = i*new Random().nextInt(100000);
            System.out.println("ind:"+ind+"ThreadName=" + Thread.currentThread().getName()+" next sleep:"+sleep);
            Thread.sleep(sleep);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
            System.out.println("ind:"+ind+",ThreadName=" + Thread.currentThread().getName() +" lock.unlock()");
        }
    }

    public static void main(String[] args) {
        TLock tl = new TLock(2);
        for(int i=0;i<10;i++){
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Thread finalI:"+finalI+"is doing.");
                    tl.t1(finalI);
                }
            }).start();
        }
    }
}
