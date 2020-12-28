package cn.huanzi.qch.springbootasync.demo;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * CountDownLatch和CyclicBarrier都能够实现线程之间的等待，只不过它们侧重点不同；
 * CountDownLatch一般用于某个线程A等待若干个其他线程执行完任务之后，它才
 * 执行；而CyclicBarrier一般用于一组线程互相等待至某个状态，然后这一组线程再同时执行；
 * 另外，CountDownLatch是不能够重用的，而CyclicBarrier是可以重用的
 *
 * 4.1.15.2.CyclicBarrier（回环栅栏-等待至barrier状态再全部同时执行）
 * 字面意思回环栅栏，通过它可以实现让一组线程等待至某个状态之后再全部同时执行。
 * 叫做回环是因为当所有等待线程都被释放以后，CyclicBarrier可以被重用。
 * 我们暂且把这个状态就叫做barrier，当调用await()方法之后，线程就处于barrier了。
 * CyclicBarrier中最重要的方法就是await方法，
 * 它有2个重载版本：1.public  int  await()：
 * 用来挂起当前线程，直至所有线程都到达barrier状态再同时执行后续任务；
 * 2.public  int  await(long  timeout, TimeUnit  unit)：让这些线程等待至一定的时间，
 * 如果还有线程没有到达barrier状态就直接让到达barrier的线程执行后续任务。
 */
public class CyclicBarrierTest {
    public static void main(String[] args) {
        int n = 4;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(n);
        for(int i=0;i<n;i++){
            new Writer(cyclicBarrier).start();
        }
        System.out.println("main is doing...");
    }
    static class Writer extends Thread{
        private CyclicBarrier cyclicBarrier;
        public  Writer(CyclicBarrier cyclicBarrier){
            this.cyclicBarrier =cyclicBarrier;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(50_000);
                System.out.println("线程"+Thread.currentThread().getName()+" 写入数据完毕，等待其他线程写入完毕.");
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println("线程"+Thread.currentThread().getName()+" 所有线程写入完毕.");
        }
    }
}
