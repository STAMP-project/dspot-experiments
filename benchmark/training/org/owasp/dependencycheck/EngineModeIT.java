package org.owasp.dependencycheck;


import Engine.Mode;
import Engine.Mode.EVIDENCE_COLLECTION;
import Engine.Mode.EVIDENCE_PROCESSING;
import Engine.Mode.STANDALONE;
import EvidenceType.VENDOR;
import java.io.File;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.owasp.dependencycheck.analyzer.AnalysisPhase;
import org.owasp.dependencycheck.dependency.Dependency;


/**
 *
 *
 * @author Mark Rekveld
 */
public class EngineModeIT extends BaseTest {
    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Rule
    public TestName testName = new TestName();

    private String originalDataDir = null;

    @Test
    public void testEvidenceCollectionAndEvidenceProcessingModes() throws Exception {
        Dependency[] dependencies;
        try (Engine engine = new Engine(Mode.EVIDENCE_COLLECTION, getSettings())) {
            engine.openDatabase();// does nothing in the current mode

            assertDatabase(false);
            EVIDENCE_COLLECTION.getPhases().forEach(( phase) -> {
                assertThat(engine.getAnalyzers(phase), is(notNullValue()));
            });
            EVIDENCE_PROCESSING.getPhases().forEach(( phase) -> {
                assertThat(engine.getAnalyzers(phase), is(nullValue()));
            });
            File file = BaseTest.getResourceAsFile(this, "struts2-core-2.1.2.jar");
            engine.scan(file);
            engine.analyzeDependencies();
            dependencies = engine.getDependencies();
            MatcherAssert.assertThat(dependencies.length, Is.is(1));
            Dependency dependency = dependencies[0];
            Assert.assertTrue(dependency.getEvidence(VENDOR).toString().toLowerCase().contains("apache"));
            Assert.assertTrue(dependency.getVendorWeightings().contains("apache"));
            Assert.assertTrue(dependency.getVulnerabilities().isEmpty());
        }
        try (Engine engine = new Engine(Mode.EVIDENCE_PROCESSING, getSettings())) {
            engine.openDatabase();
            assertDatabase(true);
            EVIDENCE_PROCESSING.getPhases().forEach(( phase) -> {
                assertThat(engine.getAnalyzers(phase), is(notNullValue()));
            });
            EVIDENCE_COLLECTION.getPhases().forEach(( phase) -> {
                assertThat(engine.getAnalyzers(phase), is(nullValue()));
            });
            engine.addDependency(dependencies[0]);
            engine.analyzeDependencies();
            Dependency dependency = dependencies[0];
            Assert.assertFalse(dependency.getVulnerabilities().isEmpty());
        }
    }

    @Test
    public void testStandaloneMode() throws Exception {
        try (Engine engine = new Engine(Mode.STANDALONE, getSettings())) {
            engine.openDatabase();
            assertDatabase(true);
            for (AnalysisPhase phase : STANDALONE.getPhases()) {
                MatcherAssert.assertThat(engine.getAnalyzers(phase), Is.is(CoreMatchers.notNullValue()));
            }
            File file = BaseTest.getResourceAsFile(this, "struts2-core-2.1.2.jar");
            engine.scan(file);
            engine.analyzeDependencies();
            Dependency[] dependencies = engine.getDependencies();
            // 8 because there is JS being caught by the retireJS analyzer
            MatcherAssert.assertThat(dependencies.length, Is.is(8));
            Dependency dependency = dependencies[0];
            Assert.assertTrue(dependency.getEvidence(VENDOR).toString().toLowerCase().contains("apache"));
            Assert.assertTrue(dependency.getVendorWeightings().contains("apache"));
            Assert.assertFalse(dependency.getVulnerabilities().isEmpty());
        }
    }
}

