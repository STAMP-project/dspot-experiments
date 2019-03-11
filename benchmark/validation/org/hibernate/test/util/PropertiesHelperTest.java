/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.util;


import java.util.Properties;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class PropertiesHelperTest extends BaseUnitTestCase {
    private Properties props;

    @Test
    public void testPlaceholderReplacement() {
        ConfigurationHelper.resolvePlaceHolders(props);
        String str = ConfigurationHelper.getString("my.nonexistent.prop", props, "did.not.exist");
        Assert.assertEquals("did.not.exist", str);
        str = ConfigurationHelper.getString("my.nonexistent.prop", props, null);
        Assert.assertNull(str);
        str = ConfigurationHelper.getString("my.string.prop", props, "na");
        Assert.assertEquals("replacement did not occur", "string", str);
        str = ConfigurationHelper.getString("my.string.prop", props, "did.not.exist");
        Assert.assertEquals("replacement did not occur", "string", str);
        boolean bool = ConfigurationHelper.getBoolean("my.nonexistent.prop", props);
        Assert.assertFalse("non-exists as boolean", bool);
        bool = ConfigurationHelper.getBoolean("my.nonexistent.prop", props, false);
        Assert.assertFalse("non-exists as boolean", bool);
        bool = ConfigurationHelper.getBoolean("my.nonexistent.prop", props, true);
        Assert.assertTrue("non-exists as boolean", bool);
        bool = ConfigurationHelper.getBoolean("my.boolean.prop", props);
        Assert.assertTrue("boolean replacement did not occur", bool);
        bool = ConfigurationHelper.getBoolean("my.boolean.prop", props, false);
        Assert.assertTrue("boolean replacement did not occur", bool);
        int i = ConfigurationHelper.getInt("my.nonexistent.prop", props, (-1));
        Assert.assertEquals((-1), i);
        i = ConfigurationHelper.getInt("my.int.prop", props, 100);
        Assert.assertEquals(1, i);
        Integer I = ConfigurationHelper.getInteger("my.nonexistent.prop", props);
        Assert.assertNull(I);
        I = ConfigurationHelper.getInteger("my.integer.prop", props);
        Assert.assertEquals(I, new Integer(1));
        str = props.getProperty("partial.prop1");
        Assert.assertEquals("partial replacement (ends)", "tmp/middle/dir/tmp.txt", str);
        str = props.getProperty("partial.prop2");
        Assert.assertEquals("partial replacement (midst)", "basedir/tmp/myfile.txt", str);
    }

    @Test
    public void testParseExceptions() {
        boolean b = ConfigurationHelper.getBoolean("parse.error", props);
        Assert.assertFalse("parse exception case - boolean", b);
        try {
            ConfigurationHelper.getInt("parse.error", props, 20);
            Assert.fail("parse exception case - int");
        } catch (NumberFormatException expected) {
        }
        try {
            ConfigurationHelper.getInteger("parse.error", props);
            Assert.fail("parse exception case - Integer");
        } catch (NumberFormatException expected) {
        }
    }
}

