package cucumber.runtime.java.guice.impl;


import InjectorSourceFactory.GUICE_INJECTOR_SOURCE_KEY;
import com.google.inject.Injector;
import cucumber.runtime.java.guice.InjectorSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class InjectorSourceFactoryTest {
    @Test
    public void createsDefaultInjectorSourceWhenGuiceModulePropertyIsNotSet() throws Exception {
        InjectorSourceFactory injectorSourceFactory = createInjectorSourceFactory(new Properties());
        Assert.assertThat(injectorSourceFactory.create(), CoreMatchers.is(CoreMatchers.instanceOf(InjectorSource.class)));
    }

    static class CustomInjectorSource implements InjectorSource {
        @Override
        public Injector getInjector() {
            return null;
        }
    }

    @Test
    public void instantiatesInjectorSourceByFullyQualifiedName() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(GUICE_INJECTOR_SOURCE_KEY, InjectorSourceFactoryTest.CustomInjectorSource.class.getName());
        InjectorSourceFactory injectorSourceFactory = createInjectorSourceFactory(properties);
        Assert.assertThat(injectorSourceFactory.create(), CoreMatchers.is(CoreMatchers.instanceOf(InjectorSourceFactoryTest.CustomInjectorSource.class)));
    }

    @Test
    public void failsToInstantiateNonExistantClass() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(GUICE_INJECTOR_SOURCE_KEY, "some.bogus.Class");
        InjectorSourceFactory injectorSourceFactory = createInjectorSourceFactory(properties);
        try {
            injectorSourceFactory.create();
            Assert.fail();
        } catch (InjectorSourceInstantiationFailed exception) {
            Assert.assertThat(exception.getCause(), CoreMatchers.instanceOf(ClassNotFoundException.class));
        }
    }

    @Test
    public void failsToInstantiateClassNotImplementingInjectorSource() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(GUICE_INJECTOR_SOURCE_KEY, String.class.getName());
        InjectorSourceFactory injectorSourceFactory = createInjectorSourceFactory(properties);
        try {
            injectorSourceFactory.create();
            Assert.fail();
        } catch (InjectorSourceInstantiationFailed exception) {
            Assert.assertThat(exception.getCause(), CoreMatchers.instanceOf(ClassCastException.class));
        }
    }

    static class PrivateConstructor implements InjectorSource {
        private PrivateConstructor() {
        }

        @Override
        public Injector getInjector() {
            return null;
        }
    }

    @Test
    public void failsToInstantiateClassWithPrivateConstructor() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(GUICE_INJECTOR_SOURCE_KEY, InjectorSourceFactoryTest.PrivateConstructor.class.getName());
        InjectorSourceFactory injectorSourceFactory = createInjectorSourceFactory(properties);
        try {
            injectorSourceFactory.create();
            Assert.fail();
        } catch (InjectorSourceInstantiationFailed exception) {
            Assert.assertThat(exception.getCause(), CoreMatchers.instanceOf(IllegalAccessException.class));
        }
    }

    static class NoDefaultConstructor implements InjectorSource {
        private NoDefaultConstructor(String someParameter) {
        }

        @Override
        public Injector getInjector() {
            return null;
        }
    }

    @Test
    public void failsToInstantiateClassWithNoDefaultConstructor() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(GUICE_INJECTOR_SOURCE_KEY, InjectorSourceFactoryTest.NoDefaultConstructor.class.getName());
        InjectorSourceFactory injectorSourceFactory = createInjectorSourceFactory(properties);
        try {
            injectorSourceFactory.create();
            Assert.fail();
        } catch (InjectorSourceInstantiationFailed exception) {
            Assert.assertThat(exception.getCause(), CoreMatchers.instanceOf(InstantiationException.class));
        }
    }

    /**
     * <p>Simulates enterprise applications which often use a hierarchy of classloaders.
     *
     * <p>MyChildClassLoader is the only classloader with knowledge of c.r.j.guice.impl.LivesInChildClassLoader
     *
     * <p>The bytecode of LivesInChildClassLoader is intentionally renamed to 'LivesInChildClassLoader.class.txt' to prevent
     * this test's ClassLoader from resolving it.
     *
     * <p>If InjectorSourceFactory calls Class#forName without an explicit ClassLoader argument, which is the behavior of
     * 1.2.4 and earlier, Class#forName will default to the test's ClassLoader which has no knowledge
     * of class LivesInChildClassLoader and the test will fail.
     *
     * <p>See <a href="https://github.com/cucumber/cucumber-jvm/issues/1036">https://github.com/cucumber/cucumber-jvm/issues/1036</a>
     *
     * @throws Exception
     * 		
     */
    @Test
    public void instantiateClassInChildClassLoader() throws Exception {
        ClassLoader childClassLoader = new InjectorSourceFactoryTest.MyChildClassLoader(this.getClass().getClassLoader());
        Thread.currentThread().setContextClassLoader(childClassLoader);
        Properties properties = new Properties();
        properties.setProperty(GUICE_INJECTOR_SOURCE_KEY, "cucumber.runtime.java.guice.impl.LivesInChildClassLoader");
        InjectorSourceFactory injectorSourceFactory = createInjectorSourceFactory(properties);
        Assert.assertThat(injectorSourceFactory.create(), CoreMatchers.is(CoreMatchers.instanceOf(InjectorSource.class)));
    }

    private static class MyChildClassLoader extends ClassLoader {
        public MyChildClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.equals("cucumber.runtime.java.guice.impl.LivesInChildClassLoader")) {
                String filename = getClass().getClassLoader().getResource("cucumber/runtime/java/guice/impl/LivesInChildClassLoader.class.bin").getFile();
                File file = new File(filename);
                try {
                    FileInputStream in = new FileInputStream(file);
                    byte[] bytes = new byte[1024];
                    ByteArrayOutputStream content = new ByteArrayOutputStream();
                    while (true) {
                        int iLen = in.read(bytes);
                        content.write(bytes, 0, iLen);
                        if (iLen < 1024) {
                            break;
                        }
                    } 
                    byte[] bytecode = content.toByteArray();
                    return defineClass(name, bytecode, 0, bytecode.length);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return super.loadClass(name, resolve);
        }
    }
}

