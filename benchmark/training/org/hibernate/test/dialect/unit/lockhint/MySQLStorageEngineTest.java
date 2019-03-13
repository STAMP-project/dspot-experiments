/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.unit.lockhint;


import AvailableSettings.STORAGE_ENGINE;
import java.lang.reflect.Field;
import java.util.Properties;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


public class MySQLStorageEngineTest extends BaseUnitTestCase {
    @Test
    public void testDefaultStorage() {
        Assert.assertEquals(" engine=InnoDB", new MySQL57Dialect().getTableTypeString());
    }

    @Test
    public void testOverrideStorage() throws IllegalAccessException, NoSuchFieldException {
        final Field globalPropertiesField = Environment.class.getDeclaredField("GLOBAL_PROPERTIES");
        globalPropertiesField.setAccessible(true);
        final Properties systemProperties = ((Properties) (globalPropertiesField.get(null)));
        Assert.assertNotNull(systemProperties);
        final Object previousValue = systemProperties.setProperty(STORAGE_ENGINE, "myisam");
        try {
            Assert.assertEquals(" engine=MyISAM", new MySQL57Dialect().getTableTypeString());
        } finally {
            if (previousValue != null) {
                systemProperties.setProperty(STORAGE_ENGINE, previousValue.toString());
            } else {
                systemProperties.remove(STORAGE_ENGINE);
            }
        }
    }
}

