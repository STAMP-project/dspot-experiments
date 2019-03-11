/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.annotation;


import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.util.MockUtil;
import org.mockitoutil.TestBase;


@SuppressWarnings("unchecked")
public class SpyAnnotationInitializedInBaseClassTest extends TestBase {
    class BaseClass {
        @Spy
        List list = new LinkedList();
    }

    class SubClass extends SpyAnnotationInitializedInBaseClassTest.BaseClass {}

    @Test
    public void shouldInitSpiesInBaseClass() throws Exception {
        // given
        SpyAnnotationInitializedInBaseClassTest.SubClass subClass = new SpyAnnotationInitializedInBaseClassTest.SubClass();
        // when
        MockitoAnnotations.initMocks(subClass);
        // then
        Assert.assertTrue(MockUtil.isMock(subClass.list));
    }

    @Spy
    List spyInBaseclass = new LinkedList();

    public static class SubTest extends SpyAnnotationInitializedInBaseClassTest {
        @Spy
        List spyInSubclass = new LinkedList();

        @Test
        public void shouldInitSpiesInHierarchy() throws Exception {
            Assert.assertTrue(MockUtil.isMock(spyInSubclass));
            Assert.assertTrue(MockUtil.isMock(spyInBaseclass));
        }
    }
}

