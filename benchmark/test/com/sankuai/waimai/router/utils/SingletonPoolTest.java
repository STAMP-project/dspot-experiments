package com.sankuai.waimai.router.utils;


import android.support.annotation.NonNull;
import com.sankuai.waimai.router.annotation.RouterProvider;
import com.sankuai.waimai.router.service.IFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class SingletonPoolTest {
    public abstract static class Base {
        private final String mName;

        public Base(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }
    }

    public static class TestSingleton extends SingletonPoolTest.Base {
        public TestSingleton() {
            super("");
        }
    }

    @Test
    public void get() throws Exception {
        SingletonPoolTest.TestSingleton o1 = SingletonPool.get(SingletonPoolTest.TestSingleton.class, null);
        Assert.assertNotNull(o1);
        SingletonPoolTest.TestSingleton o2 = SingletonPool.get(SingletonPoolTest.TestSingleton.class, null);
        Assert.assertSame(o1, o2);
    }

    public static class TestProvider extends SingletonPoolTest.Base {
        @RouterProvider
        public static SingletonPoolTest.TestProvider provideInstance() {
            return new SingletonPoolTest.TestProvider("provider");
        }

        public TestProvider() {
            super("default");
        }

        public TestProvider(String name) {
            super(name);
        }
    }

    @Test
    public void provider() throws Exception {
        SingletonPoolTest.TestProvider p = SingletonPool.get(SingletonPoolTest.TestProvider.class, null);
        Assert.assertNotNull(p);
        Assert.assertEquals("provider", p.getName());
    }

    public static class TestFactory extends SingletonPoolTest.Base {
        public TestFactory() {
            super("default");
        }

        public TestFactory(String name) {
            super(name);
        }
    }

    @Test
    public void factory() throws Exception {
        SingletonPoolTest.TestFactory o = SingletonPool.get(SingletonPoolTest.TestFactory.class, new IFactory() {
            @NonNull
            @Override
            public <T> T create(@NonNull
            Class<T> clazz) throws Exception {
                return clazz.getConstructor(String.class).newInstance("factory");
            }
        });
        Assert.assertNotNull(o);
        Assert.assertEquals("factory", o.getName());
    }
}

