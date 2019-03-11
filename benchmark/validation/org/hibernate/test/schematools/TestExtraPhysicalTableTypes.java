/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schematools;


import java.sql.Connection;
import org.hamcrest.core.Is;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.testing.TestForIssue;
import org.hibernate.tool.schema.extract.internal.InformationExtractorJdbcDatabaseMetaDataImpl;
import org.hibernate.tool.schema.extract.spi.ExtractionContext;
import org.hibernate.tool.schema.internal.exec.JdbcContext;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10298")
public class TestExtraPhysicalTableTypes {
    private StandardServiceRegistry ssr;

    private MetadataImplementor metadata;

    @Test
    public void testAddOneExtraPhysicalTableType() throws Exception {
        buildMetadata("BASE TABLE");
        TestExtraPhysicalTableTypes.InformationExtractorJdbcDatabaseMetaDataImplTest informationExtractor = buildInformationExtractorJdbcDatabaseMetaDataImplTest();
        Assert.assertThat(informationExtractor.isPhysicalTableType("BASE TABLE"), Is.is(true));
        Assert.assertThat(informationExtractor.isPhysicalTableType("TABLE"), Is.is(true));
    }

    @Test
    public void testAddingMultipleExtraPhysicalTableTypes() throws Exception {
        buildMetadata("BASE, BASE TABLE");
        TestExtraPhysicalTableTypes.InformationExtractorJdbcDatabaseMetaDataImplTest informationExtractor = buildInformationExtractorJdbcDatabaseMetaDataImplTest();
        Assert.assertThat(informationExtractor.isPhysicalTableType("BASE TABLE"), Is.is(true));
        Assert.assertThat(informationExtractor.isPhysicalTableType("BASE"), Is.is(true));
        Assert.assertThat(informationExtractor.isPhysicalTableType("TABLE"), Is.is(true));
        Assert.assertThat(informationExtractor.isPhysicalTableType("TABLE 1"), Is.is(false));
    }

    @Test
    public void testExtraPhysicalTableTypesPropertyEmptyStringValue() throws Exception {
        buildMetadata("  ");
        TestExtraPhysicalTableTypes.InformationExtractorJdbcDatabaseMetaDataImplTest informationExtractor = buildInformationExtractorJdbcDatabaseMetaDataImplTest();
        Assert.assertThat(informationExtractor.isPhysicalTableType("BASE TABLE"), Is.is(false));
        Assert.assertThat(informationExtractor.isPhysicalTableType("TABLE"), Is.is(true));
    }

    @Test
    public void testNoExtraPhysicalTabeTypesProperty() throws Exception {
        buildMetadata(null);
        TestExtraPhysicalTableTypes.InformationExtractorJdbcDatabaseMetaDataImplTest informationExtractor = buildInformationExtractorJdbcDatabaseMetaDataImplTest();
        Assert.assertThat(informationExtractor.isPhysicalTableType("BASE TABLE"), Is.is(false));
        Assert.assertThat(informationExtractor.isPhysicalTableType("TABLE"), Is.is(true));
    }

    public class InformationExtractorJdbcDatabaseMetaDataImplTest extends InformationExtractorJdbcDatabaseMetaDataImpl {
        public InformationExtractorJdbcDatabaseMetaDataImplTest(ExtractionContext extractionContext) {
            super(extractionContext);
        }

        public boolean isPhysicalTableType(String tableType) {
            return super.isPhysicalTableType(tableType);
        }
    }

    class DdlTransactionIsolatorImpl implements DdlTransactionIsolator {
        @Override
        public JdbcContext getJdbcContext() {
            return null;
        }

        @Override
        public void prepare() {
        }

        @Override
        public Connection getIsolatedConnection() {
            return null;
        }

        @Override
        public void release() {
        }
    }
}

