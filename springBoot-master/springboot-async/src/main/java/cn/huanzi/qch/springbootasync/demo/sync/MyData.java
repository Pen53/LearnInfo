package cn.huanzi.qch.springbootasync.demo.sync;

/**
 * 两个线程之间共享数据 01
 */
public class MyData {
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
}
