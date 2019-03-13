/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.collectionelement.indexedCollection;


import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class IndexedCollectionOfElementsTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testIndexedCollectionOfElements() throws Exception {
        Sale sale = new Sale();
        Contact contact = new Contact();
        contact.setName("Emmanuel");
        sale.getContacts().add(contact);
        Session s = openSession();
        s.getTransaction().begin();
        s.save(sale);
        s.flush();
        s.get(Sale.class, sale.getId());
        Assert.assertEquals(1, sale.getContacts().size());
        s.getTransaction().rollback();
        s.close();
    }
}

