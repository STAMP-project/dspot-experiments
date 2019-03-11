/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.extension.undertow.deployment;


import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;


public class WarMetaDataProcessorTest {
    private final List<String> EXPECTED_ORDER = ImmutableList.of(createWebOrdering1().getJar(), createWebOrdering2().getJar(), createWebOrdering3().getJar());

    @Test
    public void test1() {
        // sort: a.jar, b.jar, c.jar
        test(ImmutableList.of(createWebOrdering1(), createWebOrdering2(), createWebOrdering3()));
    }

    @Test
    public void test2() {
        // sort: a.jar, c.jar, b.jar
        test(ImmutableList.of(createWebOrdering1(), createWebOrdering3(), createWebOrdering2()));
    }

    @Test
    public void test3() {
        // sort: b.jar, a.jar, c.jar
        test(ImmutableList.of(createWebOrdering2(), createWebOrdering1(), createWebOrdering3()));
    }

    @Test
    public void test4() {
        // sort: b.jar, c.jar, a.jar
        test(ImmutableList.of(createWebOrdering2(), createWebOrdering3(), createWebOrdering1()));
    }

    @Test
    public void test5() {
        // sort: c.jar, a.jar, b.jar
        test(ImmutableList.of(createWebOrdering3(), createWebOrdering1(), createWebOrdering2()));
    }

    @Test
    public void test6() {
        // sort: c.jar, b.jar, a.jar
        test(ImmutableList.of(createWebOrdering3(), createWebOrdering2(), createWebOrdering1()));
    }
}

