/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.sourceforge.pmd.util.ResourceLoader;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class RuleSetReferenceIdTest {
    @Test(expected = IllegalArgumentException.class)
    public void testCommaInSingleId() {
        new RuleSetReferenceId("bad,id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInternalWithInternal() {
        new RuleSetReferenceId("SomeRule", new RuleSetReferenceId("SomeOtherRule"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExternalWithExternal() {
        new RuleSetReferenceId("someruleset.xml/SomeRule", new RuleSetReferenceId("someruleset.xml/SomeOtherRule"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExternalWithInternal() {
        new RuleSetReferenceId("someruleset.xml/SomeRule", new RuleSetReferenceId("SomeOtherRule"));
    }

    @Test
    public void testInteralWithExternal() {
        // This is okay
        new RuleSetReferenceId("SomeRule", new RuleSetReferenceId("someruleset.xml/SomeOtherRule"));
    }

    @Test
    public void testEmptyRuleSet() {
        // This is representative of how the Test framework creates
        // RuleSetReferenceId from static RuleSet XMLs
        RuleSetReferenceId reference = new RuleSetReferenceId(null);
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, null, true, null, "anonymous all Rule", reference);
    }

    @Test
    public void testInternalWithExternalRuleSet() {
        // This is representative of how the RuleSetFactory temporarily pairs an
        // internal reference
        // with an external reference.
        RuleSetReferenceId internalRuleSetReferenceId = new RuleSetReferenceId("MockRuleName");
        RuleSetReferenceIdTest.assertRuleSetReferenceId(false, null, false, "MockRuleName", "MockRuleName", internalRuleSetReferenceId);
        RuleSetReferenceId externalRuleSetReferenceId = new RuleSetReferenceId("rulesets/java/basic.xml");
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/java/basic.xml", true, null, "rulesets/java/basic.xml", externalRuleSetReferenceId);
        RuleSetReferenceId pairRuleSetReferenceId = new RuleSetReferenceId("MockRuleName", externalRuleSetReferenceId);
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/java/basic.xml", false, "MockRuleName", "rulesets/java/basic.xml/MockRuleName", pairRuleSetReferenceId);
    }

    @Test
    public void testConstructorGivenHttpUrlIdSucceedsAndProcessesIdCorrectly() {
        final String sonarRulesetUrlId = "http://localhost:54321/profiles/export?format=pmd&language=java&name=Sonar%2520way";
        RuleSetReferenceId ruleSetReferenceId = new RuleSetReferenceId((("  " + sonarRulesetUrlId) + "  "));
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, sonarRulesetUrlId, true, null, sonarRulesetUrlId, ruleSetReferenceId);
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(0);

    @Test
    public void testConstructorGivenHttpUrlInputStream() throws Exception {
        String path = "/profiles/export?format=pmd&language=java&name=Sonar%2520way";
        String rulesetUrl = ("http://localhost:" + (wireMockRule.port())) + path;
        stubFor(head(urlEqualTo(path)).willReturn(aResponse().withStatus(200)));
        stubFor(get(urlEqualTo(path)).willReturn(aResponse().withStatus(200).withHeader("Content-type", "text/xml").withBody("xyz")));
        RuleSetReferenceId ruleSetReferenceId = new RuleSetReferenceId((("  " + rulesetUrl) + "  "));
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, rulesetUrl, true, null, rulesetUrl, ruleSetReferenceId);
        try (InputStream inputStream = ruleSetReferenceId.getInputStream(new ResourceLoader())) {
            String loaded = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Assert.assertEquals("xyz", loaded);
        }
        verify(1, headRequestedFor(urlEqualTo(path)));
        verify(0, headRequestedFor(urlEqualTo("/profiles")));
        verify(1, getRequestedFor(urlEqualTo(path)));
        Assert.assertEquals(1, findAll(headRequestedFor(urlMatching(".*"))).size());
        Assert.assertEquals(1, findAll(getRequestedFor(urlMatching(".*"))).size());
    }

    @Test
    public void testConstructorGivenHttpUrlSingleRuleInputStream() throws Exception {
        String path = "/profiles/export?format=pmd&language=java&name=Sonar%2520way";
        String completePath = path + "/DummyBasicMockRule";
        String hostpart = "http://localhost:" + (wireMockRule.port());
        String basicRuleSet = IOUtils.toString(RuleSetReferenceId.class.getResourceAsStream("/rulesets/dummy/basic.xml"), StandardCharsets.UTF_8);
        stubFor(head(urlEqualTo(completePath)).willReturn(aResponse().withStatus(404)));
        stubFor(head(urlEqualTo(path)).willReturn(aResponse().withStatus(200).withHeader("Content-type", "text/xml")));
        stubFor(get(urlEqualTo(path)).willReturn(aResponse().withStatus(200).withHeader("Content-type", "text/xml").withBody(basicRuleSet)));
        RuleSetReferenceId ruleSetReferenceId = new RuleSetReferenceId(((("  " + hostpart) + completePath) + "  "));
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, (hostpart + path), false, "DummyBasicMockRule", (hostpart + completePath), ruleSetReferenceId);
        try (InputStream inputStream = ruleSetReferenceId.getInputStream(new ResourceLoader())) {
            String loaded = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Assert.assertEquals(basicRuleSet, loaded);
        }
        verify(1, headRequestedFor(urlEqualTo(completePath)));
        verify(1, headRequestedFor(urlEqualTo(path)));
        verify(1, getRequestedFor(urlEqualTo(path)));
        verify(0, getRequestedFor(urlEqualTo(completePath)));
        Assert.assertEquals(2, findAll(headRequestedFor(urlMatching(".*"))).size());
        Assert.assertEquals(1, findAll(getRequestedFor(urlMatching(".*"))).size());
    }

    @Test
    public void testOneSimpleRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("dummy-basic");
        Assert.assertEquals(1, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/dummy/basic.xml", true, null, "rulesets/dummy/basic.xml", references.get(0));
    }

    @Test
    public void testMultipleSimpleRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("dummy-unusedcode,dummy-basic");
        Assert.assertEquals(2, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/dummy/unusedcode.xml", true, null, "rulesets/dummy/unusedcode.xml", references.get(0));
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/dummy/basic.xml", true, null, "rulesets/dummy/basic.xml", references.get(1));
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1201/
     */
    @Test
    public void testMultipleRulesWithSpaces() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("dummy-basic, dummy-unusedcode, dummy2-basic");
        Assert.assertEquals(3, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/dummy/basic.xml", true, null, "rulesets/dummy/basic.xml", references.get(0));
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/dummy/unusedcode.xml", true, null, "rulesets/dummy/unusedcode.xml", references.get(1));
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/dummy2/basic.xml", true, null, "rulesets/dummy2/basic.xml", references.get(2));
    }

    @Test
    public void testOneReleaseRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("50");
        Assert.assertEquals(1, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/releases/50.xml", true, null, "rulesets/releases/50.xml", references.get(0));
    }

    @Test
    public void testOneFullRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("rulesets/java/unusedcode.xml");
        Assert.assertEquals(1, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/java/unusedcode.xml", true, null, "rulesets/java/unusedcode.xml", references.get(0));
    }

    @Test
    public void testOneFullRuleSetURL() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("file://somepath/rulesets/java/unusedcode.xml");
        Assert.assertEquals(1, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "file://somepath/rulesets/java/unusedcode.xml", true, null, "file://somepath/rulesets/java/unusedcode.xml", references.get(0));
    }

    @Test
    public void testMultipleFullRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("rulesets/java/unusedcode.xml,rulesets/java/basic.xml");
        Assert.assertEquals(2, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/java/unusedcode.xml", true, null, "rulesets/java/unusedcode.xml", references.get(0));
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/java/basic.xml", true, null, "rulesets/java/basic.xml", references.get(1));
    }

    @Test
    public void testMixRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("rulesets/dummy/unusedcode.xml,dummy2-basic");
        Assert.assertEquals(2, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/dummy/unusedcode.xml", true, null, "rulesets/dummy/unusedcode.xml", references.get(0));
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/dummy2/basic.xml", true, null, "rulesets/dummy2/basic.xml", references.get(1));
    }

    @Test
    public void testUnknownRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("nonexistant.xml");
        Assert.assertEquals(1, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "nonexistant.xml", true, null, "nonexistant.xml", references.get(0));
    }

    @Test
    public void testUnknownAndSimpleRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("dummy-basic,nonexistant.xml");
        Assert.assertEquals(2, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/dummy/basic.xml", true, null, "rulesets/dummy/basic.xml", references.get(0));
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "nonexistant.xml", true, null, "nonexistant.xml", references.get(1));
    }

    @Test
    public void testSimpleRuleSetAndRule() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("dummy-basic/DummyBasicMockRule");
        Assert.assertEquals(1, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/dummy/basic.xml", false, "DummyBasicMockRule", "rulesets/dummy/basic.xml/DummyBasicMockRule", references.get(0));
    }

    @Test
    public void testFullRuleSetAndRule() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("rulesets/java/basic.xml/EmptyCatchBlock");
        Assert.assertEquals(1, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "rulesets/java/basic.xml", false, "EmptyCatchBlock", "rulesets/java/basic.xml/EmptyCatchBlock", references.get(0));
    }

    @Test
    public void testFullRuleSetURLAndRule() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("file://somepath/rulesets/java/unusedcode.xml/EmptyCatchBlock");
        Assert.assertEquals(1, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "file://somepath/rulesets/java/unusedcode.xml", false, "EmptyCatchBlock", "file://somepath/rulesets/java/unusedcode.xml/EmptyCatchBlock", references.get(0));
    }

    @Test
    public void testInternalRuleSetAndRule() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("EmptyCatchBlock");
        Assert.assertEquals(1, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(false, null, false, "EmptyCatchBlock", "EmptyCatchBlock", references.get(0));
    }

    @Test
    public void testRelativePathRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("pmd/pmd-ruleset.xml");
        Assert.assertEquals(1, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "pmd/pmd-ruleset.xml", true, null, "pmd/pmd-ruleset.xml", references.get(0));
    }

    @Test
    public void testAbsolutePathRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("/home/foo/pmd/pmd-ruleset.xml");
        Assert.assertEquals(1, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, "/home/foo/pmd/pmd-ruleset.xml", true, null, "/home/foo/pmd/pmd-ruleset.xml", references.get(0));
    }

    @Test
    public void testFooRules() throws Exception {
        String fooRulesFile = new File("./src/test/resources/net/sourceforge/pmd/rulesets/foo-project/foo-rules").getCanonicalPath();
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse(fooRulesFile);
        Assert.assertEquals(1, references.size());
        RuleSetReferenceIdTest.assertRuleSetReferenceId(true, fooRulesFile, true, null, fooRulesFile, references.get(0));
    }

    @Test
    public void testNullRulesetString() throws Exception {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse(null);
        Assert.assertTrue(references.isEmpty());
    }
}

