package org.hibernate.test.schemafilter;


import AvailableSettings.DIALECT;
import AvailableSettings.FORMAT_SQL;
import DialectChecks.SupportSchemaCreation;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.mapping.Table;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.ServiceRegistryBuilder;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.schema.internal.DefaultSchemaFilter;
import org.hibernate.tool.schema.spi.SchemaFilter;
import org.junit.Assert;
import org.junit.Test;

import static org.hibernate.test.schemafilter.RecordingTarget.Category.SCHEMA_CREATE;
import static org.hibernate.test.schemafilter.RecordingTarget.Category.SCHEMA_DROP;
import static org.hibernate.test.schemafilter.RecordingTarget.Category.TABLE_CREATE;
import static org.hibernate.test.schemafilter.RecordingTarget.Category.TABLE_DROP;


@TestForIssue(jiraKey = "HHH-9876")
@SuppressWarnings({ "rawtypes", "unchecked" })
@RequiresDialectFeature({ SupportSchemaCreation.class })
public class SchemaFilterTest extends BaseUnitTestCase {
    private final StandardServiceRegistryImpl serviceRegistry;

    private final Metadata metadata;

    public SchemaFilterTest() {
        Map settings = new HashMap();
        settings.putAll(Environment.getProperties());
        settings.put(DIALECT, SQLServerDialect.class.getName());
        settings.put(FORMAT_SQL, false);
        this.serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry(settings);
        MetadataSources ms = new MetadataSources(serviceRegistry);
        ms.addAnnotatedClass(SchemaFilterTest.SchemaNoneEntity0.class);
        ms.addAnnotatedClass(SchemaFilterTest.Schema1Entity1.class);
        ms.addAnnotatedClass(SchemaFilterTest.Schema1Entity2.class);
        ms.addAnnotatedClass(SchemaFilterTest.Schema2Entity3.class);
        ms.addAnnotatedClass(SchemaFilterTest.Schema2Entity4.class);
        this.metadata = ms.buildMetadata();
    }

    @Test
    public void createSchema_unfiltered() {
        RecordingTarget target = doCreation(new DefaultSchemaFilter());
        Assert.assertThat(target.getActions(SCHEMA_CREATE), containsExactly("the_schema_1", "the_schema_2"));
        Assert.assertThat(target.getActions(TABLE_CREATE), containsExactly("the_entity_0", "the_schema_1.the_entity_1", "the_schema_1.the_entity_2", "the_schema_2.the_entity_3", "the_schema_2.the_entity_4"));
    }

    @Test
    public void createSchema_filtered() {
        RecordingTarget target = doCreation(new SchemaFilterTest.TestSchemaFilter());
        Assert.assertThat(target.getActions(SCHEMA_CREATE), containsExactly("the_schema_1"));
        Assert.assertThat(target.getActions(TABLE_CREATE), containsExactly("the_entity_0", "the_schema_1.the_entity_1"));
    }

    @Test
    public void dropSchema_unfiltered() {
        RecordingTarget target = doDrop(new DefaultSchemaFilter());
        Assert.assertThat(target.getActions(SCHEMA_DROP), containsExactly("the_schema_1", "the_schema_2"));
        Assert.assertThat(target.getActions(TABLE_DROP), containsExactly("the_entity_0", "the_schema_1.the_entity_1", "the_schema_1.the_entity_2", "the_schema_2.the_entity_3", "the_schema_2.the_entity_4"));
    }

    @Test
    public void dropSchema_filtered() {
        RecordingTarget target = doDrop(new SchemaFilterTest.TestSchemaFilter());
        Assert.assertThat(target.getActions(SCHEMA_DROP), containsExactly("the_schema_1"));
        Assert.assertThat(target.getActions(TABLE_DROP), containsExactly("the_entity_0", "the_schema_1.the_entity_1"));
    }

    private static class TestSchemaFilter implements SchemaFilter {
        @Override
        public boolean includeNamespace(Namespace namespace) {
            // exclude schema "the_schema_2"
            Identifier identifier = namespace.getName().getSchema();
            if (identifier != null) {
                return !("the_schema_2".equals(identifier.getText()));
            }
            return true;
        }

        @Override
        public boolean includeTable(Table table) {
            // exclude table "the_entity_2"
            return !("the_entity_2".equals(table.getName()));
        }

        @Override
        public boolean includeSequence(Sequence sequence) {
            return true;
        }
    }

    @Entity
    @Table(name = "the_entity_1", schema = "the_schema_1")
    public static class Schema1Entity1 {
        @Id
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    @Entity
    @Table(name = "the_entity_2", schema = "the_schema_1")
    public static class Schema1Entity2 {
        @Id
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    @Entity
    @Table(name = "the_entity_3", schema = "the_schema_2")
    public static class Schema2Entity3 {
        @Id
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    @Entity
    @Table(name = "the_entity_4", schema = "the_schema_2")
    public static class Schema2Entity4 {
        @Id
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    @Entity
    @Table(name = "the_entity_0")
    public static class SchemaNoneEntity0 {
        @Id
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }
}

