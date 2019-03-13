/**
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.core.annotation;


import AnnotationAwareOrderComparator.INSTANCE;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Priority;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Oliver Gierke
 */
public class AnnotationAwareOrderComparatorTests {
    @Test
    public void instanceVariableIsAnAnnotationAwareOrderComparator() {
        Assert.assertThat(INSTANCE, CoreMatchers.is(CoreMatchers.instanceOf(AnnotationAwareOrderComparator.class)));
    }

    @Test
    public void sortInstances() {
        List<Object> list = new ArrayList<>();
        list.add(new AnnotationAwareOrderComparatorTests.B());
        list.add(new AnnotationAwareOrderComparatorTests.A());
        AnnotationAwareOrderComparator.sort(list);
        Assert.assertTrue(((list.get(0)) instanceof AnnotationAwareOrderComparatorTests.A));
        Assert.assertTrue(((list.get(1)) instanceof AnnotationAwareOrderComparatorTests.B));
    }

    @Test
    public void sortInstancesWithPriority() {
        List<Object> list = new ArrayList<>();
        list.add(new AnnotationAwareOrderComparatorTests.B2());
        list.add(new AnnotationAwareOrderComparatorTests.A2());
        AnnotationAwareOrderComparator.sort(list);
        Assert.assertTrue(((list.get(0)) instanceof AnnotationAwareOrderComparatorTests.A2));
        Assert.assertTrue(((list.get(1)) instanceof AnnotationAwareOrderComparatorTests.B2));
    }

    @Test
    public void sortInstancesWithOrderAndPriority() {
        List<Object> list = new ArrayList<>();
        list.add(new AnnotationAwareOrderComparatorTests.B());
        list.add(new AnnotationAwareOrderComparatorTests.A2());
        AnnotationAwareOrderComparator.sort(list);
        Assert.assertTrue(((list.get(0)) instanceof AnnotationAwareOrderComparatorTests.A2));
        Assert.assertTrue(((list.get(1)) instanceof AnnotationAwareOrderComparatorTests.B));
    }

    @Test
    public void sortInstancesWithSubclass() {
        List<Object> list = new ArrayList<>();
        list.add(new AnnotationAwareOrderComparatorTests.B());
        list.add(new AnnotationAwareOrderComparatorTests.C());
        AnnotationAwareOrderComparator.sort(list);
        Assert.assertTrue(((list.get(0)) instanceof AnnotationAwareOrderComparatorTests.C));
        Assert.assertTrue(((list.get(1)) instanceof AnnotationAwareOrderComparatorTests.B));
    }

    @Test
    public void sortClasses() {
        List<Object> list = new ArrayList<>();
        list.add(AnnotationAwareOrderComparatorTests.B.class);
        list.add(AnnotationAwareOrderComparatorTests.A.class);
        AnnotationAwareOrderComparator.sort(list);
        Assert.assertEquals(AnnotationAwareOrderComparatorTests.A.class, list.get(0));
        Assert.assertEquals(AnnotationAwareOrderComparatorTests.B.class, list.get(1));
    }

    @Test
    public void sortClassesWithSubclass() {
        List<Object> list = new ArrayList<>();
        list.add(AnnotationAwareOrderComparatorTests.B.class);
        list.add(AnnotationAwareOrderComparatorTests.C.class);
        AnnotationAwareOrderComparator.sort(list);
        Assert.assertEquals(AnnotationAwareOrderComparatorTests.C.class, list.get(0));
        Assert.assertEquals(AnnotationAwareOrderComparatorTests.B.class, list.get(1));
    }

    @Test
    public void sortWithNulls() {
        List<Object> list = new ArrayList<>();
        list.add(null);
        list.add(AnnotationAwareOrderComparatorTests.B.class);
        list.add(null);
        list.add(AnnotationAwareOrderComparatorTests.A.class);
        AnnotationAwareOrderComparator.sort(list);
        Assert.assertEquals(AnnotationAwareOrderComparatorTests.A.class, list.get(0));
        Assert.assertEquals(AnnotationAwareOrderComparatorTests.B.class, list.get(1));
        Assert.assertNull(list.get(2));
        Assert.assertNull(list.get(3));
    }

    @Order(1)
    private static class A {}

    @Order(2)
    private static class B {}

    private static class C extends AnnotationAwareOrderComparatorTests.A {}

    @Priority(1)
    private static class A2 {}

    @Priority(2)
    private static class B2 {}
}

