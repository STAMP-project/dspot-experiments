/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.base.MockitoException;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class CaptorAnnotationTest extends TestBase {
    @Retention(RetentionPolicy.RUNTIME)
    public @interface NotAMock {}

    @Captor
    final ArgumentCaptor<String> finalCaptor = ArgumentCaptor.forClass(String.class);

    @Captor
    ArgumentCaptor<List<List<String>>> genericsCaptor;

    @SuppressWarnings("rawtypes")
    @Captor
    ArgumentCaptor nonGenericCaptorIsAllowed;

    @Mock
    CaptorAnnotationTest.MockInterface mockInterface;

    @CaptorAnnotationTest.NotAMock
    Set<?> notAMock;

    public interface MockInterface {
        void testMe(String simple, List<List<String>> genericList);
    }

    @Test
    public void testNormalUsage() {
        MockitoAnnotations.initMocks(this);
        // check if assigned correctly
        Assert.assertNotNull(finalCaptor);
        Assert.assertNotNull(genericsCaptor);
        Assert.assertNotNull(nonGenericCaptorIsAllowed);
        Assert.assertNull(notAMock);
        // use captors in the field to be sure they are cool
        String argForFinalCaptor = "Hello";
        ArrayList<List<String>> argForGenericsCaptor = new ArrayList<List<String>>();
        mockInterface.testMe(argForFinalCaptor, argForGenericsCaptor);
        Mockito.verify(mockInterface).testMe(finalCaptor.capture(), genericsCaptor.capture());
        Assert.assertEquals(argForFinalCaptor, finalCaptor.getValue());
        Assert.assertEquals(argForGenericsCaptor, genericsCaptor.getValue());
    }

    public static class WrongType {
        @Captor
        List<?> wrongType;
    }

    @Test
    public void shouldScreamWhenWrongTypeForCaptor() {
        try {
            MockitoAnnotations.initMocks(new CaptorAnnotationTest.WrongType());
            Assert.fail();
        } catch (MockitoException e) {
        }
    }

    public static class ToManyAnnotations {
        @Captor
        @Mock
        ArgumentCaptor<List> missingGenericsField;
    }

    @Test
    public void shouldScreamWhenMoreThanOneMockitoAnnotation() {
        try {
            MockitoAnnotations.initMocks(new CaptorAnnotationTest.ToManyAnnotations());
            Assert.fail();
        } catch (MockitoException e) {
            Assert.assertThat(e).hasMessageContaining("missingGenericsField").hasMessageContaining("multiple Mockito annotations");
        }
    }

    @Test
    public void shouldScreamWhenInitializingCaptorsForNullClass() throws Exception {
        try {
            MockitoAnnotations.initMocks(null);
            Assert.fail();
        } catch (MockitoException e) {
        }
    }

    @Test
    public void shouldLookForAnnotatedCaptorsInSuperClasses() throws Exception {
        CaptorAnnotationTest.Sub sub = new CaptorAnnotationTest.Sub();
        MockitoAnnotations.initMocks(sub);
        Assert.assertNotNull(sub.getCaptor());
        Assert.assertNotNull(sub.getBaseCaptor());
        Assert.assertNotNull(sub.getSuperBaseCaptor());
    }

    class SuperBase {
        @Captor
        private ArgumentCaptor<IMethods> mock;

        public ArgumentCaptor<IMethods> getSuperBaseCaptor() {
            return mock;
        }
    }

    class Base extends CaptorAnnotationTest.SuperBase {
        @Captor
        private ArgumentCaptor<IMethods> mock;

        public ArgumentCaptor<IMethods> getBaseCaptor() {
            return mock;
        }
    }

    class Sub extends CaptorAnnotationTest.Base {
        @Captor
        private ArgumentCaptor<IMethods> mock;

        public ArgumentCaptor<IMethods> getCaptor() {
            return mock;
        }
    }
}

