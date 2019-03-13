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
package org.drools.compiler.rule.builder.dialect;


import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;


public class JavaAndMVELCombinedTest extends CommonTestMethodBase {
    private static final String FN1 = "mveljavarules.drl";

    private static final String FN2 = "mvelonly.drl";

    private static final String FN3 = "javaonly.drl";

    @Test
    public void testMixed() {
        timing(JavaAndMVELCombinedTest.FN1, "mveljava: ");
    }

    @Test
    public void testMVEL() {
        timing(JavaAndMVELCombinedTest.FN2, "    mvel: ");
    }

    @Test
    public void testJAVA() {
        timing(JavaAndMVELCombinedTest.FN3, "    java: ");
    }
}

