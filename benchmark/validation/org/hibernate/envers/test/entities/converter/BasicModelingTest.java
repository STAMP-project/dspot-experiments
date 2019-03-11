/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.entities.converter;


import AvailableSettings.HBM2DDL_AUTO;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.envers.test.AbstractEnversTest;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class BasicModelingTest extends AbstractEnversTest {
    @Test
    @TestForIssue(jiraKey = "HHH-9042")
    public void testMetamodelBuilding() {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySetting(HBM2DDL_AUTO, "create-drop").build();
        try {
            Metadata metadata = addAnnotatedClass(Person.class).getMetadataBuilder().applyAttributeConverter(SexConverter.class).build();
            validate();
            PersistentClass personBinding = metadata.getEntityBinding(Person.class.getName());
            Assert.assertNotNull(personBinding);
            PersistentClass personAuditBinding = metadata.getEntityBinding(((Person.class.getName()) + "_AUD"));
            Assert.assertNotNull(personAuditBinding);
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }
}

