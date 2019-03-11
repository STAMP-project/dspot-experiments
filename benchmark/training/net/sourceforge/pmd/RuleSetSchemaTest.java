/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class RuleSetSchemaTest {
    private RuleSetSchemaTest.CollectingErrorHandler errorHandler;

    @Test
    public void verifyVersion2() throws Exception {
        String ruleset = generateRuleSet("2.0.0");
        Document doc = parseWithVersion2(ruleset);
        Assert.assertNotNull(doc);
        Assert.assertTrue(errorHandler.isValid());
        Assert.assertEquals("Custom ruleset", ((Attr) (doc.getElementsByTagName("ruleset").item(0).getAttributes().getNamedItem("name"))).getValue());
    }

    @Test
    public void validateOnly() throws Exception {
        Validator validator = RuleSetSchemaTest.PMDRuleSetEntityResolver.getSchemaVersion2().newValidator();
        validator.setErrorHandler(errorHandler);
        validator.validate(new StreamSource(new ByteArrayInputStream(generateRuleSet("2.0.0").getBytes(StandardCharsets.UTF_8))));
        Assert.assertTrue(errorHandler.isValid());
        errorHandler.reset();
    }

    public static class PMDRuleSetEntityResolver implements EntityResolver {
        private static URL schema2 = RuleSetFactory.class.getResource("/ruleset_2_0_0.xsd");

        private static SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
            if ("https://pmd.sourceforge.io/ruleset_2_0_0.xsd".equals(systemId)) {
                return new InputSource(RuleSetSchemaTest.PMDRuleSetEntityResolver.schema2.toExternalForm());
            }
            throw new IllegalArgumentException((((("Unable to resolve entity (publicId=" + publicId) + ", systemId=") + systemId) + ")"));
        }

        public static Schema getSchemaVersion2() throws SAXException {
            return RuleSetSchemaTest.PMDRuleSetEntityResolver.schemaFactory.newSchema(RuleSetSchemaTest.PMDRuleSetEntityResolver.schema2);
        }
    }

    public static class CollectingErrorHandler implements ErrorHandler {
        private List<SAXParseException> warnings = new ArrayList<>();

        private List<SAXParseException> errors = new ArrayList<>();

        private List<SAXParseException> fatalErrors = new ArrayList<>();

        public boolean isValid() {
            return ((warnings.isEmpty()) && (errors.isEmpty())) && (fatalErrors.isEmpty());
        }

        public List<SAXParseException> getWarnings() {
            return warnings;
        }

        public List<SAXParseException> getErrors() {
            return errors;
        }

        public List<SAXParseException> getFatalErrors() {
            return fatalErrors;
        }

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            warnings.add(exception);
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            errors.add(exception);
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            fatalErrors.add(exception);
        }

        @Override
        public String toString() {
            return (((("Warnings: " + (warnings)) + "; Errors: ") + (errors)) + "; Fatal Errors: ") + (fatalErrors);
        }

        public void reset() {
            warnings.clear();
            errors.clear();
            fatalErrors.clear();
        }
    }
}

