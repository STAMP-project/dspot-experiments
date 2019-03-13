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
 * Copyright (c) 2012 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.analyzer;


import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;


/**
 *
 *
 * @author Jeremy Long
 */
public class AbstractFileTypeAnalyzerTest extends BaseTest {
    /**
     * Test of newHashSet method, of class AbstractAnalyzer.
     */
    @Test
    public void testNewHashSet() {
        Set<String> result = AbstractFileTypeAnalyzer.newHashSet("one", "two");
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains("one"));
        Assert.assertTrue(result.contains("two"));
    }
}

