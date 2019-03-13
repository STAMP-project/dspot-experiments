package org.drools.testcoverage.kieci.withoutdomain;


import KieServices.Factory;
import org.assertj.core.api.Assertions;
import org.drools.testcoverage.kieci.withoutdomain.util.KJarLoadUtils;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieSession;


/**
 * Tests loading a KJAR with non-trivial pom.xml (dependencies, parent pom, ...).
 *
 * Tests must NOT have access to domain classes in test-domain module (BZ 1305798).
 */
public class KJarLoadingTest {
    private static final KieServices KS = Factory.get();

    private static final ReleaseId KJAR_RELEASE_ID = KJarLoadUtils.loadKJarGAV("testKJarGAV.properties", KJarLoadingTest.class);

    private KieSession kieSession;

    @Test
    public void testLoadingKJarWithDeps() {
        // BZ 1305798
        Assertions.assertThat(this.kieSession).as("Failed to create KieSession.").isNotNull();
        Assertions.assertThat(this.kieSession.getKieBase().getKiePackages()).as("No rules compiled.").isNotEmpty();
    }
}

