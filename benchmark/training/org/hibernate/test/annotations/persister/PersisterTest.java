/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.persister;


import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Shawn Clowater
 */
public class PersisterTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testEntityEntityPersisterAndPersisterSpecified() throws Exception {
        // checks to see that the persister specified with the @Persister annotation takes precedence if a @Entity.persister() is also specified
        PersistentClass persistentClass = metadata().getEntityBinding(Deck.class.getName());
        Assert.assertEquals(("Incorrect Persister class for " + (persistentClass.getMappedClass())), EntityPersister.class, persistentClass.getEntityPersisterClass());
    }

    @Test
    public void testEntityEntityPersisterSpecified() throws Exception {
        // tests the persister specified with an @Entity.persister()
        PersistentClass persistentClass = metadata().getEntityBinding(Card.class.getName());
        Assert.assertEquals(("Incorrect Persister class for " + (persistentClass.getMappedClass())), SingleTableEntityPersister.class, persistentClass.getEntityPersisterClass());
    }

    @Test
    public void testCollectionPersisterSpecified() throws Exception {
        // tests the persister specified by the @Persister annotation on a collection
        Collection collection = metadata().getCollectionBinding(((Deck.class.getName()) + ".cards"));
        Assert.assertEquals(("Incorrect Persister class for collection " + (collection.getRole())), CollectionPersister.class, collection.getCollectionPersisterClass());
    }
}

