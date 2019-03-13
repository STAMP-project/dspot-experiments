package MyPerf4J;


import PropertyValues.METRICS_PROCESS_TYPE_STDOUT;
import cn.myperf4j.asm.aop.ProfilingTransformer;
import java.io.IOException;
import org.junit.Test;


/**
 * Created by LinShunkang on 2018/10/17
 */
public class PreMainTest {
    @Test
    public void test() {
        test(METRICS_PROCESS_TYPE_STDOUT);
        // test(PropertyValues.METRICS_PROCESS_TYPE_INFLUX_DB);
    }

    public class MyClassLoader extends ClassLoader {
        @Override
        protected Class<?> findClass(String name) {
            ProfilingTransformer transformer = new ProfilingTransformer();
            Class<?> targetClass = ClassToTest.class;
            try {
                byte[] transformBytes = transformer.transform(PreMainTest.class.getClassLoader(), targetClass.getName(), targetClass, null, ClassFileUtils.getClassFileContent(targetClass.getName()));
                return defineClass(name, transformBytes, 0, transformBytes.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

