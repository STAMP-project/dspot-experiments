/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.lob;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.Random;
import org.hibernate.LobHelper;
import org.hibernate.Session;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Brett Meyer
 */
@TestForIssue(jiraKey = "HHH-7698")
@RequiresDialect(value = H2Dialect.class, jiraKey = "HHH-7724")
public class JpaLargeBlobTest extends BaseCoreFunctionalTestCase {
    @Test
    public void jpaBlobStream() throws Exception {
        Session session = openSession();
        LobEntity o = new LobEntity();
        LobHelper lh = session.getLobHelper();
        JpaLargeBlobTest.LobInputStream lis = new JpaLargeBlobTest.LobInputStream();
        session.getTransaction().begin();
        Blob blob = lh.createBlob(lis, LobEntity.BLOB_LENGTH);
        o.setBlob(blob);
        // Regardless if NON_CONTEXTUAL_LOB_CREATION is set to true,
        // ContextualLobCreator should use a NonContextualLobCreator to create
        // a blob Proxy.  If that's the case, the InputStream will not be read
        // until it's persisted with the JDBC driver.
        // Although HHH-7698 was about high memory consumption, this is the best
        // way to test that the high memory use is being prevented.
        Assert.assertFalse(lis.wasRead());
        session.persist(o);
        session.getTransaction().commit();
        Assert.assertTrue(lis.wasRead());
        session.close();
        lis.close();
    }

    private class LobInputStream extends InputStream {
        private boolean read = false;

        private Long count = (((long) (200)) * 1024) * 1024;

        @Override
        public int read() throws IOException {
            read = true;
            if ((count) > 0) {
                (count)--;
                return new Random().nextInt();
            }
            return -1;
        }

        @Override
        public int available() throws IOException {
            return 1;
        }

        public boolean wasRead() {
            return read;
        }
    }
}

