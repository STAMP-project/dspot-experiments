/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.decisiontable;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;


public class XlsFormulaTest {
    @Test
    public void testFormulaValue() throws Exception {
        // DROOLS-643
        Resource dt = ResourceFactory.newClassPathResource("/data/XlsFormula.xls", getClass());
        KieSession ksession = getKieSession(dt);
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.insert(new Person("michael", "stilton", 1));
        ksession.fireAllRules();
        Assert.assertEquals("10", list.get(0));// 10

        ksession.insert(new Person("michael", "stilton", 2));
        ksession.fireAllRules();
        Assert.assertEquals("11", list.get(1));// =ROW()

        ksession.insert(new Person("michael", "stilton", 3));
        ksession.fireAllRules();
        Assert.assertEquals("21", list.get(2));// =SUM(D10:D11)

    }
}

