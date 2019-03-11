/**
 * This file is part of dependency-check-cofre.
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
 * Copyright (c) 2018 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.analyzer;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseDBTestCase;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.dependency.Dependency;


public class RetireJsAnalyzerFilters extends BaseDBTestCase {
    private RetireJsAnalyzer analyzer;

    private Engine engine;

    /**
     * Test of filters method.
     *
     * @throws Exception
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testFilters() throws Exception {
        // removed by filter (see setup above)
        File file = BaseTest.getResourceAsFile(this, "javascript/jquery-1.6.2.js");
        Dependency dependency = new Dependency(file);
        engine.addDependency(dependency);
        Assert.assertEquals(1, engine.getDependencies().length);
        analyzer.analyze(dependency, engine);
        Assert.assertEquals(0, engine.getDependencies().length);
        // remove non-vulnerable
        file = BaseTest.getResourceAsFile(this, "javascript/custom.js");
        dependency = new Dependency(file);
        engine.addDependency(dependency);
        Assert.assertEquals(1, engine.getDependencies().length);
        analyzer.analyze(dependency, engine);
        Assert.assertEquals(0, engine.getDependencies().length);
        // kept because it is does not match the filter and is vulnerable
        file = BaseTest.getResourceAsFile(this, "javascript/ember.js");
        dependency = new Dependency(file);
        engine.addDependency(dependency);
        Assert.assertEquals(1, engine.getDependencies().length);
        analyzer.analyze(dependency, engine);
        Assert.assertEquals(0, engine.getDependencies().length);
    }
}

