/**
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 *
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.test.common.rule;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.rule.MvelHelper;


public class MvelHelperTest extends TestCase {
    private static final Log LOG = LogFactory.getLog(MvelHelperTest.class);

    /**
     * Test that a blank rule is true.
     */
    public void testBlankRule() {
        boolean result = MvelHelper.evaluateRule("", null);
        TestCase.assertTrue(result);
    }

    /**
     * Test that a null rule is true.
     */
    public void testNullRule() {
        boolean result = MvelHelper.evaluateRule(null, null);
        TestCase.assertTrue(result);
    }

    /**
     * Test rule with parse errors
     */
    public void testRuleWithParseErrors() {
        MvelHelper.setTestMode(true);
        boolean result = MvelHelper.evaluateRule("BadFunction(xyz)", null);
        MvelHelper.setTestMode(false);
        TestCase.assertFalse(result);
    }

    /**
     * Test rule that evaluates to true
     */
    public void testRuleThatEvaluatesToTrue() {
        // Locale used as an illustrative domain class only.  Any object could have been used.
        Locale testLocale = new LocaleImpl();
        testLocale.setLocaleCode("US");
        Map parameters = new HashMap();
        parameters.put("locale", testLocale);
        boolean result = MvelHelper.evaluateRule("locale.localeCode == 'US'", parameters);
        TestCase.assertTrue(result);
    }

    /**
     * Test rule that evaluates to true
     */
    public void testRuleThatEvaluatesToFalse() {
        // Locale used as an illustrative domain class only.  Any object could have been used.
        Locale testLocale = new LocaleImpl();
        testLocale.setLocaleCode("GB");
        Map parameters = new HashMap();
        parameters.put("locale", testLocale);
        boolean result = MvelHelper.evaluateRule("locale.localeCode == 'US'", parameters);
        TestCase.assertFalse(result);
    }

    /**
     * Confirms repeated success for method overload workaround in SelectizeCollectionUtils
     * </p>
     * See {@link #testMvelMethodOverloadFailureCase()} for a more complete description of the problem case.
     */
    public void testMvelMethodOverloadWorkaroundCase() throws IOException {
        // Test multiple iterations to make sure we no longer fail at all
        for (int j = 0; j < 20; j++) {
            String output = executeExternalJavaProcess(MvelOverloadWorkaroundReproduction.class);
            TestCase.assertEquals("true", output);
        }
    }
}

