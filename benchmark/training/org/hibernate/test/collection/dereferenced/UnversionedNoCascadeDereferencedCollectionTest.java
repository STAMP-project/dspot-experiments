/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.collection.dereferenced;


import java.util.HashSet;
import org.hibernate.Session;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class UnversionedNoCascadeDereferencedCollectionTest extends AbstractDereferencedCollectionTest {
    @Test
    @TestForIssue(jiraKey = "HHH-9777")
    public void testMergeNullCollection() {
        Session s = openSession();
        s.getTransaction().begin();
        UnversionedNoCascadeOne one = new UnversionedNoCascadeOne();
        Assert.assertNull(one.getManies());
        s.save(one);
        Assert.assertNull(one.getManies());
        EntityEntry eeOne = getEntityEntry(s, one);
        Assert.assertNull(eeOne.getLoadedValue("manies"));
        s.flush();
        Assert.assertNull(one.getManies());
        Assert.assertNull(eeOne.getLoadedValue("manies"));
        s.getTransaction().commit();
        s.close();
        final String role = (UnversionedNoCascadeOne.class.getName()) + ".manies";
        s = openSession();
        s.getTransaction().begin();
        one = ((UnversionedNoCascadeOne) (s.merge(one)));
        // after merging, one.getManies() should still be null;
        // the EntityEntry loaded state should contain a PersistentCollection though.
        Assert.assertNull(one.getManies());
        eeOne = getEntityEntry(s, one);
        AbstractPersistentCollection maniesEEOneStateOrig = ((AbstractPersistentCollection) (eeOne.getLoadedValue("manies")));
        Assert.assertNotNull(maniesEEOneStateOrig);
        // Ensure maniesEEOneStateOrig has role, key, and session properly defined (even though one.manies == null)
        Assert.assertEquals(role, maniesEEOneStateOrig.getRole());
        Assert.assertEquals(one.getId(), maniesEEOneStateOrig.getKey());
        Assert.assertSame(s, maniesEEOneStateOrig.getSession());
        // Ensure there is a CollectionEntry for maniesEEOneStateOrig and that the role, persister, and key are set properly.
        CollectionEntry ceManiesOrig = getCollectionEntry(s, maniesEEOneStateOrig);
        Assert.assertNotNull(ceManiesOrig);
        Assert.assertEquals(role, ceManiesOrig.getRole());
        Assert.assertSame(sessionFactory().getCollectionPersister(role), ceManiesOrig.getLoadedPersister());
        Assert.assertEquals(one.getId(), ceManiesOrig.getKey());
        s.flush();
        // Ensure the same EntityEntry is being used.
        Assert.assertSame(eeOne, getEntityEntry(s, one));
        // Ensure one.getManies() is still null.
        Assert.assertNull(one.getManies());
        // Ensure CollectionEntry for maniesEEOneStateOrig is no longer in the PersistenceContext.
        Assert.assertNull(getCollectionEntry(s, maniesEEOneStateOrig));
        // Ensure the original CollectionEntry has role, persister, and key set to null.
        Assert.assertNull(ceManiesOrig.getRole());
        Assert.assertNull(ceManiesOrig.getLoadedPersister());
        Assert.assertNull(ceManiesOrig.getKey());
        // Ensure the PersistentCollection (that was previously returned by eeOne.getLoadedState())
        // has key and role set to null.
        Assert.assertNull(maniesEEOneStateOrig.getKey());
        Assert.assertNull(maniesEEOneStateOrig.getRole());
        // Ensure eeOne.getLoadedState() returns null for collection after flush.
        Assert.assertNull(eeOne.getLoadedValue("manies"));
        // Ensure the session in maniesEEOneStateOrig has been unset.
        Assert.assertNull(maniesEEOneStateOrig.getSession());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9777")
    public void testGetAndNullifyCollection() {
        Session s = openSession();
        s.getTransaction().begin();
        UnversionedNoCascadeOne one = new UnversionedNoCascadeOne();
        Assert.assertNull(one.getManies());
        s.save(one);
        Assert.assertNull(one.getManies());
        EntityEntry eeOne = getEntityEntry(s, one);
        Assert.assertNull(eeOne.getLoadedValue("manies"));
        s.flush();
        Assert.assertNull(one.getManies());
        Assert.assertNull(eeOne.getLoadedValue("manies"));
        s.getTransaction().commit();
        s.close();
        final String role = (UnversionedNoCascadeOne.class.getName()) + ".manies";
        s = openSession();
        s.getTransaction().begin();
        one = ((UnversionedNoCascadeOne) (s.get(UnversionedNoCascadeOne.class, one.getId())));
        // When returned by Session.get(), one.getManies() will return a PersistentCollection;
        // the EntityEntry loaded state should contain the same PersistentCollection.
        eeOne = getEntityEntry(s, one);
        Assert.assertNotNull(one.getManies());
        AbstractPersistentCollection maniesEEOneStateOrig = ((AbstractPersistentCollection) (eeOne.getLoadedValue("manies")));
        Assert.assertSame(one.getManies(), maniesEEOneStateOrig);
        // Ensure maniesEEOneStateOrig has role, key, and session properly defined (even though one.manies == null)
        Assert.assertEquals(role, maniesEEOneStateOrig.getRole());
        Assert.assertEquals(one.getId(), maniesEEOneStateOrig.getKey());
        Assert.assertSame(s, maniesEEOneStateOrig.getSession());
        // Ensure there is a CollectionEntry for maniesEEOneStateOrig and that the role, persister, and key are set properly.
        CollectionEntry ceManies = getCollectionEntry(s, maniesEEOneStateOrig);
        Assert.assertNotNull(ceManies);
        Assert.assertEquals(role, ceManies.getRole());
        Assert.assertSame(sessionFactory().getCollectionPersister(role), ceManies.getLoadedPersister());
        Assert.assertEquals(one.getId(), ceManies.getKey());
        // nullify collection
        one.setManies(null);
        s.flush();
        // Ensure the same EntityEntry is being used.
        Assert.assertSame(eeOne, getEntityEntry(s, one));
        // Ensure one.getManies() is still null.
        Assert.assertNull(one.getManies());
        // Ensure CollectionEntry for maniesEEOneStateOrig is no longer in the PersistenceContext.
        Assert.assertNull(getCollectionEntry(s, maniesEEOneStateOrig));
        // Ensure the original CollectionEntry has role, persister, and key set to null.
        Assert.assertNull(ceManies.getRole());
        Assert.assertNull(ceManies.getLoadedPersister());
        Assert.assertNull(ceManies.getKey());
        // Ensure the PersistentCollection (that was previously returned by eeOne.getLoadedState())
        // has key and role set to null.
        Assert.assertNull(maniesEEOneStateOrig.getKey());
        Assert.assertNull(maniesEEOneStateOrig.getRole());
        // Ensure eeOne.getLoadedState() returns null for collection after flush.
        Assert.assertNull(eeOne.getLoadedValue("manies"));
        // Ensure the session in maniesEEOneStateOrig has been unset.
        Assert.assertNull(maniesEEOneStateOrig.getSession());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9777")
    public void testGetAndReplaceCollection() {
        Session s = openSession();
        s.getTransaction().begin();
        UnversionedNoCascadeOne one = new UnversionedNoCascadeOne();
        Assert.assertNull(one.getManies());
        s.save(one);
        Assert.assertNull(one.getManies());
        EntityEntry eeOne = getEntityEntry(s, one);
        Assert.assertNull(eeOne.getLoadedValue("manies"));
        s.flush();
        Assert.assertNull(one.getManies());
        Assert.assertNull(eeOne.getLoadedValue("manies"));
        s.getTransaction().commit();
        s.close();
        final String role = (UnversionedNoCascadeOne.class.getName()) + ".manies";
        s = openSession();
        s.getTransaction().begin();
        one = ((UnversionedNoCascadeOne) (s.get(UnversionedNoCascadeOne.class, one.getId())));
        // When returned by Session.get(), one.getManies() will return a PersistentCollection;
        // the EntityEntry loaded state should contain the same PersistentCollection.
        eeOne = getEntityEntry(s, one);
        Assert.assertNotNull(one.getManies());
        AbstractPersistentCollection maniesEEOneStateOrig = ((AbstractPersistentCollection) (eeOne.getLoadedValue("manies")));
        Assert.assertSame(one.getManies(), maniesEEOneStateOrig);
        // Ensure maniesEEOneStateOrig has role, key, and session properly defined (even though one.manies == null)
        Assert.assertEquals(role, maniesEEOneStateOrig.getRole());
        Assert.assertEquals(one.getId(), maniesEEOneStateOrig.getKey());
        Assert.assertSame(s, maniesEEOneStateOrig.getSession());
        // Ensure there is a CollectionEntry for maniesEEOneStateOrig and that the role, persister, and key are set properly.
        CollectionEntry ceManiesOrig = getCollectionEntry(s, maniesEEOneStateOrig);
        Assert.assertNotNull(ceManiesOrig);
        Assert.assertEquals(role, ceManiesOrig.getRole());
        Assert.assertSame(sessionFactory().getCollectionPersister(role), ceManiesOrig.getLoadedPersister());
        Assert.assertEquals(one.getId(), ceManiesOrig.getKey());
        // replace collection
        one.setManies(new HashSet<Many>());
        s.flush();
        // Ensure the same EntityEntry is being used.
        Assert.assertSame(eeOne, getEntityEntry(s, one));
        // Ensure CollectionEntry for maniesEEOneStateOrig is no longer in the PersistenceContext.
        Assert.assertNull(getCollectionEntry(s, maniesEEOneStateOrig));
        // Ensure the original CollectionEntry has role, persister, and key set to null.
        Assert.assertNull(ceManiesOrig.getRole());
        Assert.assertNull(ceManiesOrig.getLoadedPersister());
        Assert.assertNull(ceManiesOrig.getKey());
        // Ensure the PersistentCollection (that was previously returned by eeOne.getLoadedState())
        // has key and role set to null.
        Assert.assertNull(maniesEEOneStateOrig.getKey());
        Assert.assertNull(maniesEEOneStateOrig.getRole());
        // one.getManies() should be "wrapped" by a PersistentCollection now; role, key, and session should be set properly.
        Assert.assertTrue(PersistentCollection.class.isInstance(one.getManies()));
        Assert.assertEquals(role, getRole());
        Assert.assertEquals(one.getId(), getKey());
        Assert.assertSame(s, getSession());
        // Ensure eeOne.getLoadedState() contains the new collection.
        Assert.assertSame(one.getManies(), eeOne.getLoadedValue("manies"));
        // Ensure there is a new CollectionEntry for the new collection and that role, persister, and key are set properly.
        CollectionEntry ceManiesAfterReplace = getCollectionEntry(s, ((PersistentCollection) (one.getManies())));
        Assert.assertNotNull(ceManiesAfterReplace);
        Assert.assertEquals(role, ceManiesAfterReplace.getRole());
        Assert.assertSame(sessionFactory().getCollectionPersister(role), ceManiesAfterReplace.getLoadedPersister());
        Assert.assertEquals(one.getId(), ceManiesAfterReplace.getKey());
        // Ensure the session in maniesEEOneStateOrig has been unset.
        Assert.assertNull(maniesEEOneStateOrig.getSession());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testSaveOrUpdateNullCollection() {
        Session s = openSession();
        s.getTransaction().begin();
        UnversionedNoCascadeOne one = new UnversionedNoCascadeOne();
        Assert.assertNull(one.getManies());
        s.save(one);
        Assert.assertNull(one.getManies());
        EntityEntry eeOne = getEntityEntry(s, one);
        Assert.assertNull(eeOne.getLoadedValue("manies"));
        s.flush();
        Assert.assertNull(one.getManies());
        Assert.assertNull(eeOne.getLoadedValue("manies"));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        s.saveOrUpdate(one);
        // Ensure one.getManies() is still null.
        Assert.assertNull(one.getManies());
        // Ensure the EntityEntry loaded state contains null for the manies collection.
        eeOne = getEntityEntry(s, one);
        Assert.assertNull(eeOne.getLoadedValue("manies"));
        s.flush();
        // Ensure one.getManies() is still null.
        Assert.assertNull(one.getManies());
        // Ensure the same EntityEntry is being used.
        Assert.assertSame(eeOne, getEntityEntry(s, one));
        // Ensure the EntityEntry loaded state still contains null for the manies collection.
        Assert.assertNull(eeOne.getLoadedValue("manies"));
        s.getTransaction().commit();
        s.close();
    }
}

