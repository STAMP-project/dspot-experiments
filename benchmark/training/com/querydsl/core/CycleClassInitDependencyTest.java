package com.querydsl.core;


import com.querydsl.core.testutil.ThreadSafety;
import org.junit.Test;


public class CycleClassInitDependencyTest {
    private static ClassLoader loader;

    @Test(timeout = 2000)
    public void test() {
        // each thread wants to load one part of the dependency circle
        ThreadSafety.check(new CycleClassInitDependencyTest.LoadClassRunnable("com.querydsl.core.types.dsl.NumberExpression"), new CycleClassInitDependencyTest.LoadClassRunnable("com.querydsl.core.types.dsl.Expressions"));
    }

    private static class LoadClassRunnable implements Runnable {
        private final String classToLoad;

        public LoadClassRunnable(String classToLoad) {
            this.classToLoad = classToLoad;
        }

        @Override
        public void run() {
            try {
                Class.forName(classToLoad, true, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

