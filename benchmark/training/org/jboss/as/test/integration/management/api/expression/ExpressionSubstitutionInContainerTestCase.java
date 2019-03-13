/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.management.api.expression;


import javax.ejb.EJB;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Validation of the system property substitution for expressions handling. Test for AS7-6120.
 * Global parameters testing could be found in domain module: ExpressionSupportSmokeTestCase
 * <p>
 * The expression substitution test runs the evaluation of expressions in bean deployed in container.
 * The managementClient injected by arquillian is taken via remote interface.
 *
 * @author <a href="ochaloup@jboss.com">Ondrej Chaloupka</a>
 */
@RunWith(Arquillian.class)
public class ExpressionSubstitutionInContainerTestCase {
    private static final Logger log = Logger.getLogger(ExpressionSubstitutionInContainerTestCase.class);

    private static final String ARCHIVE_NAME = "expression-substitution-test";

    private static final String PROP_NAME = "qa.test.property";

    private static final String PROP_DEFAULT_VALUE = "defaultValue";

    private static final String EXPRESSION_PROP_NAME = "qa.test.exp";

    private static final String EXPRESSION_PROP_VALUE = "expression.value";

    private static final String INNER_PROP_NAME = "qa.test.inner.property";

    private static final String INNER_PROP_DEFAULT_VALUE = "inner.value";

    @EJB
    private StatelessBean bean;

    @ArquillianResource
    private ManagementClient managementClient;

    /**
     * <system-properties>
     * <property name="qa.test.exp" value="expression.value"/>
     * <property name="qa.test.property" value="${qa.test.exp:defaultValue}"/>
     * </system-properties>
     */
    @Test
    @InSequence(1)
    public void testPropertyDefinedFirst() {
        Utils.setProperty(ExpressionSubstitutionInContainerTestCase.EXPRESSION_PROP_NAME, ExpressionSubstitutionInContainerTestCase.EXPRESSION_PROP_VALUE, managementClient.getControllerClient());
        Utils.setProperty(ExpressionSubstitutionInContainerTestCase.PROP_NAME, (((("${" + (ExpressionSubstitutionInContainerTestCase.EXPRESSION_PROP_NAME)) + ":") + (ExpressionSubstitutionInContainerTestCase.PROP_DEFAULT_VALUE)) + "}"), managementClient.getControllerClient());
        try {
            expresionEvaluation();
        } finally {
            // removing tested properties
            Utils.removeProperty(ExpressionSubstitutionInContainerTestCase.EXPRESSION_PROP_NAME, managementClient.getControllerClient());
            Utils.removeProperty(ExpressionSubstitutionInContainerTestCase.PROP_NAME, managementClient.getControllerClient());
        }
    }

    /* <system-properties>
       <property name="qa.test.property" value="${qa.test.exp:defaultValue}"/>
       <property name="qa.test.exp" value="expression.value"/>
    </system-properties>
     */
    /* @Ignore("AS7-6431")
    @Test
    @InSequence(2)
    public void testExpressionDefinedFirst() {
    Utils.setProperty(PROP_NAME, "${" + EXPRESSION_PROP_NAME + ":" + PROP_DEFAULT_VALUE + "}", managementClient.getControllerClient());
    Utils.setProperty(EXPRESSION_PROP_NAME, EXPRESSION_PROP_VALUE, managementClient.getControllerClient());
    try {
    expresionEvaluation();
    } finally {
    // removing tested properties
    Utils.removeProperty(EXPRESSION_PROP_NAME, managementClient.getControllerClient());
    Utils.removeProperty(PROP_NAME, managementClient.getControllerClient());
    }
    }
     */
    /**
     * <system-properties>
     * <property name="qa.test.property" value="${qa.test.exp:defaultValue}"/>
     * </system-properties>
     */
    @Test
    @InSequence(3)
    public void testSystemPropertyEvaluation() {
        // the system property has to be defined in the same VM as the container resides
        bean.addSystemProperty(ExpressionSubstitutionInContainerTestCase.EXPRESSION_PROP_NAME, ExpressionSubstitutionInContainerTestCase.EXPRESSION_PROP_VALUE);
        Utils.setProperty(ExpressionSubstitutionInContainerTestCase.PROP_NAME, (((("${" + (ExpressionSubstitutionInContainerTestCase.EXPRESSION_PROP_NAME)) + ":") + (ExpressionSubstitutionInContainerTestCase.PROP_DEFAULT_VALUE)) + "}"), managementClient.getControllerClient());
        try {
            systemPropertyEvaluation();
        } finally {
            // removing tested properties
            Utils.removeProperty(ExpressionSubstitutionInContainerTestCase.PROP_NAME, managementClient.getControllerClient());
        }
    }

