package cn.huanzi.qch.springbootasync.demo.sync;

/**
 * 两个线程之间共享数据 01
 */
public class SynchronizedTest01 {
    private int j = 0;

    public synchronized void add(){
        j++;
        System.out.println("Thread "+Thread.currentThread().getName()+",add j:"+j);
    }

    public synchronized void dec(){
        j--;
        System.out.println("Thread "+Thread.currentThread().getName()+",dec j:"+j);
    }
    public int getData(){
        return j;
    }

    public static void main(String[] args) {
        SynchronizedTest01 st = new SynchronizedTest01();
        Thread t1 = new Thread(
                new Runnable() {
                    private SynchronizedTest01 synchronizedTest01 ;
                    @Override
                    public void run() {
                        synchronizedTest01 = st;
                        synchronizedTest01.add();
                    }
                }
        );
        t1.start();
        Thread t2 = new Thread(
                new Runnable() {
                    private SynchronizedTest01 synchronizedTest01 ;
                    @Override
                    public void run() {
                        synchronizedTest01 = st;
                        synchronizedTest01.dec();
                    }
                }
        );
        t2.start();
    }
}
