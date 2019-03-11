/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.various;


import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.type.DbTimestampType;
import org.hibernate.type.TimestampType;
import org.junit.Test;


/**
 * Test for the @Timestamp annotation.
 *
 * @author Hardy Ferentschik
 */
public class TimestampTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    private MetadataImplementor metadata;

    @Test
    public void testTimestampSourceIsVM() throws Exception {
        assertTimestampSource(VMTimestamped.class, TimestampType.class);
    }

    @Test
    public void testTimestampSourceIsDB() throws Exception {
        assertTimestampSource(DBTimestamped.class, DbTimestampType.class);
    }
}

