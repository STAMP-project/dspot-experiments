/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test;


import org.hibernate.jpamodelgen.test.util.TestForIssue;
import org.hibernate.jpamodelgen.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Hardy Ferentschik
 */
public class StringUtilTest {
    @Test
    public void testIsPropertyName() {
        Assert.assertTrue(StringUtil.isProperty("getFoo", "java.lang.Object"));
        Assert.assertTrue(StringUtil.isProperty("isFoo", "Boolean"));
        Assert.assertTrue(StringUtil.isProperty("hasFoo", "java.lang.Boolean"));
        Assert.assertFalse(StringUtil.isProperty("isfoo", "void"));
        Assert.assertFalse(StringUtil.isProperty("hasfoo", "java.lang.Object"));
        Assert.assertFalse(StringUtil.isProperty("", "java.lang.Object"));
        Assert.assertFalse(StringUtil.isProperty(null, "java.lang.Object"));
    }

    @Test
    @TestForIssue(jiraKey = "METAGEN-76")
    public void testHashCodeNotAProperty() {
        Assert.assertFalse(StringUtil.isProperty("hashCode", "Integer"));
    }

    @Test
    public void testGetUpperUnderscoreCaseFromLowerCamelCase() {
        Assert.assertEquals("USER_PARENT_NAME", StringUtil.getUpperUnderscoreCaseFromLowerCamelCase("userParentName"));
    }
}

