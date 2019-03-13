/**
 * This file is part of dependency-check-core.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) 2013 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.xml.suppression;


import java.io.File;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;


/**
 * Test of the suppression parser.
 *
 * @author Jeremy Long
 */
public class SuppressionParserTest extends BaseTest {
    /**
     * Test of parseSuppressionRules method, of class SuppressionParser for the v1.0 suppressions XML Schema.
     */
    @Test
    public void testParseSuppressionRulesV1dot0() throws Exception {
        // File file = new File(this.getClass().getClassLoader().getResource("suppressions.xml").getPath());
        File file = BaseTest.getResourceAsFile(this, "suppressions.xml");
        SuppressionParser instance = new SuppressionParser();
        List<SuppressionRule> result = instance.parseSuppressionRules(file);
        Assert.assertEquals(5, result.size());
    }

    /**
     * Test of parseSuppressionRules method, of class SuppressionParser for the v1.1 suppressions XML Schema.
     */
    @Test
    public void testParseSuppressionRulesV1dot1() throws Exception {
        // File file = new File(this.getClass().getClassLoader().getResource("suppressions.xml").getPath());
        File file = BaseTest.getResourceAsFile(this, "suppressions_1_1.xml");
        SuppressionParser instance = new SuppressionParser();
        List<SuppressionRule> result = instance.parseSuppressionRules(file);
        Assert.assertEquals(5, result.size());
    }

    /**
     * Test of parseSuppressionRules method, of class SuppressionParser for the v1.2 suppressions XML Schema.
     */
    @Test
    public void testParseSuppressionRulesV1dot2() throws Exception {
        // File file = new File(this.getClass().getClassLoader().getResource("suppressions.xml").getPath());
        File file = BaseTest.getResourceAsFile(this, "suppressions_1_2.xml");
        SuppressionParser instance = new SuppressionParser();
        List<SuppressionRule> result = instance.parseSuppressionRules(file);
        Assert.assertEquals(4, result.size());
    }
}

