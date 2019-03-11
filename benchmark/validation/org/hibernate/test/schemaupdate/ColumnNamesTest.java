/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import TargetType.SCRIPT;
import java.io.File;
import java.nio.file.Files;
import java.util.EnumSet;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author Andrea Boriero
 */
@RunWith(Parameterized.class)
public class ColumnNamesTest {
    @Parameterized.Parameter
    public String jdbcMetadataExtractorStrategy;

    private StandardServiceRegistry ssr;

    private Metadata metadata;

    private File output;

    @Test
    public void testSchemaUpdateWithQuotedColumnNames() throws Exception {
        new SchemaUpdate().setOutputFile(output.getAbsolutePath()).execute(EnumSet.of(SCRIPT), metadata);
        final String fileContent = new String(Files.readAllBytes(output.toPath()));
        Assert.assertThat("The update output file should be empty", fileContent, Is.is(""));
    }

    @Entity
    @Table(name = "Employee")
    public class Employee {
        @Id
        private long id;

        @Column(name = "`Age`")
        public String age;

        @Column(name = "Name")
        private String name;

        private String match;

        private String birthday;

        private String homeAddress;
    }
}

