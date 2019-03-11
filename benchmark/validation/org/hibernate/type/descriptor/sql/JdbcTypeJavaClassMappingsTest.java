/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.type.descriptor.sql;


import JdbcTypeJavaClassMappings.INSTANCE;
import java.sql.Types;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tiger Wang
 */
public class JdbcTypeJavaClassMappingsTest {
    @Test
    public void testDetermineJdbcTypeCodeForJavaClass() throws Exception {
        int jdbcTypeCode = INSTANCE.determineJdbcTypeCodeForJavaClass(Short.class);
        Assert.assertEquals(jdbcTypeCode, Types.SMALLINT);
    }

    @Test
    public void testDetermineJavaClassForJdbcTypeCodeTypeCode() throws Exception {
        Class javaClass = INSTANCE.determineJavaClassForJdbcTypeCode(Types.SMALLINT);
        Assert.assertEquals(javaClass, Short.class);
    }
}

