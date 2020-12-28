package cn.huanzi.qch.springbootasync.demo;

import java.util.concurrent.CountDownLatch;

/**
 * 13/04/2018Page 84of 2834.1.15.CyclicBarrier、CountDownLatch、Semaphore的用法
 * 4.1.15.1.CountDownLatch（线程计数器）CountDownLatch类位于java.util.concurrent包下，
 * 利用它可以实现类似计数器的功能。
 * 比如有一个任务A，它要等待其他4个任务执行完毕之后才能执行，
 * 此时就可以利用CountDownLatch来实现这种功能了
 */
public class CountDownLatchTest {
    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(2);
        new Thread(){
            public void run() {
                System.out.println("子线程"+Thread.currentThread().getName()+"正在执行");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("子线程"+Thread.currentThread().getName()+"执行完毕");
                latch.countDown();
            };
        }.start();
        new Thread(){
            public void run() {
                System.out.println("子线程"+Thread.currentThread().getName()+"正在执行");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("子线程"+Thread.currentThread().getName()+"执行完毕");
                latch.countDown();
            };
        }.start();
        System.out.println("等待2个子线程执行完毕...");
        latch.await();
        System.out.println("2个子线程已经执行完毕");
        System.out.println("继续执行主线程");

    }
}
