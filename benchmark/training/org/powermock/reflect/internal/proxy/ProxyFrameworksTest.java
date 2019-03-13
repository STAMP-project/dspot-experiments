package org.powermock.reflect.internal.proxy;


import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import net.sf.cglib.proxy.Factory;
import org.junit.Test;


public class ProxyFrameworksTest {
    private ProxyFrameworks proxyFrameworks;

    @Test
    public void should_throw_illegal_argument_exception_if_class_is_null() throws Exception {
        assertThat(proxyFrameworks.getUnproxiedType(null)).isNull();
    }

    @Test
    public void should_return_null_if_object_is_null() throws Exception {
        assertThat(proxyFrameworks.getUnproxiedType(((Object) (null)))).isNull();
    }

    @Test
    public void should_return_original_class_if_object_not_proxy() throws Exception {
        SomeClass someClass = new SomeClass();
        UnproxiedType unproxiedType = proxyFrameworks.getUnproxiedType(someClass);
        assertThatOriginalTypeInstanceOf(unproxiedType, SomeClass.class);
    }

    @Test
    public void should_return_original_class_if_proxy_created_with_java() {
        SomeInterface someInterface = createJavaProxy(new Class[]{ SomeInterface.class });
        UnproxiedType unproxiedType = proxyFrameworks.getUnproxiedType(someInterface);
        assertThatOriginalTypeInstanceOf(unproxiedType, SomeInterface.class);
    }

    @Test
    public void should_return_original_class_if_proxy_created_with_cglib() {
        SomeClass someClass = ((SomeClass) (createCglibProxy(SomeClass.class)));
        UnproxiedType unproxiedType = proxyFrameworks.getUnproxiedType(someClass);
        assertThatOriginalTypeInstanceOf(unproxiedType, SomeClass.class);
    }

    @Test
    public void should_not_detect_synthetic_classes_as_cglib_proxy() throws Exception {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final Enumeration<URL> resources = classLoader.getResources("org/powermock/reflect/internal/proxy");
        final List<URL> urls = new ArrayList<URL>();
        while (resources.hasMoreElements()) {
            urls.add(resources.nextElement());
        } 
        String className = "Some$$SyntheticClass$$Lambda";
        byte[] bytes = ClassFactory.create(className);
        ProxyFrameworksTest.CustomClassLoader customClassLoader = new ProxyFrameworksTest.CustomClassLoader(urls.toArray(new URL[urls.size()]), classLoader);
        Class<?> defineClass = customClassLoader.defineClass(className, bytes);
        UnproxiedType unproxiedType = proxyFrameworks.getUnproxiedType(defineClass.newInstance());
        assertThatOriginalTypeInstanceOf(unproxiedType, defineClass);
    }

    @Test
    public void should_return_object_as_original_class_if_no_non_no_mocking_interfaces() {
        Factory someClass = ((Factory) (createCglibProxy(Factory.class)));
        UnproxiedType unproxiedType = proxyFrameworks.getUnproxiedType(someClass);
        assertThatOriginalTypeInstanceOf(unproxiedType, Object.class);
    }

    @Test
    public void should_return_interface_as_original_type_if_only_one_non_mocking_interface() {
        Factory someClass = ((Factory) (createCglibProxy(Factory.class, SomeInterface.class)));
        UnproxiedType unproxiedType = proxyFrameworks.getUnproxiedType(someClass);
        assertThatOriginalTypeInstanceOf(unproxiedType, SomeInterface.class);
    }

    @Test
    public void should_return_interface_and_original_type_if_proxy_has_interface_and_superclass() {
        SomeClass someClass = ((SomeClass) (createCglibProxy(SomeClass.class, SomeInterface.class, AnotherInterface.class)));
        UnproxiedType unproxiedType = proxyFrameworks.getUnproxiedType(someClass);
        assertThatOriginalTypeInstanceOfAndInterfaces(unproxiedType, SomeClass.class, new Class[]{ SomeInterface.class, AnotherInterface.class });
    }

    @Test
    public void should_return_interfaces_if_proxy_create_from_several_interfaces() {
        Class[] interfaces = new Class[]{ SomeInterface.class, AnotherInterface.class };
        SomeInterface someInterface = createJavaProxy(interfaces);
        UnproxiedType unproxiedType = proxyFrameworks.getUnproxiedType(someInterface);
        assertThatOriginalIsNullAndInterfaces(unproxiedType, interfaces);
    }

    private static class CustomClassLoader extends URLClassLoader {
        private CustomClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}

