/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.idgen.identity.hhh10429;


import DialectChecks.SupportsIdentityColumns;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.mapping.KeyValue;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthew Morrissette
 */
@TestForIssue(jiraKey = "HHH-10429")
@RequiresDialectFeature(SupportsIdentityColumns.class)
public class IdentityGeneratorExtendsTest extends BaseCoreFunctionalTestCase {
    @Entity(name = "EntityBean")
    @Table
    public static class EntityBean {
        @Id
        @Column
        @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "customGenerator")
        @GenericGenerator(name = "customGenerator", strategy = "org.hibernate.test.idgen.identity.hhh10429.CustomIdentityGenerator")
        private int entityId;

        public int getEntityId() {
            return entityId;
        }

        public void setEntityId(int entityId) {
            this.entityId = entityId;
        }

        public String description;
    }

    @Test
    public void testIdentifierGeneratorExtendsIdentityGenerator() {
        final MetadataSources sources = new MetadataSources(serviceRegistry());
        sources.addAnnotatedClass(IdentityGeneratorExtendsTest.EntityBean.class);
        final MetadataBuilder builder = sources.getMetadataBuilder();
        final Metadata metadata = builder.build();
        for (final Namespace ns : metadata.getDatabase().getNamespaces()) {
            for (final org.hibernate.mapping.Table table : ns.getTables()) {
                final KeyValue value = table.getIdentifierValue();
                Assert.assertNotNull("IdentifierValue was null", value);
                Assert.assertTrue(value.isIdentityColumn(metadata.getIdentifierGeneratorFactory(), getDialect()));
            }
        }
        Session s = openSession();
        s.beginTransaction();
        s.save(new IdentityGeneratorExtendsTest.EntityBean());
        s.getTransaction().commit();
        s.close();
    }
}

