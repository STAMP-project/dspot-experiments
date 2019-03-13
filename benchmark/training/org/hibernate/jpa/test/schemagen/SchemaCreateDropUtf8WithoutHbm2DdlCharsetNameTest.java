/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.schemagen;


import java.io.File;
import java.nio.file.Files;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class SchemaCreateDropUtf8WithoutHbm2DdlCharsetNameTest {
    private File createSchema;

    private File dropSchema;

    private EntityManagerFactoryBuilder entityManagerFactoryBuilder;

    @Test
    @TestForIssue(jiraKey = "HHH-10972")
    public void testEncoding() throws Exception {
        entityManagerFactoryBuilder.generateSchema();
        final String fileContent = new String(Files.readAllBytes(createSchema.toPath())).toLowerCase();
        Assert.assertTrue(fileContent.contains(expectedTableName()));
        Assert.assertTrue(fileContent.contains(expectedFieldName()));
        final String dropFileContent = new String(Files.readAllBytes(dropSchema.toPath())).toLowerCase();
        Assert.assertTrue(dropFileContent.contains(expectedTableName()));
    }

    @Entity
    @Table(name = ("test_" + ((char) (233))) + "ntity")
    public static class TestEntity {
        @Id
        @Column(name = ("fi" + ((char) (233))) + "ld")
        private String field;
    }
}

