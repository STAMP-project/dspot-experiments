/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.security.xacml;


import XACMLConstants.DECISION_DENY;
import XACMLConstants.DECISION_NOT_APPLICABLE;
import XACMLConstants.DECISION_PERMIT;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.security.xacml.core.JBossPDP;
import org.jboss.security.xacml.interfaces.PolicyDecisionPoint;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Testcases which test JBossPDP initialization a validates XACML Interoperability Use Cases. .
 *
 * @author Josef Cacek
 */
@RunWith(Arquillian.class)
public class JBossPDPInteroperabilityTestCase {
    private static Logger LOGGER = Logger.getLogger(JBossPDPInteroperabilityTestCase.class);

    @Inject
    JBossPDPServiceBean pdpServiceBean;

    /**
     * Validates the 7 Oasis XACML Interoperability Use Cases.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testInteropTestWithXMLRequests() throws Exception {
        Assert.assertNotNull("PDPServiceBean should be injected.", pdpServiceBean);
        final PolicyDecisionPoint pdp = pdpServiceBean.getJBossPDP();
        Assert.assertNotNull("JBossPDP should be not-null", pdp);
        Assert.assertEquals("Case 1 should be deny", DECISION_DENY, getDecision(pdp, "scenario2-testcase1-request.xml"));
        Assert.assertEquals("Case 2 should be permit", DECISION_PERMIT, getDecision(pdp, "scenario2-testcase2-request.xml"));
        Assert.assertEquals("Case 3 should be permit", DECISION_PERMIT, getDecision(pdp, "scenario2-testcase3-request.xml"));
        Assert.assertEquals("Case 4 should be deny", DECISION_DENY, getDecision(pdp, "scenario2-testcase4-request.xml"));
        Assert.assertEquals("Case 5 should be deny", DECISION_DENY, getDecision(pdp, "scenario2-testcase5-request.xml"));
        Assert.assertEquals("Case 6 should be deny", DECISION_DENY, getDecision(pdp, "scenario2-testcase6-request.xml"));
        Assert.assertEquals("Case 7 should be permit", DECISION_PERMIT, getDecision(pdp, "scenario2-testcase7-request.xml"));
    }

    /**
     * Tests PDP evaluation of XACML requests provided as the objects (Oasis XACML Interoperability Use Cases).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testInteropTestWithObjects() throws Exception {
        Assert.assertNotNull("PDPServiceBean should be injected.", pdpServiceBean);
        final PolicyDecisionPoint pdp = pdpServiceBean.getJBossPDP();
        Assert.assertNotNull("JBossPDP should be not-null", pdp);
        Assert.assertEquals("Case 1 should be deny", DECISION_DENY, getDecision(pdp, getRequestContext("false", "false", 10)));
        Assert.assertEquals("Case 2 should be permit", DECISION_PERMIT, getDecision(pdp, getRequestContext("false", "false", 1)));
        Assert.assertEquals("Case 3 should be permit", DECISION_PERMIT, getDecision(pdp, getRequestContext("true", "false", 5)));
        Assert.assertEquals("Case 4 should be deny", DECISION_DENY, getDecision(pdp, getRequestContext("false", "false", 9)));
        Assert.assertEquals("Case 5 should be deny", DECISION_DENY, getDecision(pdp, getRequestContext("true", "false", 10)));
        Assert.assertEquals("Case 6 should be deny", DECISION_DENY, getDecision(pdp, getRequestContext("true", "false", 15)));
        Assert.assertEquals("Case 7 should be permit", DECISION_PERMIT, getDecision(pdp, getRequestContext("true", "true", 10)));
    }

    /**
     * Tests loading XACML policies from a filesystem folder.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPoliciesLoadedFromDir() throws Exception {
        // create temporary folder for policies
        final File policyDir = new File(("test-JBossPDP-Med-" + (System.currentTimeMillis())));
        final InputStream requestIS = getClass().getResourceAsStream(((XACMLTestUtils.TESTOBJECTS_REQUESTS) + "/med-example-request.xml"));
        try {
            policyDir.mkdirs();
            final JBossPDP pdp = createPDPForMed(policyDir);
            final String requestTemplate = IOUtils.toString(requestIS, StandardCharsets.UTF_8);
            JBossPDPInteroperabilityTestCase.LOGGER.trace(("REQUEST template: " + requestTemplate));
            final Map<String, Object> substitutionMap = new HashMap<String, Object>();
            substitutionMap.put(XACMLTestUtils.SUBST_SUBJECT_ID, "josef@med.example.com");
            Assert.assertEquals("Decision for josef@med.example.com should be DECISION_PERMIT", DECISION_PERMIT, getDecisionForStr(pdp, StrSubstitutor.replace(requestTemplate, substitutionMap)));
            substitutionMap.put(XACMLTestUtils.SUBST_SUBJECT_ID, "guest@med.example.com");
            Assert.assertEquals("Decision for guest@med.example.com should be DECISION_DENY", DECISION_DENY, getDecisionForStr(pdp, StrSubstitutor.replace(requestTemplate, substitutionMap)));
            substitutionMap.put(XACMLTestUtils.SUBST_SUBJECT_ID, "hs@simpsons.com");
            Assert.assertEquals("Decision for hs@simpsons.com should be DECISION_DENY", DECISION_DENY, getDecisionForStr(pdp, StrSubstitutor.replace(requestTemplate, substitutionMap)));
            substitutionMap.put(XACMLTestUtils.SUBST_SUBJECT_ID, "bs@simpsons.com");
            Assert.assertEquals("Decision for bs@simpsons.com should be DECISION_NOT_APPLICABLE", DECISION_NOT_APPLICABLE, getDecisionForStr(pdp, StrSubstitutor.replace(requestTemplate, substitutionMap)));
            substitutionMap.put(XACMLTestUtils.SUBST_SUBJECT_ID, "admin@acme.com");
            Assert.assertEquals("Decision for admin@acme.com should be DECISION_NOT_APPLICABLE", DECISION_NOT_APPLICABLE, getDecisionForStr(pdp, StrSubstitutor.replace(requestTemplate, substitutionMap)));
        } finally {
            FileUtils.deleteDirectory(policyDir);
            requestIS.close();
        }
    }
}

