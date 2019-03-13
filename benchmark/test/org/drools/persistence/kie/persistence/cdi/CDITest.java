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
package org.drools.persistence.kie.persistence.cdi;


import KieServices.Factory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;


@RunWith(Parameterized.class)
public class CDITest {
    private Map<String, Object> context;

    private Environment env;

    private boolean locking;

    public CDITest(String locking) {
        this.locking = DroolsPersistenceUtil.PESSIMISTIC_LOCKING.equals(locking);
    }

    @Test
    public void testCDI() {
        // DROOLS-34
        Weld w = new Weld();
        WeldContainer wc = w.initialize();
        CDITest.CDIBean bean = wc.instance().select(CDITest.CDIBean.class).get();
        bean.test(env);
        w.shutdown();
    }

    public static class CDIBean {
        @Inject
        @KBase("cdiexample")
        KieBase kBase;

        public void test(Environment env) {
            KieSession ksession = Factory.get().getStoreServices().newKieSession(kBase, null, env);
            List<?> list = new ArrayList<Object>();
            ksession.setGlobal("list", list);
            ksession.insert(1);
            ksession.insert(2);
            ksession.insert(3);
            ksession.fireAllRules();
            Assert.assertEquals(3, list.size());
        }
    }
}

