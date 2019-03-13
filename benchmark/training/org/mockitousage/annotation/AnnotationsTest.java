/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.base.MockitoException;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class AnnotationsTest extends TestBase {
    @Retention(RetentionPolicy.RUNTIME)
    public @interface NotAMock {}

    @Mock
    List<?> list;

    @Mock
    final Map<Integer, String> map = new HashMap<Integer, String>();

    @AnnotationsTest.NotAMock
    Set<?> notAMock;

    @Mock
    List<?> listTwo;

    @Test
    public void shouldInitMocks() throws Exception {
        list.clear();
        map.clear();
        listTwo.clear();
        Mockito.verify(list).clear();
        Mockito.verify(map).clear();
        Mockito.verify(listTwo).clear();
    }

    @Test
    public void shouldScreamWhenInitializingMocksForNullClass() throws Exception {
        try {
            MockitoAnnotations.initMocks(null);
            Assert.fail();
        } catch (MockitoException e) {
            Assert.assertEquals("testClass cannot be null. For info how to use @Mock annotations see examples in javadoc for MockitoAnnotations class", e.getMessage());
        }
    }

    @Test
    public void shouldLookForAnnotatedMocksInSuperClasses() throws Exception {
        AnnotationsTest.Sub sub = new AnnotationsTest.Sub();
        MockitoAnnotations.initMocks(sub);
        Assert.assertNotNull(sub.getMock());
        Assert.assertNotNull(sub.getBaseMock());
        Assert.assertNotNull(sub.getSuperBaseMock());
    }

    @Mock(answer = Answers.RETURNS_MOCKS, name = "i have a name")
    IMethods namedAndReturningMocks;

    @Mock(answer = Answers.RETURNS_DEFAULTS)
    IMethods returningDefaults;

    @Mock(extraInterfaces = { List.class })
    IMethods hasExtraInterfaces;

    @Mock
    IMethods noExtraConfig;

    @Mock(stubOnly = true)
    IMethods stubOnly;

    @Test
    public void shouldInitMocksWithGivenSettings() throws Exception {
        Assert.assertEquals("i have a name", namedAndReturningMocks.toString());
        Assert.assertNotNull(namedAndReturningMocks.iMethodsReturningMethod());
        Assert.assertEquals("returningDefaults", returningDefaults.toString());
        Assert.assertEquals(0, returningDefaults.intReturningMethod());
        Assert.assertTrue(((hasExtraInterfaces) instanceof List));
        Assert.assertTrue(Mockito.mockingDetails(stubOnly).getMockCreationSettings().isStubOnly());
        Assert.assertEquals(0, noExtraConfig.intReturningMethod());
    }

    class SuperBase {
        @Mock
        private IMethods mock;

        public IMethods getSuperBaseMock() {
            return mock;
        }
    }

    class Base extends AnnotationsTest.SuperBase {
        @Mock
        private IMethods mock;

        public IMethods getBaseMock() {
            return mock;
        }
    }

    class Sub extends AnnotationsTest.Base {
        @Mock
        private IMethods mock;

        public IMethods getMock() {
            return mock;
        }
    }
}