    /**
     * <system-properties>
     * <property name="qa.test.property" value="${qa.test.exp:defaultValue}"/>
     * </system-properties>
     */
    @Test
    @InSequence(4)
    public void testSystemPropertyEvaluationSetAfterExpression() {
        Utils.setProperty(ExpressionSubstitutionInContainerTestCase.PROP_NAME, (((("${" + (ExpressionSubstitutionInContainerTestCase.EXPRESSION_PROP_NAME)) + ":") + (ExpressionSubstitutionInContainerTestCase.PROP_DEFAULT_VALUE)) + "}"), managementClient.getControllerClient());
        // the system property has to be defined in the same VM as the container resides
        bean.addSystemProperty(ExpressionSubstitutionInContainerTestCase.EXPRESSION_PROP_NAME, ExpressionSubstitutionInContainerTestCase.EXPRESSION_PROP_VALUE);
        try {
            systemPropertyEvaluation();
        } finally {
            // removing tested properties
            Utils.removeProperty(ExpressionSubstitutionInContainerTestCase.PROP_NAME, managementClient.getControllerClient());
        }
    }

    /**
     * <system-properties>
     * <property name="qa.test.exp" value="expression.value"/>
     * <property name="qa.test.inner.property" value="${qa.test.exp:inner.value}"/>
     * <property name="qa.test.property" value="${qa.test.inner.property:defaultValue}"/>
     * </system-properties>
     */
    @Test
    @InSequence(5)
    public void testMultipleLevelExpression() {
        Utils.setProperty(ExpressionSubstitutionInContainerTestCase.EXPRESSION_PROP_NAME, ExpressionSubstitutionInContainerTestCase.EXPRESSION_PROP_VALUE, managementClient.getControllerClient());
        Utils.setProperty(ExpressionSubstitutionInContainerTestCase.INNER_PROP_NAME, (((("${" + (ExpressionSubstitutionInContainerTestCase.EXPRESSION_PROP_NAME)) + ":") + (ExpressionSubstitutionInContainerTestCase.INNER_PROP_DEFAULT_VALUE)) + "}"), managementClient.getControllerClient());
        Utils.setProperty(ExpressionSubstitutionInContainerTestCase.PROP_NAME, (((("${" + (ExpressionSubstitutionInContainerTestCase.INNER_PROP_NAME)) + ":") + (ExpressionSubstitutionInContainerTestCase.PROP_DEFAULT_VALUE)) + "}"), managementClient.getControllerClient());
        try {
            // evaluation the inner prop name in addition
            String result = bean.getJBossProperty(ExpressionSubstitutionInContainerTestCase.INNER_PROP_NAME);
            ExpressionSubstitutionInContainerTestCase.log.infof("expressionEvaluation: JBoss property %s was resolved to %s", ExpressionSubstitutionInContainerTestCase.INNER_PROP_NAME, result);
            Assert.assertEquals((("jboss property " + (ExpressionSubstitutionInContainerTestCase.INNER_PROP_NAME)) + " substitution evaluation expected"), ExpressionSubstitutionInContainerTestCase.EXPRESSION_PROP_VALUE, result);
            result = bean.getSystemProperty(ExpressionSubstitutionInContainerTestCase.INNER_PROP_NAME);
            ExpressionSubstitutionInContainerTestCase.log.infof("expressionEvaluation: System property %s has value %s", ExpressionSubstitutionInContainerTestCase.INNER_PROP_NAME, result);
            Assert.assertEquals((("system property " + (ExpressionSubstitutionInContainerTestCase.INNER_PROP_NAME)) + " from substitued jboss property"), ExpressionSubstitutionInContainerTestCase.EXPRESSION_PROP_VALUE, result);
            // then evaluation of the rest
            expresionEvaluation();
        } finally {
            // removing tested properties
            Utils.removeProperty(ExpressionSubstitutionInContainerTestCase.EXPRESSION_PROP_NAME, managementClient.getControllerClient());
            Utils.removeProperty(ExpressionSubstitutionInContainerTestCase.PROP_NAME, managementClient.getControllerClient());
            Utils.removeProperty(ExpressionSubstitutionInContainerTestCase.INNER_PROP_NAME, managementClient.getControllerClient());
        }
    }
}

