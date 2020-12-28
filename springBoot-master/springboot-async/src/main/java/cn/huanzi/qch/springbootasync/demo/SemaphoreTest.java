package cn.huanzi.qch.springbootasync.demo;

import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Semaphore其实和锁有点类似，它一般用于控制对某组资源的访问权限
 1.public  boolean  tryAcquire():尝试获取一个许可，若获取成功，则立即返回true，
 若获取失败，则立即返回false
 2.public boolean tryAcquire(long timeout, TimeUnit unit):
 尝试获取一个许可，若在指定的时间内获取成功，则立即返回true，否则则立即返回false
 3.public boolean tryAcquire(int permits):
 尝试获取permits个许可，若获取成功，则立即返回true，
 若获取失败，则立即返回false
 4.public boolean tryAcquire(int permits, long timeout, TimeUnit unit):
 尝试获取permits个许可，若在指定的时间内获取成功，则立即返回true，
 否则则立即返回false
 5.还可以通过availablePermits()方法得到可用的许可数目。
 */

/**
 * 若一个工厂有5台机器，但是有8个工人，一台机器同时只能被一个工人使用，
 * 只有使用完了，其他工人才能继续使用
 */
public class SemaphoreTest {
    public static void main(String[] args) {
        int n = 8;
        Semaphore semaphore = new Semaphore(5);//机器
        for(int i=0;i<n;i++){
            new Work(i,semaphore).start();
        }
    }

    static class Work extends Thread{
        private int num;
        private Semaphore semaphore;
        public Work(int num,Semaphore semaphore){
            this.num = num;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();//获取
                int r = new Random().nextInt(50000);
                System.out.println("num:"+num+"thread "+Thread.currentThread().getName()+" is doing "+r+"ms");
                Thread.sleep(r);
                System.out.println("num:"+num+"thread "+Thread.currentThread().getName()+" is doing. finish");
                semaphore.release();//释放
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
