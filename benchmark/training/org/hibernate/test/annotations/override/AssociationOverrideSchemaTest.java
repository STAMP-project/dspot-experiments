/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.override;


import org.hibernate.dialect.H2Dialect;
import org.hibernate.mapping.Table;
import org.hibernate.test.util.SchemaUtil;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@RequiresDialect({ H2Dialect.class })
@TestForIssue(jiraKey = "HHH-6662")
public class AssociationOverrideSchemaTest extends BaseNonConfigCoreFunctionalTestCase {
    public static final String SCHEMA_NAME = "OTHER_SCHEMA";

    public static final String TABLE_NAME = "BLOG_TAGS";

    public static final String ID_COLUMN_NAME = "BLOG_ID";

    public static final String VALUE_COLUMN_NAME = "BLOG_TAG";

    @Test
    public void testJoinTableSchemaName() {
        for (Table table : metadata().collectTableMappings()) {
            if (AssociationOverrideSchemaTest.TABLE_NAME.equals(table.getName())) {
                Assert.assertEquals(AssociationOverrideSchemaTest.SCHEMA_NAME, table.getSchema());
                return;
            }
        }
        Assert.fail();
    }

    @Test
    public void testJoinTableJoinColumnName() {
        Assert.assertTrue(SchemaUtil.isColumnPresent(AssociationOverrideSchemaTest.TABLE_NAME, AssociationOverrideSchemaTest.ID_COLUMN_NAME, metadata()));
    }

    @Test
    public void testJoinTableColumnName() {
        Assert.assertTrue(SchemaUtil.isColumnPresent(AssociationOverrideSchemaTest.TABLE_NAME, AssociationOverrideSchemaTest.VALUE_COLUMN_NAME, metadata()));
    }
}

