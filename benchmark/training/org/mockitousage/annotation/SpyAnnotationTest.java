/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.annotation;


import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.exceptions.base.MockitoException;
import org.mockitoutil.TestBase;


@SuppressWarnings("unused")
public class SpyAnnotationTest extends TestBase {
    @Spy
    final List<String> spiedList = new ArrayList<String>();

    @Spy
    SpyAnnotationTest.InnerStaticClassWithNoArgConstructor staticTypeWithNoArgConstructor;

    @Spy
    SpyAnnotationTest.InnerStaticClassWithoutDefinedConstructor staticTypeWithoutDefinedConstructor;

    @Spy
    SpyAnnotationTest.MockTranslator translator;

    @Rule
    public final ExpectedException shouldThrow = ExpectedException.none();

    @Test
    public void should_init_spy_by_instance() throws Exception {
        Mockito.doReturn("foo").when(spiedList).get(10);
        Assert.assertEquals("foo", spiedList.get(10));
        Assert.assertTrue(spiedList.isEmpty());
    }

    @Test
    public void should_init_spy_and_automatically_create_instance() throws Exception {
        Mockito.when(staticTypeWithNoArgConstructor.toString()).thenReturn("x");
        Mockito.when(staticTypeWithoutDefinedConstructor.toString()).thenReturn("y");
        Assert.assertEquals("x", staticTypeWithNoArgConstructor.toString());
        Assert.assertEquals("y", staticTypeWithoutDefinedConstructor.toString());
    }

    @Test
    public void should_allow_spying_on_interfaces() throws Exception {
        class WithSpy {
            @Spy
            List<String> list;
        }
        WithSpy withSpy = new WithSpy();
        MockitoAnnotations.initMocks(withSpy);
        Mockito.when(withSpy.list.size()).thenReturn(3);
        Assert.assertEquals(3, withSpy.list.size());
    }

    @Test
    public void should_allow_spying_on_interfaces_when_instance_is_concrete() throws Exception {
        class WithSpy {
            @Spy
            List<String> list = new LinkedList<String>();
        }
        WithSpy withSpy = new WithSpy();
        // when
        MockitoAnnotations.initMocks(withSpy);
        // then
        Mockito.verify(withSpy.list, Mockito.never()).clear();
    }

    @Test
    public void should_report_when_no_arg_less_constructor() throws Exception {
        class FailingSpy {
            @Spy
            SpyAnnotationTest.NoValidConstructor noValidConstructor;
        }
        try {
            MockitoAnnotations.initMocks(new FailingSpy());
            Assert.fail();
        } catch (MockitoException e) {
            assertThat(e.getMessage()).contains("Please ensure that the type").contains(SpyAnnotationTest.NoValidConstructor.class.getSimpleName()).contains("has a no-arg constructor");
        }
    }

    @Test
    public void should_report_when_constructor_is_explosive() throws Exception {
        class FailingSpy {
            @Spy
            SpyAnnotationTest.ThrowingConstructor throwingConstructor;
        }
        try {
            MockitoAnnotations.initMocks(new FailingSpy());
            Assert.fail();
        } catch (MockitoException e) {
            assertThat(e.getMessage()).contains("Unable to create mock instance");
        }
    }

    @Test
    public void should_spy_abstract_class() throws Exception {
        class SpyAbstractClass {
            @Spy
            AbstractList<String> list;

            List<String> asSingletonList(String s) {
                Mockito.when(list.size()).thenReturn(1);
                Mockito.when(list.get(0)).thenReturn(s);
                return list;
            }
        }
        SpyAbstractClass withSpy = new SpyAbstractClass();
        MockitoAnnotations.initMocks(withSpy);
        Assert.assertEquals(Arrays.asList("a"), withSpy.asSingletonList("a"));
    }

