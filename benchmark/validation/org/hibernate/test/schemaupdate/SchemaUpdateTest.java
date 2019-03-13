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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.testing.SkipLog;
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
public class SchemaUpdateTest {
    private boolean skipTest;

    @Parameterized.Parameter
    public String jdbcMetadataExtractorStrategy;

    private File output;

    private StandardServiceRegistry ssr;

    private MetadataImplementor metadata;

    @Test
    public void testSchemaUpdateAndValidation() throws Exception {
        if (skipTest) {
            SkipLog.reportSkip("skipping test because quoted names are not case-sensitive.");
            return;
        }
        new SchemaUpdate().setHaltOnError(true).execute(EnumSet.of(DATABASE), metadata);
        new SchemaValidator().validate(metadata);
        new SchemaUpdate().setHaltOnError(true).setOutputFile(output.getAbsolutePath()).setFormat(false).execute(EnumSet.of(DATABASE, SCRIPT), metadata);
        final String fileContent = new String(Files.readAllBytes(output.toPath()));
        Assert.assertThat("The update output file should be empty", fileContent, Is.is(""));
    }

    @Entity(name = "TestEntity")
    @Table(name = "`testentity`")
    public static class LowercaseTableNameEntity {
        @Id
        long id;

        String field1;

        @ManyToMany(mappedBy = "entities")
        Set<SchemaUpdateTest.TestEntity> entity1s;
    }

    @Entity(name = "TestEntity1")
    public static class TestEntity {
        @Id
        @Column(name = "`Id`")
        long id;

        String field1;

        @ManyToMany
        Set<SchemaUpdateTest.LowercaseTableNameEntity> entities;

        @OneToMany
        @JoinColumn
        private Set<SchemaUpdateTest.UppercaseTableNameEntity> entitie2s;

        @ManyToOne
        private SchemaUpdateTest.LowercaseTableNameEntity entity;
    }

    @Entity(name = "TestEntity2")
    @Table(name = "`TESTENTITY`")
    public static class UppercaseTableNameEntity {
        @Id
        long id;

        String field1;

        @ManyToOne
        SchemaUpdateTest.TestEntity testEntity;

        @ManyToOne
        @JoinColumn(foreignKey = @ForeignKey(name = "FK_mixedCase"))
        SchemaUpdateTest.MixedCaseTableNameEntity mixedCaseTableNameEntity;
    }

    @Entity(name = "TestEntity3")
    @Table(name = "`TESTentity`", indexes = { @Index(name = "index1", columnList = "`FieLd1`"), @Index(name = "Index2", columnList = "`FIELD_2`") })
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
        private Set<SchemaUpdateTest.Match> matches = new HashSet<>();
    }

    @Entity(name = "Match")
    public static class Match {
        @Id
        long id;

        String match;

        @ElementCollection
        @CollectionTable
        private Map<Integer, Integer> timeline = new TreeMap<>();
    }

    @Entity(name = "InheritanceRootEntity")
    @Inheritance(strategy = InheritanceType.JOINED)
    public static class InheritanceRootEntity {
        @Id
        protected Long id;
    }

    @Entity(name = "InheritanceChildEntity")
    @PrimaryKeyJoinColumn(name = "ID", foreignKey = @ForeignKey(name = "FK_ROOT"))
    public static class InheritanceChildEntity extends SchemaUpdateTest.InheritanceRootEntity {}

    @Entity(name = "InheritanceSecondChildEntity")
    @PrimaryKeyJoinColumn(name = "ID")
    public static class InheritanceSecondChildEntity extends SchemaUpdateTest.InheritanceRootEntity {
        @ManyToOne
        @JoinColumn
        public SchemaUpdateTest.Match match;
    }
}

