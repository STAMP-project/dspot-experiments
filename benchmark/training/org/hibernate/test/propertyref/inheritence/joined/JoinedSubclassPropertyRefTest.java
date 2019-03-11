/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.propertyref.inheritence.joined;


import FetchMode.JOIN;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class JoinedSubclassPropertyRefTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testPropertyRefToJoinedSubclass() {
        Session session = openSession();
        Transaction tx = session.beginTransaction();
        Person p = new Person();
        p.setName("Gavin King");
        BankAccount acc = new BankAccount();
        acc.setBsb("0634");
        acc.setType('B');
        acc.setAccountNumber("xxx-123-abc");
        p.setBankAccount(acc);
        session.persist(p);
        tx.commit();
        session.close();
        session = openSession();
        tx = session.beginTransaction();
        p = ((Person) (session.get(Person.class, p.getId())));
        Assert.assertNotNull(p.getBankAccount());
        Assert.assertTrue(Hibernate.isInitialized(p.getBankAccount()));
        tx.commit();
        session.close();
        session = openSession();
        tx = session.beginTransaction();
        p = ((Person) (session.createCriteria(Person.class).setFetchMode("bankAccount", JOIN).uniqueResult()));
        Assert.assertNotNull(p.getBankAccount());
        Assert.assertTrue(Hibernate.isInitialized(p.getBankAccount()));
        tx.commit();
        session.close();
        session = openSession();
        tx = session.beginTransaction();
        session.delete(p);
        tx.commit();
        session.close();
    }
}

