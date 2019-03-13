/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.cdi;


import KieServices.Factory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.drools.cdi.kproject.AbstractKnowledgeTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.cdi.KReleaseId;
import org.kie.api.cdi.KSession;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.StatelessKieSession;


@RunWith(CDITestRunner.class)
public class StatelessKieSessionInjectionTest {
    public static AbstractKnowledgeTest helper;

    @Inject
    @KSession("jar1.KSession1")
    @KReleaseId(groupId = "jar1", artifactId = "art1", version = "1.0")
    private StatelessKieSession kbase1ksession1v10;

    @Inject
    @KSession("jar1.KSession1")
    @KReleaseId(groupId = "jar1", artifactId = "art1", version = "1.1")
    private StatelessKieSession kbase1ksession1v11;

    @Inject
    @KSession(value = "jar1.KSession1", name = "sks1")
    @KReleaseId(groupId = "jar1", artifactId = "art1", version = "1.0")
    private StatelessKieSession kbase1ksession1sks1;

    @Inject
    @KSession(value = "jar1.KSession1", name = "sks2")
    @KReleaseId(groupId = "jar1", artifactId = "art1", version = "1.0")
    private StatelessKieSession kbase1ksession1sks2;

    @Inject
    @KSession(value = "jar1.KSession1", name = "sks2")
    @KReleaseId(groupId = "jar1", artifactId = "art1", version = "1.0")
    private StatelessKieSession kbase1ksession1sks22;

    @Test
    public void testDynamicStatelessKieSessionReleaseId() throws IOException, ClassNotFoundException, InterruptedException {
        Assert.assertNotNull(kbase1ksession1v10);
        Assert.assertNotNull(kbase1ksession1v10);
        KieCommands cmds = Factory.get().getCommands();
        List<String> list = new ArrayList<String>();
        kbase1ksession1v10.setGlobal("list", list);
        kbase1ksession1v10.execute(cmds.newFireAllRules());
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.get(0).endsWith("1.0"));
        Assert.assertTrue(list.get(1).endsWith("1.0"));
        list = new ArrayList<String>();
        kbase1ksession1v11.setGlobal("list", list);
        kbase1ksession1v11.execute(cmds.newFireAllRules());
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.get(0).endsWith("1.1"));
        Assert.assertTrue(list.get(1).endsWith("1.1"));
    }

    @Test
    public void testNamedStatelessKieSessions() throws IOException, ClassNotFoundException, InterruptedException {
        Assert.assertNotNull(kbase1ksession1sks1);
        Assert.assertNotNull(kbase1ksession1sks2);
        Assert.assertNotNull(kbase1ksession1sks22);
        Assert.assertNotSame(kbase1ksession1sks1, kbase1ksession1sks2);
        Assert.assertSame(kbase1ksession1sks2, kbase1ksession1sks22);
        KieCommands cmds = Factory.get().getCommands();
        List<String> list = new ArrayList<String>();
        kbase1ksession1sks1.setGlobal("list", list);
        kbase1ksession1sks1.execute(cmds.newFireAllRules());
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.get(0).endsWith("1.0"));
        Assert.assertTrue(list.get(1).endsWith("1.0"));
        list = new ArrayList<String>();
        kbase1ksession1sks2.setGlobal("list", list);
        kbase1ksession1sks2.execute(cmds.newFireAllRules());
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.get(0).endsWith("1.0"));
        Assert.assertTrue(list.get(1).endsWith("1.0"));
    }
}

