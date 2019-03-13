/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.lang;


import org.drools.core.base.evaluators.EvaluatorRegistry;
import org.junit.Assert;
import org.junit.Test;


public class DroolsSoftKeywordsTest {
    /**
     * Test method for {@link org.kie.lang.DroolsSoftKeywords#isOperator(java.lang.String, boolean)}.
     */
    @Test
    public void testIsOperator() {
        // initializes the registry
        new EvaluatorRegistry();
        // test the registry
        Assert.assertTrue(DroolsSoftKeywords.isOperator("matches", false));
        Assert.assertTrue(DroolsSoftKeywords.isOperator("matches", true));
        Assert.assertTrue(DroolsSoftKeywords.isOperator("contains", false));
        Assert.assertTrue(DroolsSoftKeywords.isOperator("contains", true));
        Assert.assertTrue(DroolsSoftKeywords.isOperator("after", false));
        Assert.assertTrue(DroolsSoftKeywords.isOperator("after", true));
        Assert.assertTrue(DroolsSoftKeywords.isOperator("before", false));
        Assert.assertTrue(DroolsSoftKeywords.isOperator("before", true));
        Assert.assertTrue(DroolsSoftKeywords.isOperator("finishes", false));
        Assert.assertTrue(DroolsSoftKeywords.isOperator("finishes", true));
        Assert.assertTrue(DroolsSoftKeywords.isOperator("overlappedby", false));
        Assert.assertTrue(DroolsSoftKeywords.isOperator("overlappedby", true));
        Assert.assertFalse(DroolsSoftKeywords.isOperator("xyz", false));
        Assert.assertFalse(DroolsSoftKeywords.isOperator("xyz", true));
    }
}

