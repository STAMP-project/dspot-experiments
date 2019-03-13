/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.kproject;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.ReleaseIdComparator;


public class ReleaseIdTest {
    private static final ReleaseId gav0 = ReleaseIdTest.newReleaseId("abc.def:ghi:9.0.1.GA");

    private static final ReleaseId gav1 = ReleaseIdTest.newReleaseId("com.test:foo:1.0.0-SNAPSHOT");

    private static final ReleaseId gav2 = ReleaseIdTest.newReleaseId("com.test:foo:1.0.0.Final");

    private static final ReleaseId gav3 = ReleaseIdTest.newReleaseId("com.test:foo:2.0.0-SNAPSHOT");

    private static final ReleaseId gav4 = ReleaseIdTest.newReleaseId("com.test:foo:2.0.0.Alpha1");

    private static final ReleaseId gav5 = ReleaseIdTest.newReleaseId("com.test:foo:2.0.0.Beta2");

    private static final ReleaseId gav6 = ReleaseIdTest.newReleaseId("org.example:test:0.0.1-SNAPSHOT");

    private static final ReleaseId gav7 = ReleaseIdTest.newReleaseId("org.example:test:1.0");

    @Test
    public void testDefaultSort() {
        List<ReleaseId> list = newUnsortedList();
        list.sort(new ReleaseIdComparator());
        Assert.assertSame(ReleaseIdTest.gav0, list.get(0));
        Assert.assertSame(ReleaseIdTest.gav1, list.get(1));
        Assert.assertSame(ReleaseIdTest.gav2, list.get(2));
        Assert.assertSame(ReleaseIdTest.gav3, list.get(3));
        Assert.assertSame(ReleaseIdTest.gav4, list.get(4));
        Assert.assertSame(ReleaseIdTest.gav5, list.get(5));
        Assert.assertSame(ReleaseIdTest.gav6, list.get(6));
        Assert.assertSame(ReleaseIdTest.gav7, list.get(7));
    }

    @Test
    public void testAscendingSort() {
        List<ReleaseId> list = newUnsortedList();
        list.sort(new ReleaseIdComparator(ASCENDING));
        Assert.assertSame(ReleaseIdTest.gav0, list.get(0));
        Assert.assertSame(ReleaseIdTest.gav1, list.get(1));
        Assert.assertSame(ReleaseIdTest.gav2, list.get(2));
        Assert.assertSame(ReleaseIdTest.gav3, list.get(3));
        Assert.assertSame(ReleaseIdTest.gav4, list.get(4));
        Assert.assertSame(ReleaseIdTest.gav5, list.get(5));
        Assert.assertSame(ReleaseIdTest.gav6, list.get(6));
        Assert.assertSame(ReleaseIdTest.gav7, list.get(7));
    }

    @Test
    public void testDecendingSort() {
        List<ReleaseId> list = newUnsortedList();
        list.sort(new ReleaseIdComparator(DESCENDING));
        Assert.assertSame(ReleaseIdTest.gav7, list.get(0));
        Assert.assertSame(ReleaseIdTest.gav6, list.get(1));
        Assert.assertSame(ReleaseIdTest.gav5, list.get(2));
        Assert.assertSame(ReleaseIdTest.gav4, list.get(3));
        Assert.assertSame(ReleaseIdTest.gav3, list.get(4));
        Assert.assertSame(ReleaseIdTest.gav2, list.get(5));
        Assert.assertSame(ReleaseIdTest.gav1, list.get(6));
        Assert.assertSame(ReleaseIdTest.gav0, list.get(7));
    }

    @Test
    public void testGetEarliest() {
        List<ReleaseId> list = newUnsortedList();
        Assert.assertSame(ReleaseIdTest.gav0, ReleaseIdComparator.getEarliest(list));
    }

    @Test
    public void testGetLatest() {
        List<ReleaseId> list = newUnsortedList();
        Assert.assertSame(ReleaseIdTest.gav7, ReleaseIdComparator.getLatest(list));
    }
}

