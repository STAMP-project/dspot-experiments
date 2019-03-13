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


import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.KieRepository;
import org.kie.api.command.KieCommands;
import org.kie.api.io.KieResources;


@RunWith(CDITestRunner.class)
public class KieServicesInjectionTest {
    @Inject
    KieServices sc;

    @Inject
    KieRepository kr;

    @Inject
    KieCommands cmds;

    @Inject
    KieResources rscs;

    @Test
    public void testKieServicesInjection() {
        Assert.assertNotNull(sc);
        Assert.assertNotNull(sc.getResources().newByteArrayResource(new byte[]{ 0 }));
    }

    @Test
    public void testKieRepositoryInjection() {
        Assert.assertNotNull(kr);
        Assert.assertNotNull(kr.getDefaultReleaseId());
    }

    @Test
    public void testKieCommands() {
        Assert.assertNotNull(cmds);
        Assert.assertNotNull(cmds.newFireAllRules());
    }

    @Test
    public void testKieResources() {
        Assert.assertNotNull(rscs);
        Assert.assertNotNull(rscs.newByteArrayResource(new byte[]{ 0 }));
    }
}

