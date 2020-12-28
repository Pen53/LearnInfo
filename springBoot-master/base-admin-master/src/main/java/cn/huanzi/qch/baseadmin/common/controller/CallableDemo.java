package cn.huanzi.qch.baseadmin.common.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class CallableDemo<T> implements Callable<T> {
    int ind;
    public CallableDemo(int ind){
        this.ind = ind;
    }
    @Override
    public T call() throws Exception {
        System.out.println("CallableDemo task is doing..");
        Map<String,Object> result = new HashMap<>();
        result.put("curTime",new Date());
        result.put("ind",ind);
        if(ind==6){
            Thread.sleep(10_000);
        }
        return (T) result;
    }
}
