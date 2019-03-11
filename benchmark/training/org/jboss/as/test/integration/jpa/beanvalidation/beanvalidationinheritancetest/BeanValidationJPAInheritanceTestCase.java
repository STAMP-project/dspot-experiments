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
package org.jboss.as.test.integration.jpa.beanvalidation.beanvalidationinheritancetest;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Locale;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.jpa.beanvalidation.SLSBInheritance;
import org.jboss.as.test.integration.jpa.beanvalidation.SoccerPlayer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test bean validation is propagated on inherited attributes
 *
 * @author Madhumita Sadhukhan
 */
@RunWith(Arquillian.class)
public class BeanValidationJPAInheritanceTestCase {
    private static final String ARCHIVE_NAME = "jpa_TestBeanValidationJPAInheritance";

    @ArquillianResource
    private static InitialContext iniCtx;

    /* Ensure that bean validation works for inheritance across persistent objects */
    @Test
    public void testConstraintValidationforJPA() throws SQLException, NamingException {
        SLSBInheritance slsb = BeanValidationJPAInheritanceTestCase.lookup("SLSBInheritance", SLSBInheritance.class);
        try {
            SoccerPlayer socplayer = slsb.createSoccerPlayer("LEONARDO", "", "SOCCER", "REAL MADRID");
            socplayer.setFirstName("Christiano");
            socplayer.setLastName("");
            socplayer.setGame("FOOTBALL");
            socplayer = slsb.updateSoccerPlayer(socplayer);
        } catch (Exception e) {
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            String stacktrace = w.toString();
            if (Locale.getDefault().getLanguage().equals("en")) {
                Assert.assertTrue(stacktrace.contains("interpolatedMessage='may not be empty', propertyPath=lastName, rootBeanClass=class org.jboss.as.test.integration.jpa.beanvalidation.SoccerPlayer"));
            } else {
                Assert.assertTrue(stacktrace.contains("propertyPath=lastName, rootBeanClass=class org.jboss.as.test.integration.jpa.beanvalidation.SoccerPlayer"));
            }
        }
    }
}

