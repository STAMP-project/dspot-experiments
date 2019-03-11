/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate.foreignkeys;


import DialectChecks.SupportDropConstraints;
import TargetType.DATABASE;
import TargetType.SCRIPT;
import java.io.File;
import java.util.EnumSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-12271")
@RequiresDialectFeature(SupportDropConstraints.class)
public class ForeignKeyDropTest extends BaseUnitTestCase {
    private File output;

    private MetadataImplementor metadata;

    private StandardServiceRegistry ssr;

    private SchemaExport schemaExport;

    @Test
    @TestForIssue(jiraKey = "HHH-11236")
    public void testForeignKeyDropIsCorrectlyGenerated() throws Exception {
        schemaExport.drop(EnumSet.of(SCRIPT, DATABASE), metadata);
        Assert.assertThat("The ddl foreign key drop command has not been properly generated", checkDropForeignKeyConstraint("CHILD_ENTITY"), Is.is(true));
    }

    @Entity(name = "ParentEntity")
    @Table(name = "PARENT_ENTITY")
    public static class ParentEntity {
        @Id
        private Long id;

        @OneToMany
        @JoinColumn(name = "PARENT")
        Set<ForeignKeyDropTest.ChildEntity> children;
    }

    @Entity(name = "ChildEntity")
    @Table(name = "CHILD_ENTITY")
    public static class ChildEntity {
        @Id
        private Long id;
    }
}

