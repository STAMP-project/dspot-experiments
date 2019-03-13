/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.engine.jdbc.cursor.internal;


import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.hamcrest.CoreMatchers;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit test of the {@link StandardRefCursorSupport} class.
 *
 * @author Daniel Heinrich
 */
@TestForIssue(jiraKey = "HHH-10612")
public class StandardRefCursorSupportTest {
    interface TestDatabaseMetaData extends DatabaseMetaData {
        boolean supportsRefCursors() throws SQLException;
    }

    @Test
    public void testSupportsRefCursorsAboveJava8() throws Exception {
        StandardRefCursorSupportTest.TestDatabaseMetaData metaMock = Mockito.mock(StandardRefCursorSupportTest.TestDatabaseMetaData.class);
        Mockito.when(metaMock.supportsRefCursors()).thenReturn(true);
        boolean result = StandardRefCursorSupport.supportsRefCursors(metaMock);
        Assert.assertThat(result, CoreMatchers.is(true));
    }
}

