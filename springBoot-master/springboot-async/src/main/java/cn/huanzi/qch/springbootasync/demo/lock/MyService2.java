package cn.huanzi.qch.springbootasync.demo.lock;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 1.Condition类的awiat方法和Object类的wait方法等效
 * 2.Condition类的signal方法和Object类的notify方法等效
 * 3.Condition类的signalAll方法和Object类的notifyAll方法等效
 * 4.ReentrantLock类可以唤醒指定条件的线程，而object的唤醒是随机
 */
public class MyService2 {
    Lock lock = new ReentrantLock();//默认非公平锁
    //Lock lock=new ReentrantLock(true);//公平锁
    // Lock lock=new ReentrantLock(false);//非公平锁
   private Condition condition=lock.newCondition();// 创建Condition
    private Condition conditionProductGood=lock.newCondition();// 创建Condition
    private Condition conditionCustomGood=lock.newCondition();// 创建Condition
    private int ind = 0;
    private int goodSize = 30;
    private int[] arr = new int[20];//20个
    private ArrayList<Integer> goodList = new ArrayList<Integer>();//

    public static void main(String[] args) {
        MyService2 ms = new MyService2();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("ThreadName=" + Thread.currentThread().getName() +" produce 90 good. is doing");
                for(int i=0;i<90;i++){
                    ms.productGood(i);
                    System.out.println("run produce good:"+i);
                }
                System.err.println("ThreadName=" + Thread.currentThread().getName() +" produce 90 good is finish");
            }
        });
        t1.start();
        Thread t11 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("ThreadName=" + Thread.currentThread().getName() +" produce 20 good. is doing");
                for(int i=90;i<110;i++){
                    ms.productGood(i);
                    System.out.println("run produce good:"+i);
                }
                System.err.println("ThreadName=" + Thread.currentThread().getName() +" produce 20 good is finish");
            }
        });
        t11.start();
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("ThreadName=" + Thread.currentThread().getName() +" customGood 80 good. is doing");
                for(int i=0;i<80;i++){
                    ms.customGood();
                    System.out.println("ThreadName=" + Thread.currentThread().getName() +" run customGood good:"+i);
                }
                System.err.println("ThreadName=" + Thread.currentThread().getName() +" produce 80 good is finish");
            }
        });
        t2.start();
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("ThreadName=" + Thread.currentThread().getName() +" customGood 30 good. is doing");
                for(int i=80;i<110;i++){
                    ms.customGood();
                    System.out.println("ThreadName=" + Thread.currentThread().getName() +" run customGood good:"+i);
                }
                System.err.println("ThreadName=" + Thread.currentThread().getName() +" customGood 30 good is finish");
            }
        });
        t3.start();
    }
    public static void main1(String[] args) {
        int[] arr1 = new int[20];
        System.out.println("arr1.length:"+arr1.length);
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(99);

        int good = list.remove(0);
        System.out.println("good:"+good);
    }
    public void productGood(int good){
        try {
            lock.lock();//lock加锁
            // 1：wait 方法等待：//
            System.out.println("ThreadName=" + Thread.currentThread().getName() +"productGood is doing");
            while(ind>=goodSize){
                conditionCustomGood.signal();
                //conditionCustomGood.signalAll();
                System.out.println("ThreadName=" + Thread.currentThread().getName() +"生产已满,请 customGood 消费");
                conditionProductGood.await();
                System.out.println("ThreadName=" + Thread.currentThread().getName() +"开始wait");
            }
            ind++;
            goodList.add(good);
            System.out.println("ThreadName=" + Thread.currentThread().getName() +"生产good:"+good);
            conditionCustomGood.signal();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    public void customGood(){
        try {
            lock.lock();//lock加锁
            // 1：wait 方法等待：//
            System.out.println("ThreadName=" + Thread.currentThread().getName() +"customGood is doing");
            while(ind<=0){
                System.out.println("ThreadName=" + Thread.currentThread().getName() +"没有东西可以消费,请productGood 生产");
                conditionProductGood.signal();//通知一个
                //conditionProductGood.signalAll();//通知所有
                System.out.println("ThreadName=" + Thread.currentThread().getName() +"开始wait");
                conditionCustomGood.await();
            }
            ind--;
            int good = goodList.remove(ind);
            System.out.println("ThreadName=" + Thread.currentThread().getName() +"消费good:"+good);
            conditionProductGood.signal();//通知所有
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
   public void testMethod() {
       try {
           lock.lock();//lock加锁
           // 1：wait 方法等待：//
           System.out.println("开始wait");
           condition.await();//通过创建Condition对象来使线程wait，必须先执行lock.lock方法获得锁
           // :2：signal方法唤醒
           condition.signal();//condition对象的signal方法可以唤醒wait线程
           for (int i = 0; i < 5; i++) {
               System.out.println("ThreadName=" + Thread.currentThread().getName() + (" " + (i + 1)));
           }
       } catch (InterruptedException e) {
           e.printStackTrace();
       } finally {
           lock.unlock();
       }
   }
}
