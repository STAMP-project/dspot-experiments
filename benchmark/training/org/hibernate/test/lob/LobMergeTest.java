/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.lob;


import DialectChecks.SupportsExpectedLobUsagePattern;
import DialectChecks.SupportsLobValueChangePropogation;
import java.util.Arrays;
import org.hibernate.Session;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
// Skip for Sybase. HHH-6807
@TestForIssue(jiraKey = "HHH-2680")
@RequiresDialectFeature({ SupportsExpectedLobUsagePattern.class, SupportsLobValueChangePropogation.class })
public class LobMergeTest extends BaseCoreFunctionalTestCase {
    private static final int LOB_SIZE = 10000;

    @Test
    public void testMergingBlobData() throws Exception {
        final byte[] original = BlobLocatorTest.buildByteArray(LobMergeTest.LOB_SIZE, true);
        final byte[] updated = BlobLocatorTest.buildByteArray(LobMergeTest.LOB_SIZE, false);
        Session s = openSession();
        s.beginTransaction();
        LobHolder entity = new LobHolder();
        entity.setBlobLocator(s.getLobHelper().createBlob(original));
        s.save(entity);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        // entity still detached...
        entity.setBlobLocator(s.getLobHelper().createBlob(updated));
        entity = ((LobHolder) (s.merge(entity)));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = ((LobHolder) (s.get(LobHolder.class, entity.getId())));
        Assert.assertEquals("blob sizes did not match after merge", LobMergeTest.LOB_SIZE, entity.getBlobLocator().length());
        Assert.assertTrue("blob contents did not match after merge", Arrays.equals(updated, BlobLocatorTest.extractData(entity.getBlobLocator())));
        s.delete(entity);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testMergingClobData() throws Exception {
        final String original = ClobLocatorTest.buildString(LobMergeTest.LOB_SIZE, 'a');
        final String updated = ClobLocatorTest.buildString(LobMergeTest.LOB_SIZE, 'z');
        Session s = openSession();
        s.beginTransaction();
        LobHolder entity = new LobHolder();
        entity.setClobLocator(s.getLobHelper().createClob(original));
        s.save(entity);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        // entity still detached...
        entity.setClobLocator(s.getLobHelper().createClob(updated));
        entity = ((LobHolder) (s.merge(entity)));
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = ((LobHolder) (s.get(LobHolder.class, entity.getId())));
        Assert.assertEquals("clob sizes did not match after merge", LobMergeTest.LOB_SIZE, entity.getClobLocator().length());
        Assert.assertEquals("clob contents did not match after merge", updated, ClobLocatorTest.extractData(entity.getClobLocator()));
        s.delete(entity);
        s.getTransaction().commit();
        s.close();
    }
}

