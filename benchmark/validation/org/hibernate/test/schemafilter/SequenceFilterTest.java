/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemafilter;


import DialectChecks.SupportSchemaCreation;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.mapping.Table;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.schema.internal.DefaultSchemaFilter;
import org.hibernate.tool.schema.spi.SchemaFilter;
import org.junit.Assert;
import org.junit.Test;

import static org.hibernate.test.schemafilter.RecordingTarget.Category.SEQUENCE_CREATE;
import static org.hibernate.test.schemafilter.RecordingTarget.Category.SEQUENCE_DROP;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10937")
@RequiresDialectFeature({ SupportSchemaCreation.class })
public class SequenceFilterTest extends BaseUnitTestCase {
    private StandardServiceRegistryImpl serviceRegistry;

    private Metadata metadata;

    @Test
    public void createSchema_unfiltered() {
        RecordingTarget target = doCreation(new DefaultSchemaFilter());
        Assert.assertThat(target.getActions(SEQUENCE_CREATE), containsExactly("entity_1_seq_gen", "entity_2_seq_gen"));
    }

    @Test
    public void createSchema_filtered() {
        RecordingTarget target = doCreation(new SequenceFilterTest.TestSchemaFilter());
        Assert.assertThat(target.getActions(SEQUENCE_CREATE), containsExactly("entity_1_seq_gen"));
    }

    @Test
    public void dropSchema_unfiltered() {
        RecordingTarget target = doDrop(new DefaultSchemaFilter());
        Assert.assertThat(target.getActions(SEQUENCE_DROP), containsExactly("entity_1_seq_gen", "entity_2_seq_gen"));
    }

    @Test
    public void dropSchema_filtered() {
        RecordingTarget target = doDrop(new SequenceFilterTest.TestSchemaFilter());
        Assert.assertThat(target.getActions(SEQUENCE_DROP), containsExactly("entity_1_seq_gen"));
    }

    @Entity
    @SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "entity_1_seq_gen")
    @Table(name = "the_entity_1", schema = "the_schema_1")
    public static class Schema1Entity1 {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    @Entity
    @SequenceGenerator(initialValue = 1, name = "idgen2", sequenceName = "entity_2_seq_gen")
    @Table(name = "the_entity_2", schema = "the_schema_2")
    public static class Schema2Entity2 {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen2")
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    private static class TestSchemaFilter implements SchemaFilter {
        @Override
        public boolean includeNamespace(Namespace namespace) {
            return true;
        }

        @Override
        public boolean includeTable(Table table) {
            return true;
        }

        @Override
        public boolean includeSequence(Sequence sequence) {
            final String render = sequence.getName().render();
            return !("entity_2_seq_gen".endsWith(sequence.getName().render()));
        }
    }
}

