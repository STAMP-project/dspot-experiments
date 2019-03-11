package liquibase.parser;


import java.util.List;
import liquibase.parser.core.sql.SqlChangeLogParser;
import liquibase.parser.core.xml.XMLChangeLogSAXParser;
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
        ChangeLogParserFactory.getInstance().register(new MockChangeLogParser("mock"));
        Assert.assertEquals(1, ChangeLogParserFactory.getInstance().getParsers().size());
    }

    @Test
    public void unregister_instance() {
        ChangeLogParserFactory factory = ChangeLogParserFactory.getInstance();
        factory.getParsers().clear();
        Assert.assertEquals(0, factory.getParsers().size());
        XMLChangeLogSAXParser changeLogParser = new XMLChangeLogSAXParser();
        factory.register(new SqlChangeLogParser());
        factory.register(changeLogParser);
        Assert.assertEquals(2, factory.getParsers().size());
        factory.unregister(changeLogParser);
        Assert.assertEquals(1, factory.getParsers().size());
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
        List<ChangeLogParser> generators = ChangeLogParserFactory.getInstance().getParsers();
        Assert.assertEquals(5, generators.size());
    }

    @Test
    public void getParsers() throws Exception {
        ChangeLogParser parser = ChangeLogParserFactory.getInstance().getParser("asdf.xml", new JUnitResourceAccessor());
        Assert.assertNotNull(parser);
        Assert.assertTrue((parser instanceof XMLChangeLogSAXParser));
    }

    @Test
    public void getExtensionParser() throws Exception {
        ChangeLogParserFactory parserFactory = ChangeLogParserFactory.getInstance();
        ChangeLogParser defaultParser = parserFactory.getParser("asdf.xml", new JUnitResourceAccessor());
        Assert.assertNotNull(defaultParser);
        Assert.assertTrue((defaultParser instanceof XMLChangeLogSAXParser));
        ChangeLogParser otherXmlParser = new XMLChangeLogSAXParser() {
            @Override
            public int getPriority() {
                return 100;
            }
        };
        parserFactory.register(otherXmlParser);
        try {
            Assert.assertTrue((otherXmlParser == (parserFactory.getParser("asdf.xml", new JUnitResourceAccessor()))));
            Assert.assertFalse((defaultParser == (parserFactory.getParser("asdf.xml", new JUnitResourceAccessor()))));
        } finally {
            parserFactory.unregister(otherXmlParser);
        }
    }
}

