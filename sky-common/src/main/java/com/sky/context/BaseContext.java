package com.sky.context;

/**
 * 利用ThreadLocal存储拦截器处解析出的用户id，供Controller、Service等其他位置读取使用
 */
public class BaseContext {

    //ThreadLocal对象threadLocal，作为key。注意它是static的，只随本类加载时new一次，各个线程共享它一个。
    /* 这将意味着所有线程利用这里定义的ThreadLocal型对象threadLocal存用户id时，
        底层往线程的ThreadLocalMap集合里存数据时，使用的键this都是同一个，即这里“单例”的threadLocal对象。
        这样的话即使忘了remove，下次使用同样的key可覆盖上次的value，避免存入新的k-v对。 */
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 从登录校验拦截器JWT令牌中解析出登录用户id，存进线程里
     *
     * @param id 用户id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 从线程里获取先前在拦截器里保存到线程里的id
     *
     * @return 用户id
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }

    /**
     * 从线程里移除保存的id数据以节约资源(本例仅是让线程用后回池子前，清空里面保存的id)
     */
    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
