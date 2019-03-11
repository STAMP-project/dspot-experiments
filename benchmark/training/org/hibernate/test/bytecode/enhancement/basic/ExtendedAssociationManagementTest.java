/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.basic;


import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Luis Barreiro
 */
@RunWith(BytecodeEnhancerRunner.class)
public class ExtendedAssociationManagementTest {
    @Test
    public void test() {
        ExtendedAssociationManagementTest.User user = new ExtendedAssociationManagementTest.User();
        user.login = UUID.randomUUID().toString();
        ExtendedAssociationManagementTest.Customer customer = new ExtendedAssociationManagementTest.Customer();
        customer.user = user;
        Assert.assertEquals(customer, getFieldByReflection(user, "customer"));
        // check dirty tracking is set automatically with bi-directional association management
        EnhancerTestUtils.checkDirtyTracking(user, "login", "customer");
        ExtendedAssociationManagementTest.User anotherUser = new ExtendedAssociationManagementTest.User();
        anotherUser.login = UUID.randomUUID().toString();
        customer.user = anotherUser;
        Assert.assertNull(user.customer);
        Assert.assertEquals(customer, getFieldByReflection(anotherUser, "customer"));
        user.customer = new ExtendedAssociationManagementTest.Customer();
        Assert.assertEquals(user, user.customer.user);
    }

    // --- //
    @Entity
    private static class Customer {
        @Id
        Long id;

        String firstName;

        String lastName;

        @OneToOne(fetch = FetchType.LAZY)
        ExtendedAssociationManagementTest.User user;
    }

    @Entity
    private static class User {
        @Id
        Long id;

        String login;

        String password;

        @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
        ExtendedAssociationManagementTest.Customer customer;
    }
}

