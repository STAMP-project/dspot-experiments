/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.property;


import java.util.Date;
import org.hibernate.mapping.Map;
import org.hibernate.property.access.internal.PropertyAccessStrategyMapImpl;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


public class PropertyAccessStrategyMapTest extends BaseUnitTestCase {
    @Test
    public void testBasicMapClass() {
        testBasic(Map.class);
    }

    @Test
    public void testBasicNullClass() {
        testBasic(null);
    }

    @Test
    public void testNonMap() {
        final PropertyAccessStrategyMapImpl accessStrategy = PropertyAccessStrategyMapImpl.INSTANCE;
        try {
            accessStrategy.buildPropertyAccess(Date.class, "time");
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Expecting class: [org.hibernate.mapping.Map], but containerJavaType is of type: [java.util.Date] for propertyName: [time]", e.getMessage());
        }
    }
}

