/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.loadplans.walking;


import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.walking.spi.MetamodelGraphWalker;
import org.hibernate.test.onetoone.formula.Address;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class KeyManyToOneWalkingTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testWalkingKeyManyToOneGraphs() {
        // Address has a composite id with a bi-directional key-many to Person
        final EntityPersister ep = ((EntityPersister) (sessionFactory().getClassMetadata(Address.class)));
        MetamodelGraphWalker.visitEntity(new LoggingAssociationVisitationStrategy(), ep);
    }
}

