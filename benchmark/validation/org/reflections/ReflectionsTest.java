package org.reflections;


import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Test;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;


/**
 *
 */
@SuppressWarnings("unchecked")
public class ReflectionsTest {
    public static final FilterBuilder TestModelFilter = new FilterBuilder().include("org.reflections.TestModel\\$.*");

    static Reflections reflections;

    @Test
    public void testSubTypesOf() {
        Assert.assertThat(ReflectionsTest.reflections.getSubTypesOf(TestModel.I1.class), ReflectionsTest.are(TestModel.I2.class, TestModel.C1.class, TestModel.C2.class, TestModel.C3.class, TestModel.C5.class));
        Assert.assertThat(ReflectionsTest.reflections.getSubTypesOf(TestModel.C1.class), ReflectionsTest.are(TestModel.C2.class, TestModel.C3.class, TestModel.C5.class));
        Assert.assertFalse("getAllTypes should not be empty when Reflections is configured with SubTypesScanner(false)", ReflectionsTest.reflections.getAllTypes().isEmpty());
    }

    @Test
    public void testTypesAnnotatedWith() {
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(TestModel.MAI1.class, true), ReflectionsTest.are(TestModel.AI1.class));
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(TestModel.MAI1.class, true), annotatedWith(TestModel.MAI1.class));
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(TestModel.AI2.class, true), ReflectionsTest.are(TestModel.I2.class));
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(TestModel.AI2.class, true), annotatedWith(TestModel.AI2.class));
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(TestModel.AC1.class, true), ReflectionsTest.are(TestModel.C1.class, TestModel.C2.class, TestModel.C3.class, TestModel.C5.class));
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(TestModel.AC1.class, true), annotatedWith(TestModel.AC1.class));
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(TestModel.AC1n.class, true), ReflectionsTest.are(TestModel.C1.class));
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(TestModel.AC1n.class, true), annotatedWith(TestModel.AC1n.class));
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(TestModel.MAI1.class), ReflectionsTest.are(TestModel.AI1.class, TestModel.I1.class, TestModel.I2.class, TestModel.C1.class, TestModel.C2.class, TestModel.C3.class, TestModel.C5.class));
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(TestModel.MAI1.class), metaAnnotatedWith(TestModel.MAI1.class));
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(TestModel.AI1.class), ReflectionsTest.are(TestModel.I1.class, TestModel.I2.class, TestModel.C1.class, TestModel.C2.class, TestModel.C3.class, TestModel.C5.class));
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(TestModel.AI1.class), metaAnnotatedWith(TestModel.AI1.class));
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(TestModel.AI2.class), ReflectionsTest.are(TestModel.I2.class, TestModel.C1.class, TestModel.C2.class, TestModel.C3.class, TestModel.C5.class));
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(TestModel.AI2.class), metaAnnotatedWith(TestModel.AI2.class));
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(TestModel.AM1.class), isEmpty);
        // annotation member value matching
        TestModel.AC2 ac2 = new TestModel.AC2() {
            public String value() {
                return "ugh?!";
            }

            public Class<? extends Annotation> annotationType() {
                return TestModel.AC2.class;
            }
        };
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(ac2), ReflectionsTest.are(TestModel.C3.class, TestModel.C5.class, TestModel.I3.class, TestModel.C6.class, TestModel.AC3.class, TestModel.C7.class));
        Assert.assertThat(ReflectionsTest.reflections.getTypesAnnotatedWith(ac2, true), ReflectionsTest.are(TestModel.C3.class, TestModel.I3.class, TestModel.AC3.class));
    }

    @Test
    public void testMethodsAnnotatedWith() {
        try {
            Assert.assertThat(ReflectionsTest.reflections.getMethodsAnnotatedWith(TestModel.AM1.class), ReflectionsTest.are(TestModel.C4.class.getDeclaredMethod("m1"), TestModel.C4.class.getDeclaredMethod("m1", int.class, String[].class), TestModel.C4.class.getDeclaredMethod("m1", int[][].class, String[][].class), TestModel.C4.class.getDeclaredMethod("m3")));
            TestModel.AM1 am1 = new TestModel.AM1() {
                public String value() {
                    return "1";
                }

                public Class<? extends Annotation> annotationType() {
                    return TestModel.AM1.class;
                }
            };
            Assert.assertThat(ReflectionsTest.reflections.getMethodsAnnotatedWith(am1), ReflectionsTest.are(TestModel.C4.class.getDeclaredMethod("m1"), TestModel.C4.class.getDeclaredMethod("m1", int.class, String[].class), TestModel.C4.class.getDeclaredMethod("m1", int[][].class, String[][].class)));
        } catch (NoSuchMethodException e) {
            Assert.fail();
        }
    }

    @Test
    public void testConstructorsAnnotatedWith() {
        try {
            Assert.assertThat(ReflectionsTest.reflections.getConstructorsAnnotatedWith(TestModel.AM1.class), ReflectionsTest.are(TestModel.C4.class.getDeclaredConstructor(String.class)));
            TestModel.AM1 am1 = new TestModel.AM1() {
                public String value() {
                    return "1";
                }

                public Class<? extends Annotation> annotationType() {
                    return TestModel.AM1.class;
                }
            };
            Assert.assertThat(ReflectionsTest.reflections.getConstructorsAnnotatedWith(am1), ReflectionsTest.are(TestModel.C4.class.getDeclaredConstructor(String.class)));
        } catch (NoSuchMethodException e) {
            Assert.fail();
        }
    }

    @Test
    public void testFieldsAnnotatedWith() {
        try {
            Assert.assertThat(ReflectionsTest.reflections.getFieldsAnnotatedWith(TestModel.AF1.class), ReflectionsTest.are(TestModel.C4.class.getDeclaredField("f1"), TestModel.C4.class.getDeclaredField("f2")));
            Assert.assertThat(ReflectionsTest.reflections.getFieldsAnnotatedWith(new TestModel.AF1() {
                public String value() {
                    return "2";
                }

                public Class<? extends Annotation> annotationType() {
                    return TestModel.AF1.class;
                }
            }), ReflectionsTest.are(TestModel.C4.class.getDeclaredField("f2")));
        } catch (NoSuchFieldException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMethodParameter() {
        try {
            Assert.assertThat(ReflectionsTest.reflections.getMethodsMatchParams(String.class), ReflectionsTest.are(TestModel.C4.class.getDeclaredMethod("m4", String.class), TestModel.Usage.C1.class.getDeclaredMethod("method", String.class)));
            Assert.assertThat(ReflectionsTest.reflections.getMethodsMatchParams(), ReflectionsTest.are(TestModel.C4.class.getDeclaredMethod("m1"), TestModel.C4.class.getDeclaredMethod("m3"), TestModel.AC2.class.getMethod("value"), TestModel.AF1.class.getMethod("value"), TestModel.AM1.class.getMethod("value"), TestModel.Usage.C1.class.getDeclaredMethod("method"), TestModel.Usage.C2.class.getDeclaredMethod("method")));
            Assert.assertThat(ReflectionsTest.reflections.getMethodsMatchParams(int[][].class, String[][].class), ReflectionsTest.are(TestModel.C4.class.getDeclaredMethod("m1", int[][].class, String[][].class)));
            Assert.assertThat(ReflectionsTest.reflections.getMethodsReturn(int.class), ReflectionsTest.are(TestModel.C4.class.getDeclaredMethod("add", int.class, int.class)));
            Assert.assertThat(ReflectionsTest.reflections.getMethodsReturn(String.class), ReflectionsTest.are(TestModel.C4.class.getDeclaredMethod("m3"), TestModel.C4.class.getDeclaredMethod("m4", String.class), TestModel.AC2.class.getMethod("value"), TestModel.AF1.class.getMethod("value"), TestModel.AM1.class.getMethod("value")));
            Assert.assertThat(ReflectionsTest.reflections.getMethodsReturn(void.class), ReflectionsTest.are(TestModel.C4.class.getDeclaredMethod("m1"), TestModel.C4.class.getDeclaredMethod("m1", int.class, String[].class), TestModel.C4.class.getDeclaredMethod("m1", int[][].class, String[][].class), TestModel.Usage.C1.class.getDeclaredMethod("method"), TestModel.Usage.C1.class.getDeclaredMethod("method", String.class), TestModel.Usage.C2.class.getDeclaredMethod("method")));
            Assert.assertThat(ReflectionsTest.reflections.getMethodsWithAnyParamAnnotated(TestModel.AM1.class), ReflectionsTest.are(TestModel.C4.class.getDeclaredMethod("m4", String.class)));
            Assert.assertThat(ReflectionsTest.reflections.getMethodsWithAnyParamAnnotated(new TestModel.AM1() {
                public String value() {
                    return "2";
                }

                public Class<? extends Annotation> annotationType() {
                    return TestModel.AM1.class;
                }
            }), ReflectionsTest.are(TestModel.C4.class.getDeclaredMethod("m4", String.class)));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testConstructorParameter() throws NoSuchMethodException {
        Assert.assertThat(ReflectionsTest.reflections.getConstructorsMatchParams(String.class), ReflectionsTest.are(TestModel.C4.class.getDeclaredConstructor(String.class)));
        Assert.assertThat(ReflectionsTest.reflections.getConstructorsMatchParams(), ReflectionsTest.are(TestModel.C1.class.getDeclaredConstructor(), TestModel.C2.class.getDeclaredConstructor(), TestModel.C3.class.getDeclaredConstructor(), TestModel.C4.class.getDeclaredConstructor(), TestModel.C5.class.getDeclaredConstructor(), TestModel.C6.class.getDeclaredConstructor(), TestModel.C7.class.getDeclaredConstructor(), TestModel.Usage.C1.class.getDeclaredConstructor(), TestModel.Usage.C2.class.getDeclaredConstructor()));
        Assert.assertThat(ReflectionsTest.reflections.getConstructorsWithAnyParamAnnotated(TestModel.AM1.class), ReflectionsTest.are(TestModel.C4.class.getDeclaredConstructor(String.class)));
        Assert.assertThat(ReflectionsTest.reflections.getConstructorsWithAnyParamAnnotated(new TestModel.AM1() {
            public String value() {
                return "1";
            }

            public Class<? extends Annotation> annotationType() {
                return TestModel.AM1.class;
            }
        }), ReflectionsTest.are(TestModel.C4.class.getDeclaredConstructor(String.class)));
    }

    @Test
    public void testResourcesScanner() {
        Predicate<String> filter = new FilterBuilder().include(".*\\.xml").exclude(".*testModel-reflections\\.xml");
        Reflections reflections = new Reflections(new ConfigurationBuilder().filterInputsBy(filter).setScanners(new ResourcesScanner()).setUrls(Arrays.asList(ClasspathHelper.forClass(TestModel.class))));
        Set<String> resolved = reflections.getResources(Pattern.compile(".*resource1-reflections\\.xml"));
        Assert.assertThat(resolved, ReflectionsTest.are("META-INF/reflections/resource1-reflections.xml"));
        Set<String> resources = reflections.getStore().get(index(ResourcesScanner.class)).keySet();
        Assert.assertThat(resources, ReflectionsTest.are("resource1-reflections.xml", "resource2-reflections.xml"));
    }

    @Test
    public void testMethodParameterNames() throws NoSuchMethodException {
        Assert.assertEquals(ReflectionsTest.reflections.getMethodParamNames(TestModel.C4.class.getDeclaredMethod("m3")), Lists.newArrayList());
        Assert.assertEquals(ReflectionsTest.reflections.getMethodParamNames(TestModel.C4.class.getDeclaredMethod("m4", String.class)), Lists.newArrayList("string"));
        Assert.assertEquals(ReflectionsTest.reflections.getMethodParamNames(TestModel.C4.class.getDeclaredMethod("add", int.class, int.class)), Lists.newArrayList("i1", "i2"));
        Assert.assertEquals(ReflectionsTest.reflections.getConstructorParamNames(TestModel.C4.class.getDeclaredConstructor(String.class)), Lists.newArrayList("f1"));
    }

    @Test
    public void testMemberUsageScanner() throws NoSuchFieldException, NoSuchMethodException {
        // field usage
        Assert.assertThat(ReflectionsTest.reflections.getFieldUsage(TestModel.Usage.C1.class.getDeclaredField("c2")), ReflectionsTest.are(TestModel.Usage.C1.class.getDeclaredConstructor(), TestModel.Usage.C1.class.getDeclaredConstructor(TestModel.Usage.C2.class), TestModel.Usage.C1.class.getDeclaredMethod("method"), TestModel.Usage.C1.class.getDeclaredMethod("method", String.class)));
        // method usage
        Assert.assertThat(ReflectionsTest.reflections.getMethodUsage(TestModel.Usage.C1.class.getDeclaredMethod("method")), ReflectionsTest.are(TestModel.Usage.C2.class.getDeclaredMethod("method")));
        Assert.assertThat(ReflectionsTest.reflections.getMethodUsage(TestModel.Usage.C1.class.getDeclaredMethod("method", String.class)), ReflectionsTest.are(TestModel.Usage.C2.class.getDeclaredMethod("method")));
        // constructor usage
        Assert.assertThat(ReflectionsTest.reflections.getConstructorUsage(TestModel.Usage.C1.class.getDeclaredConstructor()), ReflectionsTest.are(TestModel.Usage.C2.class.getDeclaredConstructor(), TestModel.Usage.C2.class.getDeclaredMethod("method")));
        Assert.assertThat(ReflectionsTest.reflections.getConstructorUsage(TestModel.Usage.C1.class.getDeclaredConstructor(TestModel.Usage.C2.class)), ReflectionsTest.are(TestModel.Usage.C2.class.getDeclaredMethod("method")));
    }

    @Test
    public void testScannerNotConfigured() {
        try {
            getMethodsAnnotatedWith(TestModel.AC1.class);
            Assert.fail();
        } catch (ReflectionsException e) {
            Assert.assertEquals(e.getMessage(), (("Scanner " + (MethodAnnotationsScanner.class.getSimpleName())) + " was not configured"));
        }
    }

    private final BaseMatcher<Set<Class<?>>> isEmpty = new BaseMatcher<Set<Class<?>>>() {
        public boolean matches(Object o) {
            return ((Collection<?>) (o)).isEmpty();
        }

        public void describeTo(Description description) {
            description.appendText("empty collection");
        }
    };

    private abstract static class Match<T> extends BaseMatcher<T> {
        public void describeTo(Description description) {
        }
    }
}

