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
package org.kie.scanner.management;


import InternalKieScanner.Status.RUNNING;
import InternalKieScanner.Status.SHUTDOWN;
import InternalKieScanner.Status.STOPPED;
import KieServices.Factory;
import javax.management.ObjectName;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.util.FileManager;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.scanner.AbstractKieCiTest;
import org.kie.scanner.KieMavenRepository;
import org.kie.scanner.KieRepositoryScannerImpl;


public class KieScannerMBeanTest extends AbstractKieCiTest {
    private FileManager fileManager;

    @Test
    public void testKScannerMBean() throws Exception {
        KieServices ks = Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-mbean-test", "1.0-SNAPSHOT");
        InternalKieModule kJar1 = createKieJar(ks, releaseId, "rule1", "rule2");
        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieMavenRepository repository = KieMavenRepository.getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, createKPom(fileManager, releaseId));
        // create a ksesion and check it works as expected
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");
        KieRepositoryScannerImpl scanner = ((KieRepositoryScannerImpl) (ks.newKieScanner(kieContainer)));
        KieScannerMBeanImpl mBean = ((KieScannerMBeanImpl) (scanner.getMBean()));
        ObjectName mbeanName = mBean.getMBeanName();
        // we want to check that the mbean is register in the server and exposing the correct attribute values
        // so we fetch the attributes from the server
        Assert.assertEquals(releaseId.toExternalForm(), MBeanUtils.getAttribute(mbeanName, "ScannerReleaseId"));
        Assert.assertEquals(releaseId.toExternalForm(), MBeanUtils.getAttribute(mbeanName, "CurrentReleaseId"));
        Assert.assertEquals(STOPPED.toString(), MBeanUtils.getAttribute(mbeanName, "Status"));
        MBeanUtils.invoke(mbeanName, "start", new Object[]{ Long.valueOf(10000) }, new String[]{ "long" });
        Assert.assertEquals(RUNNING.toString(), MBeanUtils.getAttribute(mbeanName, "Status"));
        MBeanUtils.invoke(mbeanName, "stop", new Object[]{  }, new String[]{  });
        Assert.assertEquals(STOPPED.toString(), MBeanUtils.getAttribute(mbeanName, "Status"));
        // create a new kjar
        InternalKieModule kJar2 = createKieJar(ks, releaseId, "rule2", "rule3");
        // deploy it on maven
        repository.installArtifact(releaseId, kJar2, createKPom(fileManager, releaseId));
        MBeanUtils.invoke(mbeanName, "scanNow", new Object[]{  }, new String[]{  });
        // create a ksesion and check it works as expected
        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, "rule2", "rule3");
        MBeanUtils.invoke(mbeanName, "shutdown", new Object[]{  }, new String[]{  });
        Assert.assertEquals(SHUTDOWN.toString(), MBeanUtils.getAttribute(mbeanName, "Status"));
        ks.getRepository().removeKieModule(releaseId);
    }
}

