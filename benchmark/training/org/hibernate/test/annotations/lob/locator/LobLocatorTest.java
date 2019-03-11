/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.lob.locator;


import DialectChecks.UsesInputStreamToInsertBlob;
import java.sql.SQLException;
import org.hibernate.Session;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
public class LobLocatorTest extends BaseCoreFunctionalTestCase {
    /**
     * Specific JDBC drivers (e.g. SQL Server) may not automatically rewind bound input stream
     * during statement execution. Such behavior results in error message similar to:
     * {@literal The stream value is not the specified length. The specified length was 4, the actual length is 0.}
     */
    @Test
    @TestForIssue(jiraKey = "HHH-8193")
    @RequiresDialectFeature(UsesInputStreamToInsertBlob.class)
    public void testStreamResetBeforeParameterBinding() throws SQLException {
        final Session session = openSession();
        session.getTransaction().begin();
        LobHolder entity = new LobHolder(session.getLobHelper().createBlob("blob".getBytes()), session.getLobHelper().createClob("clob"), 0);
        session.persist(entity);
        session.getTransaction().commit();
        final Integer updatesLimit = 3;
        for (int i = 1; i <= updatesLimit; ++i) {
            session.getTransaction().begin();
            entity = ((LobHolder) (session.get(LobHolder.class, entity.getId())));
            entity.setCounter(i);
            entity = ((LobHolder) (session.merge(entity)));
            session.getTransaction().commit();
        }
        session.getTransaction().begin();
        entity = ((LobHolder) (session.get(LobHolder.class, entity.getId())));
        entity.setBlobLocator(session.getLobHelper().createBlob("updated blob".getBytes()));
        entity.setClobLocator(session.getLobHelper().createClob("updated clob"));
        entity = ((LobHolder) (session.merge(entity)));
        session.getTransaction().commit();
        session.clear();
        session.getTransaction().begin();
        checkState("updated blob".getBytes(), "updated clob", updatesLimit, ((LobHolder) (session.get(LobHolder.class, entity.getId()))));
        session.getTransaction().commit();
        session.close();
    }
}

