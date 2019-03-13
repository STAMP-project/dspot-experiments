/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.schemagen;


import AvailableSettings.HBM2DDL_CREATE_SCRIPT_SOURCE;
import AvailableSettings.HBM2DDL_CREATE_SOURCE;
import AvailableSettings.HBM2DDL_DATABASE_ACTION;
import AvailableSettings.HBM2DDL_DROP_SCRIPT_SOURCE;
import AvailableSettings.HBM2DDL_DROP_SOURCE;
import AvailableSettings.HBM2DDL_LOAD_SCRIPT_SOURCE;
import java.util.Map;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(H2Dialect.class)
public class JpaSchemaGeneratorTest extends BaseEntityManagerFunctionalTestCase {
    private final String LOAD_SQL = (getScriptFolderPath()) + "load-script-source.sql";

    private final String CREATE_SQL = (getScriptFolderPath()) + "create-script-source.sql";

    private final String DROP_SQL = (getScriptFolderPath()) + "drop-script-source.sql";

    private static int schemagenNumber = 0;

    @SuppressWarnings("unchecked")
    @Test
    @TestForIssue(jiraKey = "HHH-8271")
    public void testSqlLoadScriptSourceClasspath() throws Exception {
        Map settings = buildSettings();
        settings.put(HBM2DDL_DATABASE_ACTION, "drop-and-create");
        settings.put(HBM2DDL_LOAD_SCRIPT_SOURCE, getLoadSqlScript());
        doTest(settings);
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestForIssue(jiraKey = "HHH-8271")
    public void testSqlLoadScriptSourceUrl() throws Exception {
        Map settings = buildSettings();
        settings.put(HBM2DDL_DATABASE_ACTION, "drop-and-create");
        settings.put(HBM2DDL_LOAD_SCRIPT_SOURCE, getResourceUrlString(getLoadSqlScript()));
        doTest(settings);
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestForIssue(jiraKey = "HHH-8271")
    public void testSqlCreateScriptSourceClasspath() throws Exception {
        Map settings = buildSettings();
        settings.put(HBM2DDL_DATABASE_ACTION, "drop-and-create");
        settings.put(HBM2DDL_CREATE_SOURCE, "metadata-then-script");
        settings.put(HBM2DDL_CREATE_SCRIPT_SOURCE, getCreateSqlScript());
        doTest(settings);
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestForIssue(jiraKey = "HHH-8271")
    public void testSqlCreateScriptSourceUrl() throws Exception {
        Map settings = buildSettings();
        settings.put(HBM2DDL_DATABASE_ACTION, "drop-and-create");
        settings.put(HBM2DDL_CREATE_SOURCE, "metadata-then-script");
        settings.put(HBM2DDL_CREATE_SCRIPT_SOURCE, getResourceUrlString(getCreateSqlScript()));
        doTest(settings);
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestForIssue(jiraKey = "HHH-8271")
    public void testSqlDropScriptSourceClasspath() throws Exception {
        Map settings = buildSettings();
        settings.put(HBM2DDL_DROP_SOURCE, "metadata-then-script");
        settings.put(HBM2DDL_DATABASE_ACTION, "drop");
        settings.put(HBM2DDL_DROP_SCRIPT_SOURCE, getDropSqlScript());
        doTest(settings);
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestForIssue(jiraKey = "HHH-8271")
    public void testSqlDropScriptSourceUrl() throws Exception {
        Map settings = buildSettings();
        settings.put(HBM2DDL_DROP_SOURCE, "metadata-then-script");
        settings.put(HBM2DDL_DATABASE_ACTION, "drop");
        settings.put(HBM2DDL_DROP_SCRIPT_SOURCE, getResourceUrlString(getDropSqlScript()));
        doTest(settings);
    }
}

