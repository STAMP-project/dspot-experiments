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


import DefaultExternalizer.CURRENCY;
import DefaultExternalizer.LOCALE;
import DefaultExternalizer.NATURAL_ORDER_COMPARATOR;
import DefaultExternalizer.OPTIONAL;
import DefaultExternalizer.REVERSE_ORDER_COMPARATOR;
import DefaultExternalizer.TIME_UNIT;
import DefaultExternalizer.TIME_ZONE;
import java.io.IOException;
import java.util.Comparator;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


/**
 * Unit test for java.util.* externalizers.
 *
 * @author Paul Ferraro
 */
public class UtilExternalizerTestCase {
    @Test
    public void test() throws IOException, ClassNotFoundException {
        new org.wildfly.clustering.marshalling.ExternalizerTester(CURRENCY.cast(Currency.class)).test(Currency.getInstance(Locale.US));
        new org.wildfly.clustering.marshalling.ExternalizerTester(LOCALE.cast(Locale.class)).test(Locale.US);
        new org.wildfly.clustering.marshalling.ExternalizerTester(NATURAL_ORDER_COMPARATOR.cast(Comparator.class)).test(Comparator.naturalOrder());
        new org.wildfly.clustering.marshalling.ExternalizerTester(OPTIONAL.cast(Optional.class)).test(Optional.empty());
        new org.wildfly.clustering.marshalling.ExternalizerTester(OPTIONAL.cast(Optional.class)).test(Optional.of(UUID.randomUUID()));
        new org.wildfly.clustering.marshalling.ExternalizerTester(REVERSE_ORDER_COMPARATOR.cast(Comparator.class)).test(Comparator.reverseOrder());
        new org.wildfly.clustering.marshalling.EnumExternalizerTester(TIME_UNIT.cast(TimeUnit.class)).test();
        new org.wildfly.clustering.marshalling.ExternalizerTester(TIME_ZONE.cast(TimeZone.class)).test(TimeZone.getDefault());
        new org.wildfly.clustering.marshalling.ExternalizerTester(TIME_ZONE.cast(TimeZone.class)).test(TimeZone.getTimeZone("America/New_York"));
        new org.wildfly.clustering.marshalling.ExternalizerTester(DefaultExternalizer.UUID.cast(UUID.class)).test(UUID.randomUUID());
    }
}

