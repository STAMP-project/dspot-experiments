/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
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
package org.wildfly.clustering.marshalling.spi.util;


import DefaultExternalizer.ATOMIC_BOOLEAN;
import DefaultExternalizer.ATOMIC_INTEGER;
import DefaultExternalizer.ATOMIC_LONG;
import DefaultExternalizer.ATOMIC_REFERENCE;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;


/**
 * Unit test for java.util.concurrent.atomic externalizers.
 *
 * @author Paul Ferraro
 */
public class AtomicExternalizerTestCase {
    @Test
    public void test() throws IOException, ClassNotFoundException {
        new org.wildfly.clustering.marshalling.ExternalizerTester(ATOMIC_BOOLEAN.cast(AtomicBoolean.class), ( expected, actual) -> assertEquals(expected.get(), actual.get())).test(new AtomicBoolean(true));
        new org.wildfly.clustering.marshalling.ExternalizerTester(ATOMIC_INTEGER.cast(AtomicInteger.class), ( expected, actual) -> assertEquals(expected.get(), actual.get())).test(new AtomicInteger(Integer.MAX_VALUE));
        new org.wildfly.clustering.marshalling.ExternalizerTester(ATOMIC_LONG.cast(AtomicLong.class), ( expected, actual) -> assertEquals(expected.get(), actual.get())).test(new AtomicLong(Long.MAX_VALUE));
        new org.wildfly.clustering.marshalling.ExternalizerTester(ATOMIC_REFERENCE.cast(AtomicReference.class), ( expected, actual) -> assertEquals(expected.get(), actual.get())).test(new AtomicReference<Object>(Boolean.TRUE));
    }
}

