package org.drools.compiler.integrationtests;


import org.drools.compiler.CommonTestMethodBase;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;


public class KieServicesTest extends CommonTestMethodBase {
    private KieServices ks;

    @Test
    public void testGetKieClasspathIDs() {
        String myId = "myId";
        KieContainer c1 = ks.getKieClasspathContainer(myId);
        Assert.assertEquals(c1, ks.getKieClasspathContainer());
        Assert.assertEquals(c1, ks.getKieClasspathContainer(myId));
        try {
            ks.getKieClasspathContainer("invalid");
            Assert.fail("this is not the containerId for the global singleton.");
        } catch (IllegalStateException is) {
            // ok.
        }
    }

    @Test
    public void testNewKieClasspathIDs() {
        KieContainer c1 = ks.newKieClasspathContainer("id1");
        KieContainer c2 = ks.newKieClasspathContainer("id2");
        try {
            ks.newKieClasspathContainer("id2");
            Assert.fail("should not allow repeated container IDs.");
        } catch (IllegalStateException is) {
            // ok.
        }
    }

    @Test
    public void testNewKieContainerIDs() {
        ReleaseId releaseId = ks.newReleaseId("org.kie", "test-delete", "1.0.0");
        CommonTestMethodBase.createAndDeployJar(ks, releaseId, createDRL("ruleA"));
        KieContainer c1 = ks.newKieContainer("id1", releaseId);
        KieContainer c2 = ks.newKieClasspathContainer("id2");
        try {
            ks.newKieContainer("id2", releaseId);
            Assert.fail("should not allow repeated container IDs.");
        } catch (IllegalStateException is) {
            // ok.
        }
        try {
            ks.newKieClasspathContainer("id1");
            Assert.fail("should not allow repeated container IDs.");
        } catch (IllegalStateException is) {
            // ok.
        }
    }

    @Test
    public void testDisposeClearTheIDReference() {
        ReleaseId releaseId = ks.newReleaseId("org.kie", "test-delete", "1.0.0");
        CommonTestMethodBase.createAndDeployJar(ks, releaseId, createDRL("ruleA"));
        KieContainer c1 = ks.newKieContainer("id1", releaseId);
        try {
            ks.newKieClasspathContainer("id1");
            Assert.fail("should not allow repeated container IDs.");
        } catch (IllegalStateException is) {
            // ok.
        }
        dispose();
        ks.newKieClasspathContainer("id1");// now OK.

    }
}

