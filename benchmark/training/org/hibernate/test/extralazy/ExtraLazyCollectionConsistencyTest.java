/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.extralazy;


import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class ExtraLazyCollectionConsistencyTest extends BaseCoreFunctionalTestCase {
    private User user;

    @Test
    @TestForIssue(jiraKey = "HHH-9933")
    public void testSetSize() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            User _user = session.get(.class, user.getName());
            Document document = new Document("Les Miserables", "sad", _user);
            assertEquals(1, _user.getDocuments().size());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9933")
    public void testSetIterator() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            User _user = session.get(.class, user.getName());
            Document document = new Document("Les Miserables", "sad", _user);
            assertTrue(_user.getDocuments().iterator().hasNext());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9933")
    public void testSetIsEmpty() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            User _user = session.get(.class, user.getName());
            Document document = new Document("Les Miserables", "sad", _user);
            assertFalse(_user.getDocuments().isEmpty());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9933")
    public void testSetContains() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            User _user = session.get(.class, user.getName());
            Document document = new Document("Les Miserables", "sad", _user);
            assertTrue(_user.getDocuments().contains(document));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9933")
    public void testSetAdd() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            User _user = session.get(.class, user.getName());
            Document document = new Document();
            document.setTitle("Les Miserables");
            document.setContent("sad");
            document.setOwner(_user);
            assertTrue("not added", _user.getDocuments().add(document));
            assertFalse("added", _user.getDocuments().add(document));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9933")
    public void testSetRemove() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            User _user = session.get(.class, user.getName());
            Document document = new Document("Les Miserables", "sad", _user);
            assertTrue("not removed", _user.getDocuments().remove(document));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9933")
    public void testSetToArray() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            User _user = session.get(.class, user.getName());
            Document document = new Document("Les Miserables", "sad", _user);
            assertEquals(1, _user.getDocuments().toArray().length);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9933")
    public void testSetToArrayTyped() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            User _user = session.get(.class, user.getName());
            Document document = new Document("Les Miserables", "sad", _user);
            assertEquals(1, _user.getDocuments().toArray(new Document[0]).length);
        });
    }
}

