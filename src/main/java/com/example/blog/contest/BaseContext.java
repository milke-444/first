package com.example.blog.contest;

public class BaseContext {

        public static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();//线程隔离

        public static void setCurrentId(Integer id) {
            threadLocal.set(id);
        }

        public static Integer getCurrentId() {
            return threadLocal.get();
        }

        public static void removeCurrentId() {
            threadLocal.remove();
        }
}
