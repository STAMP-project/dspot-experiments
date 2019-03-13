/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.mappedsuperclass.intermediate;


import java.math.BigDecimal;
import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class IntermediateMappedSuperclassTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testGetOnIntermediateMappedSuperclass() {
        final BigDecimal withdrawalLimit = new BigDecimal(1000.0).setScale(2);
        Session session = openSession();
        session.beginTransaction();
        SavingsAccount savingsAccount = new SavingsAccount("123", withdrawalLimit);
        session.save(savingsAccount);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        Account account = ((Account) (session.get(Account.class, savingsAccount.getId())));
        // Oracle returns the BigDecimal with scale=0, which is equal to 1000 (not 1000.00);
        // compare using BigDecimal.doubleValue;
        Assert.assertEquals(withdrawalLimit.doubleValue(), ((SavingsAccount) (account)).getWithdrawalLimit().doubleValue(), 0.001);
        session.delete(account);
        session.getTransaction().commit();
        session.close();
    }
}

