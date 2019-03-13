/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate.foreignkeys.definition;


import java.io.File;
import java.nio.file.Files;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad MIhalcea
 */
public abstract class AbstractForeignKeyDefinitionTest extends BaseUnitTestCase {
    private File output;

    private StandardServiceRegistry ssr;

    private MetadataImplementor metadata;

    @Test
    @TestForIssue(jiraKey = "HHH-10643")
    public void testForeignKeyDefinitionOverridesDefaultNamingStrategy() throws Exception {
        String fileContent = new String(Files.readAllBytes(output.toPath()));
        Assert.assertTrue(("Script file : " + fileContent), validate(fileContent));
    }
}

