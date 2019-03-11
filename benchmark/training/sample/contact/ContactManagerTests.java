/**
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.contact;


import BasePermission.ADMINISTRATION;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Tests {@link ContactManager}.
 *
 * @author David Leal
 * @author Ben Alex
 * @author Luke Taylor
 */
@ContextConfiguration(locations = { "/applicationContext-security.xml", "/applicationContext-common-authorization.xml", "/applicationContext-common-business.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class ContactManagerTests {
    // ~ Instance fields
    // ================================================================================================
    @Autowired
    protected ContactManager contactManager;

    @Test
    public void testDianne() {
        makeActiveUser("dianne");// has ROLE_USER

        List<Contact> contacts = contactManager.getAll();
        assertThat(contacts).hasSize(4);
        assertContainsContact(4, contacts);
        assertContainsContact(5, contacts);
        assertContainsContact(6, contacts);
        assertContainsContact(8, contacts);
        assertDoestNotContainContact(1, contacts);
        assertDoestNotContainContact(2, contacts);
        assertDoestNotContainContact(3, contacts);
    }

    @Test
    public void testrod() {
        makeActiveUser("rod");// has ROLE_SUPERVISOR

        List<Contact> contacts = contactManager.getAll();
        assertThat(contacts).hasSize(4);
        assertContainsContact(1, contacts);
        assertContainsContact(2, contacts);
        assertContainsContact(3, contacts);
        assertContainsContact(4, contacts);
        assertDoestNotContainContact(5, contacts);
        Contact c1 = contactManager.getById(new Long(4));
        contactManager.deletePermission(c1, new PrincipalSid("bob"), ADMINISTRATION);
        contactManager.addPermission(c1, new PrincipalSid("bob"), ADMINISTRATION);
    }

    @Test
    public void testScott() {
        makeActiveUser("scott");// has ROLE_USER

        List<Contact> contacts = contactManager.getAll();
        assertThat(contacts).hasSize(5);
        assertContainsContact(4, contacts);
        assertContainsContact(6, contacts);
        assertContainsContact(7, contacts);
        assertContainsContact(8, contacts);
        assertContainsContact(9, contacts);
        assertDoestNotContainContact(1, contacts);
    }
}

