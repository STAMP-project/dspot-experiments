package brave.propagation;


import brave.test.util.ClassLoaders;
import org.junit.Test;


public class TraceContextClassLoaderTest {
    @Test
    public void unloadable_afterBasicUsage() {
        ClassLoaders.assertRunIsUnloadable(TraceContextClassLoaderTest.BasicUsage.class, getClass().getClassLoader());
    }

    static class BasicUsage implements Runnable {
        @Override
        public void run() {
            TraceContext.newBuilder().traceId(1).spanId(2).build();
        }
    }
}

