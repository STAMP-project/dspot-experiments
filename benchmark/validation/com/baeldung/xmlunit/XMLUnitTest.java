package com.baeldung.xmlunit;


import ComparisonControllers.StopWhenDifferent;
import Languages.W3C_XML_SCHEMA_NS_URI;
import java.io.File;
import java.util.Iterator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.validation.ValidationProblem;
import org.xmlunit.validation.ValidationResult;
import org.xmlunit.validation.Validator;
import org.xmlunit.xpath.JAXPXPathEngine;

import static ElementSelectors.byName;


public class XMLUnitTest {
    @Test
    public void givenWrongXml_whenValidateFailsAgainstXsd_thenCorrect() {
        Validator v = Validator.forLanguage(W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(Input.fromStream(XMLUnitTest.class.getResourceAsStream("/students.xsd")).build());
        ValidationResult r = v.validateInstance(Input.fromStream(XMLUnitTest.class.getResourceAsStream("/students_with_error.xml")).build());
        Assert.assertFalse(r.isValid());
    }

    @Test
    public void givenXmlWithErrors_whenReturnsValidationProblems_thenCorrect() {
        Validator v = Validator.forLanguage(W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(Input.fromStream(XMLUnitTest.class.getResourceAsStream("/students.xsd")).build());
        ValidationResult r = v.validateInstance(Input.fromStream(XMLUnitTest.class.getResourceAsStream("/students_with_error.xml")).build());
        Iterator<ValidationProblem> probs = r.getProblems().iterator();
        int count = 0;
        while (probs.hasNext()) {
            count++;
            probs.next().toString();
        } 
        Assert.assertTrue((count > 0));
    }

    @Test
    public void givenXml_whenValidatesAgainstXsd_thenCorrect() {
        Validator v = Validator.forLanguage(W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(Input.fromStream(XMLUnitTest.class.getResourceAsStream("/students.xsd")).build());
        ValidationResult r = v.validateInstance(Input.fromStream(XMLUnitTest.class.getResourceAsStream("/students.xml")).build());
        Iterator<ValidationProblem> probs = r.getProblems().iterator();
        while (probs.hasNext()) {
            System.out.println(probs.next().toString());
        } 
        Assert.assertTrue(r.isValid());
    }

    @Test
    public void givenXPath_whenAbleToRetrieveNodes_thenCorrect() {
        ClassLoader classLoader = getClass().getClassLoader();
        Iterable<Node> i = new JAXPXPathEngine().selectNodes("//teacher", Input.fromFile(new File(classLoader.getResource("teachers.xml").getFile())).build());
        Assert.assertNotNull(i);
        int count = 0;
        for (Iterator<Node> it = i.iterator(); it.hasNext();) {
            count++;
            Node node = it.next();
            Assert.assertEquals("teacher", node.getNodeName());
            NamedNodeMap map = node.getAttributes();
            Assert.assertEquals("department", map.item(0).getNodeName());
            Assert.assertEquals("id", map.item(1).getNodeName());
            Assert.assertEquals("teacher", node.getNodeName());
        }
        Assert.assertEquals(2, count);
    }

    @Test
    public void givenXmlSource_whenAbleToValidateExistingXPath_thenCorrect() {
        ClassLoader classLoader = getClass().getClassLoader();
        MatcherAssert.assertThat(Input.fromFile(new File(classLoader.getResource("teachers.xml").getFile())), hasXPath("//teachers"));
        MatcherAssert.assertThat(Input.fromFile(new File(classLoader.getResource("teachers.xml").getFile())), hasXPath("//teacher"));
        MatcherAssert.assertThat(Input.fromFile(new File(classLoader.getResource("teachers.xml").getFile())), hasXPath("//subject"));
        MatcherAssert.assertThat(Input.fromFile(new File(classLoader.getResource("teachers.xml").getFile())), hasXPath("//@department"));
    }

    @Test
    public void givenXmlSource_whenFailsToValidateInExistentXPath_thenCorrect() {
        ClassLoader classLoader = getClass().getClassLoader();
        MatcherAssert.assertThat(Input.fromFile(new File(classLoader.getResource("teachers.xml").getFile())), IsNot.not(hasXPath("//sujet")));
    }

    @Test
    public void given2XMLS_whenSimilar_thenCorrect() {
        String controlXml = "<struct><int>3</int><boolean>false</boolean></struct>";
        String testXml = "<struct><boolean>false</boolean><int>3</int></struct>";
        MatcherAssert.assertThat(testXml, isSimilarTo(controlXml).withNodeMatcher(new DefaultNodeMatcher(byName)));
    }

    @Test
    public void given2XMLs_whenSimilarWithDiff_thenCorrect() throws Exception {
        String myControlXML = "<struct><int>3</int><boolean>false</boolean></struct>";
        String myTestXML = "<struct><boolean>false</boolean><int>3</int></struct>";
        Diff myDiffSimilar = DiffBuilder.compare(myControlXML).withTest(myTestXML).withNodeMatcher(new DefaultNodeMatcher(byName)).checkForSimilar().build();
        Assert.assertFalse(("XML similar " + (myDiffSimilar.toString())), myDiffSimilar.hasDifferences());
    }

    @Test
    public void given2XMLsWithDifferences_whenTestsSimilarWithDifferenceEvaluator_thenCorrect() {
        final String control = "<a><b attr=\"abc\"></b></a>";
        final String test = "<a><b attr=\"xyz\"></b></a>";
        Diff myDiff = DiffBuilder.compare(control).withTest(test).withDifferenceEvaluator(new IgnoreAttributeDifferenceEvaluator("attr")).checkForSimilar().build();
        Assert.assertFalse(myDiff.toString(), myDiff.hasDifferences());
    }

    @Test
    public void given2XMLsWithDifferences_whenTestsDifferentWithoutDifferenceEvaluator_thenCorrect() {
        final String control = "<a><b attr=\"abc\"></b></a>";
        final String test = "<a><b attr=\"xyz\"></b></a>";
        Diff myDiff = DiffBuilder.compare(control).withTest(test).checkForSimilar().build();
        // assertFalse(myDiff.toString(), myDiff.hasDifferences());
        Assert.assertTrue(myDiff.toString(), myDiff.hasDifferences());
    }

    @Test
    public void given2XMLS_whenSimilarWithCustomElementSelector_thenCorrect() {
        String controlXml = "<struct><int>3</int><boolean>false</boolean></struct>";
        String testXml = "<struct><boolean>false</boolean><int>3</int></struct>";
        MatcherAssert.assertThat(testXml, isSimilarTo(controlXml).withNodeMatcher(new DefaultNodeMatcher(byName)));
    }

    @Test
    public void givenFileSourceAsObject_whenAbleToInput_thenCorrect() {
        ClassLoader classLoader = getClass().getClassLoader();
        MatcherAssert.assertThat(Input.from(new File(classLoader.getResource("test.xml").getFile())), isSimilarTo(Input.from(new File(classLoader.getResource("control.xml").getFile()))));
    }

    @Test
    public void givenStreamAsSource_whenAbleToInput_thenCorrect() {
        MatcherAssert.assertThat(Input.fromStream(XMLUnitTest.class.getResourceAsStream("/test.xml")), isSimilarTo(Input.fromStream(XMLUnitTest.class.getResourceAsStream("/control.xml"))));
    }

    @Test
    public void givenStreamAsObject_whenAbleToInput_thenCorrect() {
        MatcherAssert.assertThat(Input.from(XMLUnitTest.class.getResourceAsStream("/test.xml")), isSimilarTo(Input.from(XMLUnitTest.class.getResourceAsStream("/control.xml"))));
    }

    @Test
    public void givenStringSourceAsObject_whenAbleToInput_thenCorrect() {
        MatcherAssert.assertThat(Input.from("<struct><int>3</int><boolean>false</boolean></struct>"), isSimilarTo(Input.from("<struct><int>3</int><boolean>false</boolean></struct>")));
    }

    @Test
    public void givenFileSource_whenAbleToInput_thenCorrect() {
        ClassLoader classLoader = getClass().getClassLoader();
        String testPath = classLoader.getResource("test.xml").getPath();
        String controlPath = classLoader.getResource("control.xml").getPath();
        MatcherAssert.assertThat(Input.fromFile(testPath), isSimilarTo(Input.fromFile(controlPath)));
    }

    @Test
    public void givenStringSource_whenAbleToInput_thenCorrect() {
        String controlXml = "<struct><int>3</int><boolean>false</boolean></struct>";
        String testXml = "<struct><int>3</int><boolean>false</boolean></struct>";
        MatcherAssert.assertThat(Input.fromString(testXml), isSimilarTo(Input.fromString(controlXml)));
    }

    @Test
    public void givenSource_whenAbleToInput_thenCorrect() {
        String controlXml = "<struct><int>3</int><boolean>false</boolean></struct>";
        String testXml = "<struct><int>3</int><boolean>false</boolean></struct>";
        MatcherAssert.assertThat(Input.fromString(testXml), isSimilarTo(Input.fromString(controlXml)));
    }

    @Test
    public void given2XMLS_whenIdentical_thenCorrect() {
        String controlXml = "<struct><int>3</int><boolean>false</boolean></struct>";
        String testXml = "<struct><int>3</int><boolean>false</boolean></struct>";
        MatcherAssert.assertThat(testXml, isIdenticalTo(controlXml));
    }

    @Test
    public void given2XMLSWithSimilarNodesButDifferentSequence_whenNotIdentical_thenCorrect() {
        String controlXml = "<struct><int>3</int><boolean>false</boolean></struct>";
        String testXml = "<struct><boolean>false</boolean><int>3</int></struct>";
        MatcherAssert.assertThat(testXml, IsNot.not(isIdenticalTo(controlXml)));
    }

    @Test
    public void given2XMLS_whenGeneratesDifferences_thenCorrect() throws Exception {
        String controlXml = "<struct><int>3</int><boolean>false</boolean></struct>";
        String testXml = "<struct><boolean>false</boolean><int>3</int></struct>";
        Diff myDiff = DiffBuilder.compare(controlXml).withTest(testXml).build();
        Iterator<Difference> iter = myDiff.getDifferences().iterator();
        int size = 0;
        while (iter.hasNext()) {
            iter.next().toString();
            size++;
        } 
        MatcherAssert.assertThat(size, Matchers.greaterThan(1));
    }

    @Test
    public void given2XMLS_whenGeneratesOneDifference_thenCorrect() throws Exception {
        String myControlXML = "<struct><int>3</int><boolean>false</boolean></struct>";
        String myTestXML = "<struct><boolean>false</boolean><int>3</int></struct>";
        Diff myDiff = DiffBuilder.compare(myControlXML).withTest(myTestXML).withComparisonController(StopWhenDifferent).build();
        Iterator<Difference> iter = myDiff.getDifferences().iterator();
        int size = 0;
        while (iter.hasNext()) {
            iter.next().toString();
            size++;
        } 
        MatcherAssert.assertThat(size, Matchers.equalTo(1));
    }
}

