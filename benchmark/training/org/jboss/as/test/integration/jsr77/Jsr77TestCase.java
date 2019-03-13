/**
 * Copyright 2017 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.test.integration.jsr77;


import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.management.j2ee.Management;
import javax.management.j2ee.ManagementHome;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Smoke test for JSR-77 management bean availability
 *
 * @author jcibik@redhat.com
 */
@RunWith(Arquillian.class)
public class Jsr77TestCase {
    /**
     * Test for simple MBean functionality
     *
     * Catches NamingException if a naming exception is encountered in lookup() method.
     * Catches CreateException Indicates a failure to create the EJB object in create() method.
     * Catches RemoteException A communication exception occurred during the execution of a remote method call CreateException in create(), getDefaultDomain() or getMBeanCount() method.
     */
    @Test
    public void testJSR77Availabilty() {
        try {
            Context ic = new InitialContext();
            Object obj = ic.lookup("ejb/mgmt/MEJB");
            ManagementHome mejbHome = ((ManagementHome) (obj));
            final Management management = mejbHome.create();
            Assert.assertNotNull(management.getDefaultDomain());
            Assert.assertTrue(((management.getMBeanCount()) > 0));
        } catch (NamingException | CreateException | RemoteException ne) {
            Assert.fail(getMessage());
        }
    }
}

