/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.stateless;


import ScrollMode.FORWARD_ONLY;
import java.util.Date;
import org.hibernate.ScrollableResults;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class StatelessSessionTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testCreateUpdateReadDelete() {
        StatelessSession ss = sessionFactory().openStatelessSession();
        Transaction tx = ss.beginTransaction();
        Document doc = new Document("blah blah blah", "Blahs");
        ss.insert(doc);
        Assert.assertNotNull(doc.getName());
        Date initVersion = doc.getLastModified();
        Assert.assertNotNull(initVersion);
        tx.commit();
        tx = ss.beginTransaction();
        doc.setText("blah blah blah .... blah");
        ss.update(doc);
        Assert.assertNotNull(doc.getLastModified());
        Assert.assertNotSame(doc.getLastModified(), initVersion);
        tx.commit();
        tx = ss.beginTransaction();
        doc.setText("blah blah blah .... blah blay");
        ss.update(doc);
        tx.commit();
        Document doc2 = ((Document) (ss.get(Document.class.getName(), "Blahs")));
        Assert.assertEquals("Blahs", doc2.getName());
        Assert.assertEquals(doc.getText(), doc2.getText());
        doc2 = ((Document) (ss.createQuery("from Document where text is not null").uniqueResult()));
        Assert.assertEquals("Blahs", doc2.getName());
        Assert.assertEquals(doc.getText(), doc2.getText());
        ScrollableResults sr = ss.createQuery("from Document where text is not null").scroll(FORWARD_ONLY);
        sr.next();
        doc2 = ((Document) (sr.get(0)));
        sr.close();
        Assert.assertEquals("Blahs", doc2.getName());
        Assert.assertEquals(doc.getText(), doc2.getText());
        doc2 = ((Document) (ss.createSQLQuery("select * from Document").addEntity(Document.class).uniqueResult()));
        Assert.assertEquals("Blahs", doc2.getName());
        Assert.assertEquals(doc.getText(), doc2.getText());
        doc2 = ((Document) (ss.createCriteria(Document.class).uniqueResult()));
        Assert.assertEquals("Blahs", doc2.getName());
        Assert.assertEquals(doc.getText(), doc2.getText());
        sr = ss.createCriteria(Document.class).scroll(FORWARD_ONLY);
        sr.next();
        doc2 = ((Document) (sr.get(0)));
        sr.close();
        Assert.assertEquals("Blahs", doc2.getName());
        Assert.assertEquals(doc.getText(), doc2.getText());
        tx = ss.beginTransaction();
        ss.delete(doc);
        tx.commit();
        ss.close();
    }

    @Test
    public void testHqlBulk() {
        StatelessSession ss = sessionFactory().openStatelessSession();
        Transaction tx = ss.beginTransaction();
        Document doc = new Document("blah blah blah", "Blahs");
        ss.insert(doc);
        Paper paper = new Paper();
        paper.setColor("White");
        ss.insert(paper);
        tx.commit();
        tx = ss.beginTransaction();
        int count = ss.createQuery("update Document set name = :newName where name = :oldName").setString("newName", "Foos").setString("oldName", "Blahs").executeUpdate();
        Assert.assertEquals("hql-update on stateless session", 1, count);
        count = ss.createQuery("update Paper set color = :newColor").setString("newColor", "Goldenrod").executeUpdate();
        Assert.assertEquals("hql-update on stateless session", 1, count);
        tx.commit();
        tx = ss.beginTransaction();
        count = ss.createQuery("delete Document").executeUpdate();
        Assert.assertEquals("hql-delete on stateless session", 1, count);
        count = ss.createQuery("delete Paper").executeUpdate();
        Assert.assertEquals("hql-delete on stateless session", 1, count);
        tx.commit();
        ss.close();
    }

    @Test
    public void testInitId() {
        StatelessSession ss = sessionFactory().openStatelessSession();
        Transaction tx = ss.beginTransaction();
        Paper paper = new Paper();
        paper.setColor("White");
        ss.insert(paper);
        Assert.assertNotNull(paper.getId());
        tx.commit();
        tx = ss.beginTransaction();
        ss.delete(ss.get(Paper.class, paper.getId()));
        tx.commit();
        ss.close();
    }

    @Test
    public void testRefresh() {
        StatelessSession ss = sessionFactory().openStatelessSession();
        Transaction tx = ss.beginTransaction();
        Paper paper = new Paper();
        paper.setColor("whtie");
        ss.insert(paper);
        tx.commit();
        ss.close();
        ss = sessionFactory().openStatelessSession();
        tx = ss.beginTransaction();
        Paper p2 = ((Paper) (ss.get(Paper.class, paper.getId())));
        p2.setColor("White");
        ss.update(p2);
        tx.commit();
        ss.close();
        ss = sessionFactory().openStatelessSession();
        tx = ss.beginTransaction();
        Assert.assertEquals("whtie", paper.getColor());
        ss.refresh(paper);
        Assert.assertEquals("White", paper.getColor());
        ss.delete(paper);
        tx.commit();
        ss.close();
    }
}

