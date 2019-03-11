/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.basicapi;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.verification.SmartNullPointerException;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


@SuppressWarnings("unchecked")
public class MocksCreationTest extends TestBase {
    private class HasPrivateConstructor {}

    @Test
    public void should_create_mock_when_constructor_is_private() {
        Assert.assertNotNull(Mockito.mock(MocksCreationTest.HasPrivateConstructor.class));
    }

    @Test
    public void should_combine_mock_name_and_smart_nulls() {
        // given
        IMethods mock = Mockito.mock(IMethods.class, Mockito.withSettings().defaultAnswer(Mockito.RETURNS_SMART_NULLS).name("great mockie"));
        // when
        IMethods smartNull = mock.iMethodsReturningMethod();
        String name = mock.toString();
        // then
        assertThat(name).contains("great mockie");
        // and
        try {
            smartNull.simpleMethod();
            Assert.fail();
        } catch (SmartNullPointerException e) {
        }
    }

    @Test
    public void should_combine_mock_name_and_extra_interfaces() {
        // given
        IMethods mock = Mockito.mock(IMethods.class, Mockito.withSettings().extraInterfaces(List.class).name("great mockie"));
        // when
        String name = mock.toString();
        // then
        assertThat(name).contains("great mockie");
        // and
        Assert.assertTrue((mock instanceof List));
    }

    @Test
    public void should_specify_mock_name_via_settings() {
        // given
        IMethods mock = Mockito.mock(IMethods.class, Mockito.withSettings().name("great mockie"));
        // when
        String name = mock.toString();
        // then
        assertThat(name).contains("great mockie");
    }

    @Test
    public void should_scream_when_spy_created_with_wrong_type() {
        // given
        List list = new LinkedList();
        try {
            // when
            Mockito.mock(List.class, Mockito.withSettings().spiedInstance(list));
            Assert.fail();
            // then
        } catch (MockitoException e) {
        }
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void should_allow_creating_spies_with_correct_type() {
        List list = new LinkedList();
        Mockito.mock(LinkedList.class, Mockito.withSettings().spiedInstance(list));
    }

    @Test
    public void should_allow_inline_mock_creation() {
        Mockito.when(Mockito.mock(Set.class).isEmpty()).thenReturn(false);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface SomeAnnotation {}

    @MocksCreationTest.SomeAnnotation
    static class Foo {}

    @Test
    public void should_strip_annotations() {
        MocksCreationTest.Foo withAnnotations = Mockito.mock(MocksCreationTest.Foo.class);
        MocksCreationTest.Foo withoutAnnotations = Mockito.mock(MocksCreationTest.Foo.class, withoutAnnotations());
        // expect:
        Assert.assertTrue(withAnnotations.getClass().isAnnotationPresent(MocksCreationTest.SomeAnnotation.class));
        Assert.assertFalse(withoutAnnotations.getClass().isAnnotationPresent(MocksCreationTest.SomeAnnotation.class));
    }
}

