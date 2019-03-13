/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.lob.hhh4635;


import org.hibernate.Session;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 * To reproduce this issue, Oracle MUST use a multi-byte character set (UTF-8)!
 *
 * @author Brett Meyer
 */
@RequiresDialect(Oracle8iDialect.class)
@TestForIssue(jiraKey = "HHH-4635")
public class LobTest extends BaseCoreFunctionalTestCase {
    @Test
    public void hibernateTest() {
        printConfig();
        Session session = openSession();
        session.beginTransaction();
        LobTestEntity entity = new LobTestEntity();
        entity.setId(1L);
        entity.setLobValue(session.getLobHelper().createBlob(new byte[9999]));
        entity.setQwerty(randomString(4000));
        session.save(entity);
        session.getTransaction().commit();
    }
}

