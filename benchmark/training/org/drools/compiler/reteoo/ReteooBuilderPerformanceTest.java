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
package org.drools.compiler.reteoo;


import org.drools.compiler.compiler.DroolsParserException;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Created by IntelliJ IDEA. User: SG0521861 Date: Mar 20, 2008 Time: 2:36:47 PM To change this template use File |
 * Settings | File Templates.
 */
@Ignore
public class ReteooBuilderPerformanceTest {
    private static final int RULE_COUNT = Integer.parseInt(System.getProperty("rule.count", "1000"));

    private static final int RETEBUILDER_COUNT = Integer.parseInt(System.getProperty("retebuilder.count", "1"));

    @Test
    public void testReteBuilder() throws DroolsParserException {
        ReteooBuilderPerformanceTest.addRules(ReteooBuilderPerformanceTest.generatePackage(ReteooBuilderPerformanceTest.RULE_COUNT));
    }

    private static final int MILLIS_IN_SECOND = 1000;

    private static final int MILLIS_IN_MINUTE = (ReteooBuilderPerformanceTest.MILLIS_IN_SECOND) * 60;

    private static final int MILLIS_IN_HOUR = (ReteooBuilderPerformanceTest.MILLIS_IN_MINUTE) * 60;
}

