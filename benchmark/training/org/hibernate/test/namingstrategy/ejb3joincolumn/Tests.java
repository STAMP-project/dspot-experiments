/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.namingstrategy.ejb3joincolumn;


import ImplicitNamingStrategyJpaCompliantImpl.INSTANCE;
import java.util.Iterator;
import java.util.Locale;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link org.hibernate.cfg.Ejb3JoinColumn} and {@link org.hibernate.boot.model.naming.PhysicalNamingStrategy}
 * interaction
 *
 * @author Anton Wimmer
 * @author Steve Ebersole
 */
public class Tests extends BaseUnitTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9961")
    public void testJpaJoinColumnPhysicalNaming() {
        final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySettings(Environment.getProperties()).build();
        try {
            final MetadataSources metadataSources = new MetadataSources(ssr);
            metadataSources.addAnnotatedClass(Language.class);
            final MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();
            metadataBuilder.applyImplicitNamingStrategy(INSTANCE);
            metadataBuilder.applyPhysicalNamingStrategy(PhysicalNamingStrategyImpl.INSTANCE);
            final Metadata metadata = metadataBuilder.build();
            validate();
            final PersistentClass languageBinding = metadata.getEntityBinding(Language.class.getName());
            final Property property = languageBinding.getProperty("fallBack");
            Iterator itr = property.getValue().getColumnIterator();
            Assert.assertTrue(itr.hasNext());
            final Column column = ((Column) (itr.next()));
            Assert.assertFalse(itr.hasNext());
            Assert.assertEquals("C_FALLBACK_ID", column.getName().toUpperCase(Locale.ROOT));
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }
}

