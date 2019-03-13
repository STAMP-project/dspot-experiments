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
package org.drools.example.api.multiplekbases;


import KieServices.Factory;
import java.util.Arrays;
import java.util.List;
import org.drools.core.time.impl.JDKTimerService;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;


public class MultipleKbasesExampleTest {
    @Test
    public void testSimpleKieBase() {
        List<Integer> list = useKieSession("ksession1");
        // no packages imported means import everything
        Assert.assertEquals(4, list.size());
        Assert.assertTrue(list.containsAll(Arrays.asList(0, 1, 2, 3)));
    }

    @Test
    public void testKieBaseWithPackage() {
        List<Integer> list = useKieSession("ksession2");
        // import package org.some.pkg
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.containsAll(Arrays.asList(1)));
    }

    @Test
    public void testKieBaseWithInclusion() {
        List<Integer> list = useKieSession("ksession3");
        // include ksession2 + import package org.some.pkg2
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.containsAll(Arrays.asList(1, 2)));
    }

    @Test
    public void testKieBaseWith2Packages() {
        List<Integer> list = useKieSession("ksession4");
        // import package org.some.pkg, org.other.pkg
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.containsAll(Arrays.asList(1, 3)));
    }

    @Test
    public void testKieBaseWithPackageAndTransitiveInclusion() {
        List<Integer> list = useKieSession("ksession5");
        // import package org.*
        Assert.assertEquals(3, list.size());
        Assert.assertTrue(list.containsAll(Arrays.asList(1, 2, 3)));
    }

    @Test
    public void testKieBaseWithAllPackages() {
        List<Integer> list = useKieSession("ksession6");
        // import package org.some.*
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.containsAll(Arrays.asList(1, 2)));
    }

    @Test
    public void testEditSessionModel() {
        String name = "ksession6";
        ClockTypeOption pseudoClock = ClockTypeOption.get("pseudo");
        KieServices ks = Factory.get();
        KieContainer kContainer = ks.newKieClasspathContainer();
        KieSessionModel kieSessionModel = kContainer.getKieSessionModel(name);
        ClockTypeOption clockType = kieSessionModel.getClockType();
        // clockType realtime
        Assert.assertNotEquals(clockType, pseudoClock);
        // change model to pseudo
        kieSessionModel.setClockType(pseudoClock);
        Assert.assertEquals(kieSessionModel.getClockType(), pseudoClock);
        // new pseudo session
        KieSession kSession = kContainer.newKieSession(name);
        Assert.assertEquals(kSession.getSessionClock().getClass(), PseudoClockScheduler.class);
    }

    @Test
    public void testEditSessionModelAfterFirstCreatedKieSession() {
        String name = "ksession6";
        ClockTypeOption pseudoClock = ClockTypeOption.get("pseudo");
        KieServices ks = Factory.get();
        KieContainer kContainer = ks.newKieClasspathContainer();
        KieSessionModel kieSessionModel = kContainer.getKieSessionModel(name);
        ClockTypeOption clockType = kieSessionModel.getClockType();
        // clockType realtime
        Assert.assertEquals(clockType, ClockTypeOption.get("realtime"));
        Assert.assertNotEquals(clockType, pseudoClock);
        // session is realtime
        KieSession kSession = kContainer.newKieSession(name);
        Assert.assertEquals(kSession.getSessionClock().getClass(), JDKTimerService.class);
        // change model to pseudo
        kieSessionModel.setClockType(pseudoClock);
        // new session still realtime
        KieSession kSessionPseudo = kContainer.newKieSession(name);
        Assert.assertEquals(kSessionPseudo.getSessionClock().getClass(), PseudoClockScheduler.class);
    }
}

