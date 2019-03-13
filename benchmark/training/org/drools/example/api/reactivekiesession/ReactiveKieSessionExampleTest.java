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
package org.drools.example.api.reactivekiesession;


import KieServices.Factory;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;


public class ReactiveKieSessionExampleTest {
    @Test
    public void testGo() {
        KieServices ks = Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieSession ksession = kContainer.newKieSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains("car"));
        Assert.assertTrue(list.contains("ball"));
        list.clear();
        ksession.insert("Debbie");
        ksession.fireAllRules();
        ksession.fireAllRules();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains("doll"));
    }
}

