/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.enums;


import EnumType.ENUM;
import EnumType.NAMED;
import java.util.Properties;
import org.hibernate.internal.util.SerializationHelper;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.type.EnumType;
import org.hibernate.type.spi.TypeConfiguration;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class TestEnumTypeSerialization extends BaseUnitTestCase {
    @Test
    public void testSerializability() {
        TypeConfiguration typeConfiguration = new TypeConfiguration();
        {
            // test ordinal mapping
            EnumType enumType = new EnumType();
            enumType.setTypeConfiguration(typeConfiguration);
            Properties properties = new Properties();
            properties.put(ENUM, UnspecifiedEnumTypeEntity.E1.class.getName());
            enumType.setParameterValues(properties);
            Assert.assertTrue(enumType.isOrdinal());
            SerializationHelper.clone(enumType);
        }
        {
            // test named mapping
            EnumType enumType = new EnumType();
            enumType.setTypeConfiguration(typeConfiguration);
            Properties properties = new Properties();
            properties.put(ENUM, UnspecifiedEnumTypeEntity.E1.class.getName());
            properties.put(NAMED, "true");
            enumType.setParameterValues(properties);
            Assert.assertFalse(enumType.isOrdinal());
            SerializationHelper.clone(enumType);
        }
    }
}