    @Test
    public void should_spy_inner_class() throws Exception {
        class WithMockAndSpy {
            @Spy
            private WithMockAndSpy.InnerStrength strength;

            @Mock
            private List<String> list;

            abstract class InnerStrength {
                private final String name;

                InnerStrength() {
                    // Make sure that @Mock fields are always injected before @Spy fields.
                    Assert.assertNotNull(list);
                    // Make sure constructor is indeed called.
                    this.name = "inner";
                }

                abstract String strength();

                String fullStrength() {
                    return ((name) + " ") + (strength());
                }
            }
        }
        WithMockAndSpy outer = new WithMockAndSpy();
        MockitoAnnotations.initMocks(outer);
        Mockito.when(outer.strength.strength()).thenReturn("strength");
        Assert.assertEquals("inner strength", outer.strength.fullStrength());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void should_reset_spy() throws Exception {
        spiedList.get(10);// see shouldInitSpy

    }

    @Test
    public void should_report_when_enclosing_instance_is_needed() throws Exception {
        class Outer {
            class Inner {}
        }
        class WithSpy {
            @Spy
            private Outer.Inner inner;
        }
        try {
            MockitoAnnotations.initMocks(new WithSpy());
            Assert.fail();
        } catch (MockitoException e) {
            assertThat(e).hasMessageContaining("@Spy annotation can only initialize inner classes");
        }
    }

    @Test
    public void should_report_private_inner_not_supported() throws Exception {
        try {
            MockitoAnnotations.initMocks(new SpyAnnotationTest.WithInnerPrivate());
            Assert.fail();
        } catch (MockitoException e) {
            // Currently fails at instantiation time, because the mock subclass don't have the
            // 1-arg constructor expected for the outerclass.
            // org.mockito.internal.creation.instance.ConstructorInstantiator.withParams()
            assertThat(e).hasMessageContaining("Unable to initialize @Spy annotated field 'spy_field'").hasMessageContaining(SpyAnnotationTest.WithInnerPrivate.InnerPrivate.class.getSimpleName());
        }
    }

    @Test
    public void should_report_private_abstract_inner_not_supported() throws Exception {
        try {
            MockitoAnnotations.initMocks(new SpyAnnotationTest.WithInnerPrivateAbstract());
            Assert.fail();
        } catch (MockitoException e) {
            assertThat(e).hasMessageContaining("@Spy annotation can't initialize private abstract inner classes").hasMessageContaining(SpyAnnotationTest.WithInnerPrivateAbstract.class.getSimpleName()).hasMessageContaining(SpyAnnotationTest.WithInnerPrivateAbstract.InnerPrivateAbstract.class.getSimpleName()).hasMessageContaining("You should augment the visibility of this inner class");
        }
    }

    @Test
    public void should_report_private_static_abstract_inner_not_supported() throws Exception {
        try {
            MockitoAnnotations.initMocks(new SpyAnnotationTest.WithInnerPrivateStaticAbstract());
            Assert.fail();
        } catch (MockitoException e) {
            assertThat(e).hasMessageContaining("@Spy annotation can't initialize private abstract inner classes").hasMessageContaining(SpyAnnotationTest.WithInnerPrivateStaticAbstract.class.getSimpleName()).hasMessageContaining(SpyAnnotationTest.WithInnerPrivateStaticAbstract.InnerPrivateStaticAbstract.class.getSimpleName()).hasMessageContaining("You should augment the visibility of this inner class");
        }
    }

    @Test
    public void should_be_able_to_stub_and_verify_via_varargs_for_list_params() throws Exception {
        // You can stub with varargs.
        Mockito.when(translator.translate("hello", "mockito")).thenReturn(Arrays.asList("you", "too"));
        // Pretend the prod code will call translate(List<String>) with these elements.
        assertThat(translator.translate(Arrays.asList("hello", "mockito"))).containsExactly("you", "too");
        assertThat(translator.translate(Arrays.asList("not stubbed"))).isEmpty();
        // You can verify with varargs.
        Mockito.verify(translator).translate("hello", "mockito");
    }

    @Test
    public void should_be_able_to_stub_and_verify_via_varargs_of_matchers_for_list_params() throws Exception {
        // You can stub with varargs of matchers.
        Mockito.when(translator.translate(Mockito.anyString())).thenReturn(Arrays.asList("huh?"));
        Mockito.when(translator.translate(ArgumentMatchers.eq("hello"))).thenReturn(Arrays.asList("hi"));
        // Pretend the prod code will call translate(List<String>) with these elements.
        assertThat(translator.translate(Arrays.asList("hello"))).containsExactly("hi");
        assertThat(translator.translate(Arrays.asList("not explicitly stubbed"))).containsExactly("huh?");
        // You can verify with varargs of matchers.
        Mockito.verify(translator).translate(ArgumentMatchers.eq("hello"));
    }

    static class WithInnerPrivateStaticAbstract {
        @Spy
        private SpyAnnotationTest.WithInnerPrivateStaticAbstract.InnerPrivateStaticAbstract spy_field;

        private abstract static class InnerPrivateStaticAbstract {}
    }

    static class WithInnerPrivateAbstract {
        @Spy
        private SpyAnnotationTest.WithInnerPrivateAbstract.InnerPrivateAbstract spy_field;

        public void some_method() {
            new SpyAnnotationTest.WithInnerPrivateAbstract.InnerPrivateConcrete();
        }

        private abstract class InnerPrivateAbstract {}

        private class InnerPrivateConcrete extends SpyAnnotationTest.WithInnerPrivateAbstract.InnerPrivateAbstract {}
    }

    static class WithInnerPrivate {
        @Spy
        private SpyAnnotationTest.WithInnerPrivate.InnerPrivate spy_field;

        private class InnerPrivate {}

        private class InnerPrivateSub extends SpyAnnotationTest.WithInnerPrivate.InnerPrivate {}
    }

    static class InnerStaticClassWithoutDefinedConstructor {}

    static class InnerStaticClassWithNoArgConstructor {
        InnerStaticClassWithNoArgConstructor() {
        }

        InnerStaticClassWithNoArgConstructor(String f) {
        }
    }

    static class NoValidConstructor {
        NoValidConstructor(String f) {
        }
    }

    static class ThrowingConstructor {
        ThrowingConstructor() {
            throw new RuntimeException("boo!");
        }
    }

    interface Translator {
        List<String> translate(List<String> messages);
    }

    abstract static class MockTranslator implements SpyAnnotationTest.Translator {
        @Override
        public final List<String> translate(List<String> messages) {
            return translate(messages.toArray(new String[0]));
        }

        abstract List<String> translate(String... messages);
    }
}

