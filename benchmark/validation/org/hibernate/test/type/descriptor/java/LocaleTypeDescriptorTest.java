/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type.descriptor.java;


import LocaleTypeDescriptor.INSTANCE;
import java.util.Locale;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests of the {@link LocaleTypeDescriptor} class.
 *
 * @author Christian Beikov
 * @author Steve Ebersole
 */
public class LocaleTypeDescriptorTest extends BaseUnitTestCase {
    @Test
    public void testConversionFromString() {
        Assert.assertEquals(toLocale("de", null, null), INSTANCE.fromString("de"));
        Assert.assertEquals(toLocale("de", "DE", null), INSTANCE.fromString("de_DE"));
        Assert.assertEquals(toLocale(null, "DE", null), INSTANCE.fromString("_DE"));
        Assert.assertEquals(toLocale(null, null, "ch123"), INSTANCE.fromString("__ch123"));
        Assert.assertEquals(toLocale(null, "DE", "ch123"), INSTANCE.fromString("_DE_ch123"));
        Assert.assertEquals(toLocale("de", null, "ch123"), INSTANCE.fromString("de__ch123"));
        Assert.assertEquals(toLocale("de", "DE", "ch123"), INSTANCE.fromString("de_DE_ch123"));
        Assert.assertEquals(toLocale("", "", ""), INSTANCE.fromString(""));
        Assert.assertEquals(Locale.ROOT, INSTANCE.fromString(""));
    }
}

