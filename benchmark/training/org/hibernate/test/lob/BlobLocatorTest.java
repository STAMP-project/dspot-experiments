/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.lob;


import DialectChecks.SupportsExpectedLobUsagePattern;
import DialectChecks.SupportsUnboundedLobLocatorMaterializationCheck;
import LockOptions.UPGRADE;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.dialect.TeradataDialect;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests lazy materialization of data mapped by
 * {@link org.hibernate.type.BlobType}, as well as bounded and unbounded
 * materialization and mutation.
 *
 * @author Steve Ebersole
 */
@RequiresDialectFeature(SupportsExpectedLobUsagePattern.class)
public class BlobLocatorTest extends BaseCoreFunctionalTestCase {
    private static final long BLOB_SIZE = 10000L;

    @Test
    @SkipForDialect(value = TeradataDialect.class, jiraKey = "HHH-6637", comment = "Teradata requires locator to be used in same session where it was created/retrieved")
    public void testBoundedBlobLocatorAccess() throws Throwable {
        byte[] original = BlobLocatorTest.buildByteArray(BlobLocatorTest.BLOB_SIZE, true);
        byte[] changed = BlobLocatorTest.buildByteArray(BlobLocatorTest.BLOB_SIZE, false);
        byte[] empty = new byte[]{  };
        Session s = openSession();
        s.beginTransaction();
        LobHolder entity = new LobHolder();
        entity.setBlobLocator(s.getLobHelper().createBlob(original));
        s.save(entity);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = s.get(LobHolder.class, entity.getId());
        Assert.assertEquals(BlobLocatorTest.BLOB_SIZE, entity.getBlobLocator().length());
        BlobLocatorTest.assertEquals(original, BlobLocatorTest.extractData(entity.getBlobLocator()));
        s.getTransaction().commit();
        s.close();
        // test mutation via setting the new clob data...
        if (getDialect().supportsLobValueChangePropogation()) {
            s = openSession();
            s.beginTransaction();
            entity = ((LobHolder) (s.byId(LobHolder.class).with(UPGRADE).load(entity.getId())));
            entity.getBlobLocator().truncate(1);
            entity.getBlobLocator().setBytes(1, changed);
            s.getTransaction().commit();
            s.close();
            s = openSession();
            s.beginTransaction();
            entity = ((LobHolder) (s.byId(LobHolder.class).with(UPGRADE).load(entity.getId())));
            Assert.assertNotNull(entity.getBlobLocator());
            Assert.assertEquals(BlobLocatorTest.BLOB_SIZE, entity.getBlobLocator().length());
            BlobLocatorTest.assertEquals(changed, BlobLocatorTest.extractData(entity.getBlobLocator()));
            entity.getBlobLocator().truncate(1);
            entity.getBlobLocator().setBytes(1, original);
            s.getTransaction().commit();
            s.close();
        }
        // test mutation via supplying a new clob locator instance...
        s = openSession();
        s.beginTransaction();
        entity = ((LobHolder) (s.byId(LobHolder.class).with(UPGRADE).load(entity.getId())));
        Assert.assertNotNull(entity.getBlobLocator());
        Assert.assertEquals(BlobLocatorTest.BLOB_SIZE, entity.getBlobLocator().length());
        BlobLocatorTest.assertEquals(original, BlobLocatorTest.extractData(entity.getBlobLocator()));
        entity.setBlobLocator(s.getLobHelper().createBlob(changed));
        s.getTransaction().commit();
        s.close();
        // test empty blob
        s = openSession();
        s.beginTransaction();
        entity = s.get(LobHolder.class, entity.getId());
        Assert.assertEquals(BlobLocatorTest.BLOB_SIZE, entity.getBlobLocator().length());
        BlobLocatorTest.assertEquals(changed, BlobLocatorTest.extractData(entity.getBlobLocator()));
        entity.setBlobLocator(s.getLobHelper().createBlob(empty));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = s.get(LobHolder.class, entity.getId());
        if ((entity.getBlobLocator()) != null) {
            Assert.assertEquals(empty.length, entity.getBlobLocator().length());
            BlobLocatorTest.assertEquals(empty, BlobLocatorTest.extractData(entity.getBlobLocator()));
        }
        s.delete(entity);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @RequiresDialectFeature(value = SupportsUnboundedLobLocatorMaterializationCheck.class, comment = "database/driver does not support materializing a LOB locator outside the owning transaction")
    public void testUnboundedBlobLocatorAccess() throws Throwable {
        // Note: unbounded mutation of the underlying lob data is completely
        // unsupported; most databases would not allow such a construct anyway.
        // Thus here we are only testing materialization...
        byte[] original = BlobLocatorTest.buildByteArray(BlobLocatorTest.BLOB_SIZE, true);
        Session s = openSession();
        s.beginTransaction();
        LobHolder entity = new LobHolder();
        entity.setBlobLocator(Hibernate.getLobCreator(s).createBlob(original));
        s.save(entity);
        s.getTransaction().commit();
        s.close();
        // load the entity with the clob locator, and close the session/transaction;
        // at that point it is unbounded...
        s = openSession();
        s.beginTransaction();
        entity = s.get(LobHolder.class, entity.getId());
        s.getTransaction().commit();
        s.close();
        Assert.assertEquals(BlobLocatorTest.BLOB_SIZE, entity.getBlobLocator().length());
        BlobLocatorTest.assertEquals(original, BlobLocatorTest.extractData(entity.getBlobLocator()));
        s = openSession();
        s.beginTransaction();
        s.delete(entity);
        s.getTransaction().commit();
        s.close();
    }
}

