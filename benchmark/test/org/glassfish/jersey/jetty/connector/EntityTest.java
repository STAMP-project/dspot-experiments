/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.jetty.connector;


import MediaType.APPLICATION_JSON_TYPE;
import MediaType.APPLICATION_XML_TYPE;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the Http content negotiation.
 *
 * @author Arul Dhesiaseelan (aruld at acm.org)
 */
public class EntityTest extends JerseyTest {
    private static final Logger LOGGER = Logger.getLogger(EntityTest.class.getName());

    private static final String PATH = "test";

    @Path("/test")
    public static class EntityResource {
        @GET
        public EntityTest.Person get() {
            return new EntityTest.Person("John", "Doe");
        }

        @POST
        public EntityTest.Person post(EntityTest.Person entity) {
            return entity;
        }
    }

    @XmlRootElement
    public static class Person {
        private String firstName;

        private String lastName;

        public Person() {
        }

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        @Override
        public String toString() {
            return ((firstName) + " ") + (lastName);
        }
    }

    @Test
    public void testGet() {
        Response response = target(EntityTest.PATH).request(APPLICATION_XML_TYPE).get();
        EntityTest.Person person = response.readEntity(EntityTest.Person.class);
        Assert.assertEquals("John Doe", person.toString());
        response = target(EntityTest.PATH).request(APPLICATION_JSON_TYPE).get();
        person = response.readEntity(EntityTest.Person.class);
        Assert.assertEquals("John Doe", person.toString());
    }

    @Test
    public void testGetAsync() throws InterruptedException, ExecutionException {
        Response response = target(EntityTest.PATH).request(APPLICATION_XML_TYPE).async().get().get();
        EntityTest.Person person = response.readEntity(EntityTest.Person.class);
        Assert.assertEquals("John Doe", person.toString());
        response = target(EntityTest.PATH).request(APPLICATION_JSON_TYPE).async().get().get();
        person = response.readEntity(EntityTest.Person.class);
        Assert.assertEquals("John Doe", person.toString());
    }

    @Test
    public void testPost() {
        Response response = target(EntityTest.PATH).request(APPLICATION_XML_TYPE).post(Entity.xml(new EntityTest.Person("John", "Doe")));
        EntityTest.Person person = response.readEntity(EntityTest.Person.class);
        Assert.assertEquals("John Doe", person.toString());
        response = target(EntityTest.PATH).request(APPLICATION_JSON_TYPE).post(Entity.xml(new EntityTest.Person("John", "Doe")));
        person = response.readEntity(EntityTest.Person.class);
        Assert.assertEquals("John Doe", person.toString());
    }

    @Test
    public void testPostAsync() throws InterruptedException, ExecutionException, TimeoutException {
        Response response = target(EntityTest.PATH).request(APPLICATION_XML_TYPE).async().post(Entity.xml(new EntityTest.Person("John", "Doe"))).get();
        EntityTest.Person person = response.readEntity(EntityTest.Person.class);
        Assert.assertEquals("John Doe", person.toString());
        response = target(EntityTest.PATH).request(APPLICATION_JSON_TYPE).async().post(Entity.xml(new EntityTest.Person("John", "Doe"))).get();
        person = response.readEntity(EntityTest.Person.class);
        Assert.assertEquals("John Doe", person.toString());
    }
}

