/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import TargetType.DATABASE;
import TargetType.SCRIPT;
import java.io.File;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.hbm2ddl.SchemaValidator;
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
@RequiresDialect(SQLServerDialect.class)
public class SchemaUpdateSQLServerTest extends BaseUnitTestCase {
    @Parameterized.Parameter
    public String jdbcMetadataExtractorStrategy;

    private File output;

    private StandardServiceRegistry ssr;

    private MetadataImplementor metadata;

    @Test
    public void testSchemaUpdateAndValidation() throws Exception {
        if (!(SQLServerDialect.class.isAssignableFrom(Dialect.getDialect().getClass()))) {
            return;
        }
        new SchemaUpdate().setHaltOnError(true).execute(EnumSet.of(DATABASE), metadata);
        new SchemaValidator().validate(metadata);
        new SchemaUpdate().setHaltOnError(true).setOutputFile(output.getAbsolutePath()).setFormat(false).execute(EnumSet.of(DATABASE, SCRIPT), metadata);
        final String fileContent = new String(Files.readAllBytes(output.toPath()));
        Assert.assertThat("The update output file should be empty", fileContent, Is.is(""));
    }

    @Entity(name = "TestEntity")
    @Table(name = "`testentity`", catalog = "hibernate_orm_test_collation", schema = "dbo")
    public static class LowercaseTableNameEntity {
        @Id
        long id;

        String field1;

        @ManyToMany(mappedBy = "entities")
        Set<SchemaUpdateSQLServerTest.TestEntity> entity1s;
    }

    @Entity(name = "TestEntity1")
    @Table(name = "TestEntity1", catalog = "hibernate_orm_test_collation", schema = "dbo")
    public static class TestEntity {
        @Id
        @Column(name = "`Id`")
        long id;

        String field1;

        @ManyToMany
        @JoinTable(catalog = "hibernate_orm_test_collation", schema = "dbo")
        Set<SchemaUpdateSQLServerTest.LowercaseTableNameEntity> entities;

        @OneToMany
        @JoinColumn
        private Set<SchemaUpdateSQLServerTest.UppercaseTableNameEntity> entitie2s;

        @ManyToOne
        private SchemaUpdateSQLServerTest.LowercaseTableNameEntity entity;
    }

    @Entity(name = "TestEntity2")
    @Table(name = "`TESTENTITY`", catalog = "hibernate_orm_test_collation", schema = "dbo")
    public static class UppercaseTableNameEntity {
        @Id
        long id;

        String field1;

        @ManyToOne
        SchemaUpdateSQLServerTest.TestEntity testEntity;

        @ManyToOne
        @JoinColumn(foreignKey = @ForeignKey(name = "FK_mixedCase"))
        SchemaUpdateSQLServerTest.MixedCaseTableNameEntity mixedCaseTableNameEntity;
    }

    @Entity(name = "TestEntity3")
    @Table(name = "`TESTentity`", catalog = "hibernate_orm_test_collation", schema = "dbo", indexes = { @Index(name = "index1", columnList = "`FieLd1`"), @Index(name = "Index2", columnList = "`FIELD_2`") })
    public static class MixedCaseTableNameEntity {
        @Id
        long id;

        @Column(name = "`FieLd1`")
        String field1;

        @Column(name = "`FIELD_2`")
        String field2;

        @Column(name = "`field_3`")
        String field3;

        String field4;

        @OneToMany
        @JoinColumn
        private Set<SchemaUpdateSQLServerTest.Match> matches = new HashSet<>();
    }

    @Entity(name = "Match")
    @Table(name = "Match", catalog = "hibernate_orm_test_collation", schema = "dbo")
    public static class Match {
        @Id
        long id;

        String match;

        @ElementCollection
        @CollectionTable(catalog = "hibernate_orm_test_collation", schema = "dbo")
        private Map<Integer, Integer> timeline = new TreeMap<>();
    }

    @Entity(name = "InheritanceRootEntity")
    @Table(name = "InheritanceRootEntity", catalog = "hibernate_orm_test_collation", schema = "dbo")
    @Inheritance(strategy = InheritanceType.JOINED)
    public static class InheritanceRootEntity {
        @Id
        protected Long id;
    }

    @Entity(name = "InheritanceChildEntity")
    @Table(name = "InheritanceChildEntity", catalog = "hibernate_orm_test_collation", schema = "dbo")
    @PrimaryKeyJoinColumn(name = "ID", foreignKey = @ForeignKey(name = "FK_ROOT"))
    public static class InheritanceChildEntity extends SchemaUpdateSQLServerTest.InheritanceRootEntity {}

    @Entity(name = "InheritanceSecondChildEntity")
    @Table(name = "InheritanceSecondChildEntity", catalog = "hibernate_orm_test_collation", schema = "dbo")
    @PrimaryKeyJoinColumn(name = "ID")
    public static class InheritanceSecondChildEntity extends SchemaUpdateSQLServerTest.InheritanceRootEntity {
        @ManyToOne
        @JoinColumn
        public SchemaUpdateSQLServerTest.Match match;
    }
}

