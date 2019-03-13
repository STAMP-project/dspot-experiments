/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.jpa.hibernate.envers;


import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test @AuditJoinTable over Uni-directional One-to-Many Relationship
 *
 * @author Madhumita Sadhukhan
 */
@RunWith(Arquillian.class)
public class AuditJoinTableoverOnetoManyJoinColumnTest {
    private static final String ARCHIVE_NAME = "jpa_AuditMappedByoverOnetoManyJoinColumnTest";

    @ArquillianResource
    private static InitialContext iniCtx;

    @Test
    public void testRevisionsfromAuditJoinTable() throws Exception {
        SLSBAudit slsbAudit = AuditJoinTableoverOnetoManyJoinColumnTest.lookup("SLSBAudit", SLSBAudit.class);
        Customer c1 = slsbAudit.createCustomer("MADHUMITA", "SADHUKHAN", "WORK", "+420", "543789654");
        Phone p1 = c1.getPhones().get(1);
        p1.setType("Emergency");
        slsbAudit.updatePhone(p1);
        c1.setSurname("Mondal");
        slsbAudit.updateCustomer(c1);
        c1.setFirstname("Steve");
        c1.setSurname("Jobs");
        slsbAudit.updateCustomer(c1);
        // delete phone
        c1.getPhones().remove(p1);
        slsbAudit.updateCustomer(c1);
        slsbAudit.deletePhone(p1);
        Assert.assertEquals(1, c1.getPhones().size());
        testRevisionDatafromAuditJoinTable(c1, slsbAudit);
        testRevisionTypefromAuditJoinTable(c1, slsbAudit);
        testOtherFieldslikeForeignKeysfromAuditJoinTable(c1, slsbAudit);
    }
}

