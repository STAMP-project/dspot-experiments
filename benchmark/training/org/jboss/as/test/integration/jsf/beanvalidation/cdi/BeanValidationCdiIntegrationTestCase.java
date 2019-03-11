/**
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.as.test.integration.jsf.beanvalidation.cdi;


import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests for the integration of JSF, CDI, and Bean Validation.
 *
 * @author Farah Juma
 */
@RunWith(Arquillian.class)
@RunAsClient
public class BeanValidationCdiIntegrationTestCase {
    @ArquillianResource
    private URL url;

    private final Pattern viewStatePattern = Pattern.compile("id=\".*javax.faces.ViewState.*\" value=\"([^\"]*)\"");

    private final Pattern nameErrorPattern = Pattern.compile("<div id=\"nameError\">([^<]+)</div>");

    private final Pattern numberErrorPattern = Pattern.compile("<div id=\"numberError\">([^<]+)</div>");

    @Test
    public void testSuccessfulBeanValidation() throws Exception {
        String responseString = registerTeam("Team1", 6);
        Matcher errorMatcher = nameErrorPattern.matcher(responseString);
        Assert.assertTrue((!(errorMatcher.find())));
        errorMatcher = numberErrorPattern.matcher(responseString);
        Assert.assertTrue((!(errorMatcher.find())));
    }

    @Test
    public void testFailingBeanValidation() throws Exception {
        String nameError = null;
        String numberError = null;
        String responseString = registerTeam("", 1);
        Matcher errorMatcher = nameErrorPattern.matcher(responseString);
        if (errorMatcher.find()) {
            nameError = errorMatcher.group(1).trim();
        }
        errorMatcher = numberErrorPattern.matcher(responseString);
        if (errorMatcher.find()) {
            numberError = errorMatcher.group(1).trim();
        }
        Assert.assertEquals("Team name must be at least 3 characters.", nameError);
        Assert.assertEquals("Not enough people for a team.", numberError);
    }
}

