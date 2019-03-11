package liquibase.changelog;


import java.util.List;
import liquibase.exception.ChangeLogParseException;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.parser.core.sql.SqlChangeLogParser;
import liquibase.parser.core.xml.XMLChangeLogSAXParser;
import liquibase.resource.ResourceAccessor;
import liquibase.test.JUnitResourceAccessor;
import org.junit.Assert;
import org.junit.Test;


public class ChangeLogParserFactoryTest {
    @Test
    public void getInstance() {
        Assert.assertNotNull(ChangeLogParserFactory.getInstance());
        Assert.assertTrue(((ChangeLogParserFactory.getInstance()) == (ChangeLogParserFactory.getInstance())));
    }

    @Test
    public void register() {
        ChangeLogParserFactory.getInstance().getParsers().clear();
        Assert.assertEquals(0, ChangeLogParserFactory.getInstance().getParsers().size());
        ChangeLogParserFactory.getInstance().register(new ChangeLogParserFactoryTest.MockChangeLogParser());
        Assert.assertEquals(1, ChangeLogParserFactory.getInstance().getParsers().size());
    }

    @Test
    public void unregister_instance() {
        ChangeLogParserFactory factory = ChangeLogParserFactory.getInstance();
        factory.getParsers().clear();
        Assert.assertEquals(0, factory.getParsers().size());
        ChangeLogParser mockChangeLogParser = new ChangeLogParserFactoryTest.MockChangeLogParser();
        factory.register(new XMLChangeLogSAXParser());
        factory.register(mockChangeLogParser);
        factory.register(new SqlChangeLogParser());
        Assert.assertEquals(3, factory.getParsers().size());
        factory.unregister(mockChangeLogParser);
        Assert.assertEquals(2, factory.getParsers().size());
    }

    @Test
    public void getParser_byExtension() throws Exception {
        ChangeLogParserFactory.getInstance().getParsers().clear();
        XMLChangeLogSAXParser xmlChangeLogParser = new XMLChangeLogSAXParser();
        ChangeLogParserFactory.getInstance().register(xmlChangeLogParser);
        ChangeLogParserFactory.getInstance().register(new SqlChangeLogParser());
        ChangeLogParser parser = ChangeLogParserFactory.getInstance().getParser("xml", new JUnitResourceAccessor());
        Assert.assertNotNull(parser);
        Assert.assertTrue((xmlChangeLogParser == parser));
    }

    @Test
    public void getParser_byFile() throws Exception {
        ChangeLogParserFactory.getInstance().getParsers().clear();
        XMLChangeLogSAXParser xmlChangeLogParser = new XMLChangeLogSAXParser();
        ChangeLogParserFactory.getInstance().register(xmlChangeLogParser);
        ChangeLogParserFactory.getInstance().register(new SqlChangeLogParser());
        ChangeLogParser parser = ChangeLogParserFactory.getInstance().getParser("path/to/a/file.xml", new JUnitResourceAccessor());
        Assert.assertNotNull(parser);
        Assert.assertTrue((xmlChangeLogParser == parser));
    }

    @Test
    public void getParser_noneMatching() throws Exception {
        ChangeLogParserFactory.getInstance().getParsers().clear();
        ChangeLogParserFactory.getInstance().getParsers().clear();
        XMLChangeLogSAXParser xmlChangeLogParser = new XMLChangeLogSAXParser();
        ChangeLogParserFactory.getInstance().register(xmlChangeLogParser);
        ChangeLogParserFactory.getInstance().register(new SqlChangeLogParser());
        try {
            ChangeLogParserFactory.getInstance().getParser("badextension", new JUnitResourceAccessor());
            Assert.fail("Did not throw an exception");
        } catch (Exception e) {
            // what we want
        }
    }

    @Test
    public void reset() {
        ChangeLogParserFactory instance1 = ChangeLogParserFactory.getInstance();
        ChangeLogParserFactory.reset();
        Assert.assertFalse((instance1 == (ChangeLogParserFactory.getInstance())));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void builtInGeneratorsAreFound() {
        List parsers = ChangeLogParserFactory.getInstance().getParsers();
        Assert.assertTrue((!(parsers.isEmpty())));
    }

    private static class MockChangeLogParser implements ChangeLogParser {
        @Override
        public DatabaseChangeLog parse(String physicalChangeLogLocation, ChangeLogParameters changeLogParameters, ResourceAccessor resourceAccessor) throws ChangeLogParseException {
            return null;
        }

        @Override
        public boolean supports(String changeLogFile, ResourceAccessor resourceAccessor) {
            return changeLogFile.endsWith(".test");
        }

        @Override
        public int getPriority() {
            return PRIORITY_DEFAULT;
        }
    }
}

