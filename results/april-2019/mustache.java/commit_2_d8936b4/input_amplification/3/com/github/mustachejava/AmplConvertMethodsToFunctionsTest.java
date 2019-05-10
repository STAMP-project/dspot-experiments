package com.github.mustachejava;


import com.github.mustachejava.codes.DefaultMustache;
import com.github.mustachejava.reflect.Guard;
import com.github.mustachejava.reflect.ReflectionObjectHandler;
import com.github.mustachejava.reflect.ReflectionWrapper;
import com.github.mustachejava.util.GuardException;
import com.github.mustachejava.util.Wrapper;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;
import junit.framework.TestCase;
import org.junit.BeforeClass;


public class AmplConvertMethodsToFunctionsTest {
    private static ReflectionObjectHandler roh;

    @Retention(RetentionPolicy.RUNTIME)
    @interface TemplateMethod {}

    @BeforeClass
    public static void setup() {
        AmplConvertMethodsToFunctionsTest.roh = new ReflectionObjectHandler() {
            @Override
            protected Wrapper findWrapper(int scopeIndex, Wrapper[] wrappers, List<Guard> guards, Object scope, String name) {
                Wrapper wrapper = super.findWrapper(scopeIndex, wrappers, guards, scope, name);
                if (wrapper == null) {
                    return getWrapper(scopeIndex, wrappers, guards, scope, name, scope.getClass());
                }
                return wrapper;
            }

            private Wrapper getWrapper(final int scopeIndex, final Wrapper[] wrappers, final List<Guard> guards, Object scope, String name, Class<?> aClass) {
                try {
                    Method method = aClass.getDeclaredMethod(name, String.class);
                    method.setAccessible(true);
                    return new ReflectionWrapper(scopeIndex, wrappers, guards.toArray(new Guard[guards.size()]), method, null, this) {
                        @Override
                        public Object call(List<Object> scopes) throws GuardException {
                            guardCall(scopes);
                            final Object scope1 = unwrap(scopes);
                            if (scope1 == null) {
                                return null;
                            }
                            if ((method.getAnnotation(AmplConvertMethodsToFunctionsTest.TemplateMethod.class)) == null) {
                                return new Function<String, String>() {
                                    @Override
                                    public String apply(String input) {
                                        return getString(input, scope1);
                                    }
                                };
                            } else {
                                return new TemplateFunction() {
                                    @Override
                                    public String apply(String input) {
                                        return getString(input, scope1);
                                    }
                                };
                            }
                        }

                        private String getString(String input, Object scope1) {
                            try {
                                Object invoke = method.invoke(scope1, input);
                                return invoke == null ? null : String.valueOf(invoke);
                            } catch (InvocationTargetException e) {
                                throw new MustacheException(("Failed to execute method: " + (method)), e.getTargetException());
                            } catch (IllegalAccessException e) {
                                throw new MustacheException(("Failed to execute method: " + (method)), e);
                            }
                        }
                    };
                } catch (NoSuchMethodException e) {
                    Class<?> superclass = aClass.getSuperclass();
                    return superclass == (Object.class) ? null : getWrapper(scopeIndex, wrappers, guards, scope, name, superclass);
                }
            }
        };
    }

    public void testReadmeSerial_add12536_remove13484_add16638() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add12536__9 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        sw.toString();
        String o_testReadmeSerial_add12536_remove13484_add16638__16 = TestUtil.getContents(this.root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12536_remove13484_add16638__16);
        String o_testReadmeSerial_add12536__14 = TestUtil.getContents(this.root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12536__14);
        String String_30 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_30);
        boolean boolean_31 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12536_remove13484_add16638__16);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12536__14);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_30);
    }
}

