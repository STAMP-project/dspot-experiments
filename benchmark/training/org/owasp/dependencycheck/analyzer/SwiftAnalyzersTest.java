package org.owasp.dependencycheck.analyzer;


import CocoaPodsAnalyzer.DEPENDENCY_ECOSYSTEM;
import EvidenceType.PRODUCT;
import EvidenceType.VENDOR;
import EvidenceType.VERSION;
import java.io.File;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.analyzer.exception.AnalysisException;
import org.owasp.dependencycheck.dependency.Dependency;


/**
 * Unit tests for CocoaPodsAnalyzer.
 *
 * @author Bianca Jiang
 */
public class SwiftAnalyzersTest extends BaseTest {
    /**
     * The analyzer to test.
     */
    private CocoaPodsAnalyzer podsAnalyzer;

    private SwiftPackageManagerAnalyzer spmAnalyzer;

    /**
     * Test of getName method, of class CocoaPodsAnalyzer.
     */
    @Test
    public void testPodsGetName() {
        Assert.assertThat(podsAnalyzer.getName(), CoreMatchers.is("CocoaPods Package Analyzer"));
    }

    /**
     * Test of getName method, of class SwiftPackageManagerAnalyzer.
     */
    @Test
    public void testSPMGetName() {
        Assert.assertThat(spmAnalyzer.getName(), CoreMatchers.is("SWIFT Package Manager Analyzer"));
    }

    /**
     * Test of supportsFiles method, of class CocoaPodsAnalyzer.
     */
    @Test
    public void testPodsSupportsFiles() {
        Assert.assertThat(podsAnalyzer.accept(new File("test.podspec")), CoreMatchers.is(true));
        Assert.assertThat(podsAnalyzer.accept(new File("Podfile.lock")), CoreMatchers.is(true));
    }

    /**
     * Test of supportsFiles method, of class SwiftPackageManagerAnalyzer.
     */
    @Test
    public void testSPMSupportsFiles() {
        Assert.assertThat(spmAnalyzer.accept(new File("Package.swift")), CoreMatchers.is(true));
    }

    /**
     * Test of analyze method, of class CocoaPodsAnalyzer.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testCocoaPodsPodfileAnalyzer() throws AnalysisException {
        final Engine engine = new Engine(getSettings());
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "swift/cocoapods/Podfile.lock"));
        podsAnalyzer.analyze(result, engine);
        Assert.assertThat(engine.getDependencies().length, CoreMatchers.equalTo(9));
        Assert.assertThat(engine.getDependencies()[0].getName(), CoreMatchers.equalTo("Bolts"));
        Assert.assertThat(engine.getDependencies()[0].getVersion(), CoreMatchers.equalTo("1.9.0"));
        Assert.assertThat(engine.getDependencies()[1].getName(), CoreMatchers.equalTo("Bolts/AppLinks"));
        Assert.assertThat(engine.getDependencies()[1].getVersion(), CoreMatchers.equalTo("1.9.0"));
        Assert.assertThat(engine.getDependencies()[2].getName(), CoreMatchers.equalTo("Bolts/Tasks"));
        Assert.assertThat(engine.getDependencies()[2].getVersion(), CoreMatchers.equalTo("1.9.0"));
        Assert.assertThat(engine.getDependencies()[3].getName(), CoreMatchers.equalTo("FBSDKCoreKit"));
        Assert.assertThat(engine.getDependencies()[3].getVersion(), CoreMatchers.equalTo("4.33.0"));
        Assert.assertThat(engine.getDependencies()[4].getName(), CoreMatchers.equalTo("FBSDKLoginKit"));
        Assert.assertThat(engine.getDependencies()[4].getVersion(), CoreMatchers.equalTo("4.33.0"));
        Assert.assertThat(engine.getDependencies()[5].getName(), CoreMatchers.equalTo("FirebaseCore"));
        Assert.assertThat(engine.getDependencies()[5].getVersion(), CoreMatchers.equalTo("5.0.1"));
        Assert.assertThat(engine.getDependencies()[6].getName(), CoreMatchers.equalTo("GoogleToolboxForMac/Defines"));
        Assert.assertThat(engine.getDependencies()[6].getVersion(), CoreMatchers.equalTo("2.1.4"));
        Assert.assertThat(engine.getDependencies()[7].getName(), CoreMatchers.equalTo("GoogleToolboxForMac/NSData+zlib"));
        Assert.assertThat(engine.getDependencies()[7].getVersion(), CoreMatchers.equalTo("2.1.4"));
        Assert.assertThat(engine.getDependencies()[8].getName(), CoreMatchers.equalTo("OCMock"));
        Assert.assertThat(engine.getDependencies()[8].getVersion(), CoreMatchers.equalTo("3.4.1"));
    }

    @Test
    public void testCocoaPodsPodspecAnalyzer() throws AnalysisException {
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "swift/cocoapods/EasyPeasy.podspec"));
        podsAnalyzer.analyze(result, null);
        final String vendorString = result.getEvidence(VENDOR).toString();
        Assert.assertThat(vendorString, CoreMatchers.containsString("Carlos Vidal"));
        Assert.assertThat(vendorString, CoreMatchers.containsString("https://github.com/nakiostudio/EasyPeasy"));
        Assert.assertThat(result.getEvidence(PRODUCT).toString(), CoreMatchers.containsString("EasyPeasy"));
        Assert.assertThat(result.getEvidence(VERSION).toString(), CoreMatchers.containsString("0.2.3"));
        Assert.assertThat(result.getName(), CoreMatchers.equalTo("EasyPeasy"));
        Assert.assertThat(result.getVersion(), CoreMatchers.equalTo("0.2.3"));
        Assert.assertThat(result.getDisplayFileName(), CoreMatchers.equalTo("EasyPeasy:0.2.3"));
        Assert.assertThat(result.getLicense(), CoreMatchers.containsString("MIT"));
        Assert.assertThat(result.getEcosystem(), CoreMatchers.equalTo(DEPENDENCY_ECOSYSTEM));
    }

    /**
     * Test of analyze method, of class SwiftPackageManagerAnalyzer.
     *
     * @throws AnalysisException
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testSPMAnalyzer() throws AnalysisException {
        final Dependency result = new Dependency(BaseTest.getResourceAsFile(this, "swift/Gloss/Package.swift"));
        spmAnalyzer.analyze(result, null);
        Assert.assertThat(result.getEvidence(PRODUCT).toString(), CoreMatchers.containsString("Gloss"));
        Assert.assertThat(result.getName(), CoreMatchers.equalTo("Gloss"));
        // TODO: when version processing is added, update the expected name.
        Assert.assertThat(result.getDisplayFileName(), CoreMatchers.equalTo("Gloss"));
        Assert.assertThat(result.getEcosystem(), CoreMatchers.equalTo(SwiftPackageManagerAnalyzer.DEPENDENCY_ECOSYSTEM));
    }
}

