/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.onetoone.basic;


import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.mapping.Table;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class OneToOneSchemaTest extends BaseUnitTestCase {
    @Test
    public void testUniqueKeyNotGeneratedViaAnnotations() throws Exception {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        try {
            Metadata metadata = addAnnotatedClass(Child.class).buildMetadata();
            Table childTable = metadata.getDatabase().getDefaultNamespace().locateTable(Identifier.toIdentifier("CHILD"));
            Assert.assertFalse("UniqueKey was generated when it should not", childTable.getUniqueKeyIterator().hasNext());
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }
}

