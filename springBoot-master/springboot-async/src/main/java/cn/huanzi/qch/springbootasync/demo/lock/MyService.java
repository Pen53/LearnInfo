package cn.huanzi.qch.springbootasync.demo.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 1.Condition类的awiat方法和Object类的wait方法等效
 * 2.Condition类的signal方法和Object类的notify方法等效
 * 3.Condition类的signalAll方法和Object类的notifyAll方法等效
 * 4.ReentrantLock类可以唤醒指定条件的线程，而object的唤醒是随机
 */
public class MyService {
    Lock lock = new ReentrantLock();//默认非公平锁
    //Lock lock=new ReentrantLock(true);//公平锁
    // Lock lock=new ReentrantLock(false);//非公平锁
   private Condition condition=lock.newCondition();// 创建Condition
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
