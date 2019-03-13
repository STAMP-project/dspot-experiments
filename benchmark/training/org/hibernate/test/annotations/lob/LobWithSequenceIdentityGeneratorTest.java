/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.lob;


import org.hibernate.Session;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@TestForIssue(jiraKey = "HHH-8103")
@RequiresDialect(Oracle8iDialect.class)
public class LobWithSequenceIdentityGeneratorTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testLobInsertUpdateDeleteSelect() {
        Session session = openSession();
        // Insert
        session.getTransaction().begin();
        Document document = new Document(1, "HHH-8103", "Oracle expects all LOB properties to be last in INSERT and UPDATE statements.");
        session.persist(document);
        session.getTransaction().commit();
        session.clear();
        session.getTransaction().begin();
        Assert.assertEquals(document, session.get(Document.class, document.getId()));
        session.getTransaction().commit();
        session.clear();
        // Update
        session.getTransaction().begin();
        document = ((Document) (session.get(Document.class, document.getId())));
        document.setFullText("Correct!");
        session.update(document);
        session.getTransaction().commit();
        session.clear();
        session.getTransaction().begin();
        Assert.assertEquals(document, session.get(Document.class, document.getId()));
        session.getTransaction().commit();
        session.clear();
        // Delete
        session.getTransaction().begin();
        document = ((Document) (session.get(Document.class, document.getId())));
        session.delete(document);
        session.getTransaction().commit();
        session.clear();
        session.getTransaction().begin();
        Assert.assertNull(session.get(Document.class, document.getId()));
        session.getTransaction().commit();
        session.close();
    }
}

