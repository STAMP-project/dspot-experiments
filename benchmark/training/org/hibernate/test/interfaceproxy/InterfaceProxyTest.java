/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.interfaceproxy;


import DialectChecks.SupportsExpectedLobUsagePattern;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class InterfaceProxyTest extends BaseCoreFunctionalTestCase {
    @Test
    @RequiresDialectFeature(value = SupportsExpectedLobUsagePattern.class, comment = "database/driver does not support expected LOB usage pattern")
    public void testInterfaceProxies() {
        Session s = openSession(new DocumentInterceptor());
        Transaction t = s.beginTransaction();
        Document d = new DocumentImpl();
        d.setName("Hibernate in Action");
        d.setContent(s.getLobHelper().createBlob("blah blah blah".getBytes()));
        Long did = ((Long) (s.save(d)));
        SecureDocument d2 = new SecureDocumentImpl();
        d2.setName("Secret");
        d2.setContent(s.getLobHelper().createBlob("wxyz wxyz".getBytes()));
        // SybaseASE15Dialect only allows 7-bits in a byte to be inserted into a tinyint
        // column (0 <= val < 128)
        d2.setPermissionBits(((byte) (127)));
        d2.setOwner("gavin");
        Long d2id = ((Long) (s.save(d2)));
        t.commit();
        s.close();
        s = openSession(new DocumentInterceptor());
        t = s.beginTransaction();
        d = ((Document) (s.load(ItemImpl.class, did)));
        Assert.assertEquals(did, d.getId());
        Assert.assertEquals("Hibernate in Action", d.getName());
        Assert.assertNotNull(d.getContent());
        d2 = ((SecureDocument) (s.load(ItemImpl.class, d2id)));
        Assert.assertEquals(d2id, d2.getId());
        Assert.assertEquals("Secret", d2.getName());
        Assert.assertNotNull(d2.getContent());
        s.clear();
        d = ((Document) (s.load(DocumentImpl.class, did)));
        Assert.assertEquals(did, d.getId());
        Assert.assertEquals("Hibernate in Action", d.getName());
        Assert.assertNotNull(d.getContent());
        d2 = ((SecureDocument) (s.load(SecureDocumentImpl.class, d2id)));
        Assert.assertEquals(d2id, d2.getId());
        Assert.assertEquals("Secret", d2.getName());
        Assert.assertNotNull(d2.getContent());
        Assert.assertEquals("gavin", d2.getOwner());
        // s.clear();
        d2 = ((SecureDocument) (s.load(SecureDocumentImpl.class, did)));
        Assert.assertEquals(did, d2.getId());
        Assert.assertEquals("Hibernate in Action", d2.getName());
        Assert.assertNotNull(d2.getContent());
        try {
            d2.getOwner();// CCE

            Assert.assertFalse(true);
        } catch (ClassCastException cce) {
            // correct
        }
        s.createQuery("delete ItemImpl").executeUpdate();
        t.commit();
        s.close();
    }
}

