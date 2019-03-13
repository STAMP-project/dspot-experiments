package org.owasp.dependencycheck.analyzer;


import EvidenceType.PRODUCT;
import EvidenceType.VENDOR;
import EvidenceType.VERSION;
import Settings.KEYS.ANALYZER_NODE_AUDIT_ENABLED;
import java.io.File;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.analyzer.exception.AnalysisException;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.exception.InitializationException;
import org.owasp.dependencycheck.utils.InvalidSettingException;


public class NodeAuditAnalyzerTest extends BaseTest {
    @Test
    public void testGetName() {
        NodeAuditAnalyzer analyzer = new NodeAuditAnalyzer();
        Assert.assertThat(analyzer.getName(), CoreMatchers.is("Node Audit Analyzer"));
    }

    @Test
    public void testSupportsFiles() {
        NodeAuditAnalyzer analyzer = new NodeAuditAnalyzer();
        Assert.assertThat(analyzer.accept(new File("package-lock.json")), CoreMatchers.is(true));
        Assert.assertThat(analyzer.accept(new File("npm-shrinkwrap.json")), CoreMatchers.is(true));
        Assert.assertThat(analyzer.accept(new File("package.json")), CoreMatchers.is(false));
    }

    @Test
    public void testAnalyzePackage() throws AnalysisException, InitializationException, InvalidSettingException {
        Assume.assumeThat(getSettings().getBoolean(ANALYZER_NODE_AUDIT_ENABLED), CoreMatchers.is(true));
        try (Engine engine = new Engine(getSettings())) {
            NodeAuditAnalyzer analyzer = new NodeAuditAnalyzer();
            analyzer.setFilesMatched(true);
            analyzer.initialize(getSettings());
            analyzer.prepare(engine);
            final Dependency toScan = new Dependency(BaseTest.getResourceAsFile(this, "nodeaudit/package-lock.json"));
            analyzer.analyze(toScan, engine);
            boolean found = false;
            Assert.assertTrue("Mpre then 1 dependency should be identified", (1 < (engine.getDependencies().length)));
            for (Dependency result : engine.getDependencies()) {
                if ("package-lock.json?uglify-js".equals(result.getFileName())) {
                    found = true;
                    Assert.assertTrue(result.getEvidence(VENDOR).toString().contains("uglify-js"));
                    Assert.assertTrue(result.getEvidence(PRODUCT).toString().contains("uglify-js"));
                    Assert.assertTrue(result.getEvidence(VERSION).toString().contains("2.4.24"));
                    Assert.assertTrue(result.isVirtual());
                }
            }
            Assert.assertTrue("Uglify was not found", found);
        }
    }

    @Test
    public void testAnalyzeEmpty() throws AnalysisException, InitializationException, InvalidSettingException {
        Assume.assumeThat(getSettings().getBoolean(ANALYZER_NODE_AUDIT_ENABLED), CoreMatchers.is(true));
        try (Engine engine = new Engine(getSettings())) {
            NodeAuditAnalyzer analyzer = new NodeAuditAnalyzer();
            analyzer.setFilesMatched(true);
            analyzer.initialize(getSettings());
            analyzer.prepare(engine);
            final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "nodeaudit/empty.json"));
            analyzer.analyze(result, engine);
            Assert.assertEquals(0, result.getEvidence(VENDOR).size());
            Assert.assertEquals(0, result.getEvidence(PRODUCT).size());
            Assert.assertEquals(0, result.getEvidence(VERSION).size());
        }
    }

    @Test
    public void testAnalyzePackageJsonInNodeModulesDirectory() throws AnalysisException, InitializationException, InvalidSettingException {
        Assume.assumeThat(getSettings().getBoolean(ANALYZER_NODE_AUDIT_ENABLED), CoreMatchers.is(true));
        try (Engine engine = new Engine(getSettings())) {
            NodeAuditAnalyzer analyzer = new NodeAuditAnalyzer();
            analyzer.setFilesMatched(true);
            analyzer.initialize(getSettings());
            analyzer.prepare(engine);
            final Dependency toScan = new Dependency(BaseTest.getResourceAsFile(this, "nodejs/node_modules/dns-sync/package.json"));
            engine.addDependency(toScan);
            analyzer.analyze(toScan, engine);
            Assert.assertEquals("No dependencies should exist", 0, engine.getDependencies().length);
        }
    }
}

