/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.core.Is;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Isolated test for various usages of parameters
 *
 * @author Steve Ebersole
 */
public class ParameterTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9154")
    public void testClassAsParameter() {
        Session s = openSession();
        s.beginTransaction();
        s.createQuery("from Human h where h.name = :class").setParameter("class", new Name()).list();
        s.createQuery("from Human where name = :class").setParameter("class", new Name()).list();
        s.createQuery("from Human h where :class = h.name").setParameter("class", new Name()).list();
        s.createQuery("from Human h where :class <> h.name").setParameter("class", new Name()).list();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-7705")
    public void testSetPropertiesMapWithNullValues() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        try {
            Human human = new Human();
            human.setNickName("nick");
            s.save(human);
            Map parameters = new HashMap();
            parameters.put("nickName", null);
            Query q = s.createQuery("from Human h where h.nickName = :nickName or (h.nickName is null and :nickName is null)");
            q.setProperties(parameters);
            Assert.assertThat(q.list().size(), Is.is(0));
            Human human1 = new Human();
            human1.setNickName(null);
            s.save(human1);
            parameters = new HashMap();
            parameters.put("nickName", null);
            q = s.createQuery("from Human h where h.nickName = :nickName or (h.nickName is null and :nickName is null)");
            q.setProperties(parameters);
            Assert.assertThat(q.list().size(), Is.is(1));
            Human found = ((Human) (q.list().get(0)));
            Assert.assertThat(found.getId(), Is.is(human1.getId()));
            parameters = new HashMap();
            parameters.put("nickName", "nick");
            q = s.createQuery("from Human h where h.nickName = :nickName or (h.nickName is null and :nickName is null)");
            q.setProperties(parameters);
            Assert.assertThat(q.list().size(), Is.is(1));
            found = ((Human) (q.list().get(0)));
            Assert.assertThat(found.getId(), Is.is(human.getId()));
            s.delete(human);
            s.delete(human1);
            t.commit();
        } catch (Exception e) {
            if ((session.getTransaction().getStatus()) == (TransactionStatus.ACTIVE)) {
                session.getTransaction().rollback();
            }
            throw e;
        } finally {
            s.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10796")
    public void testSetPropertiesMapNotContainingAllTheParameters() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        try {
            Human human = new Human();
            human.setNickName("nick");
            human.setIntValue(1);
            s.save(human);
            Map parameters = new HashMap();
            parameters.put("nickNames", "nick");
            List<Integer> intValues = new ArrayList<>();
            intValues.add(1);
            Query q = s.createQuery("from Human h where h.nickName in (:nickNames) and h.intValue in (:intValues)");
            q.setParameterList("intValues", intValues);
            q.setProperties(parameters);
            Assert.assertThat(q.list().size(), Is.is(1));
            s.delete(human);
            t.commit();
        } catch (Exception e) {
            if ((session.getTransaction().getStatus()) == (TransactionStatus.ACTIVE)) {
                session.getTransaction().rollback();
            }
            throw e;
        } finally {
            s.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9154")
    public void testObjectAsParameter() {
        Session s = openSession();
        s.beginTransaction();
        s.createQuery("from Human h where h.name = :OBJECT").setParameter("OBJECT", new Name()).list();
        s.createQuery("from Human where name = :OBJECT").setParameter("OBJECT", new Name()).list();
        s.createQuery("from Human h where :OBJECT = h.name").setParameter("OBJECT", new Name()).list();
        s.createQuery("from Human h where :OBJECT <> h.name").setParameter("OBJECT", new Name()).list();
        s.getTransaction().commit();
        s.close();
    }
}

