package org.xwiki.xml.internal.html;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.xml.html.HTMLCleanerConfiguration;
import org.xwiki.xml.html.HTMLUtils;
import org.xwiki.xml.html.filter.HTMLFilter;
import org.xwiki.xml.internal.html.filter.AttributeFilter;
import org.xwiki.xml.internal.html.filter.BodyFilter;
import org.xwiki.xml.internal.html.filter.FontFilter;
import org.xwiki.xml.internal.html.filter.LinkFilter;
import org.xwiki.xml.internal.html.filter.ListFilter;
import org.xwiki.xml.internal.html.filter.ListItemFilter;
import org.xwiki.xml.internal.html.filter.UniqueIdFilter;


@ComponentTest
@ComponentList({ ListFilter.class, ListItemFilter.class, FontFilter.class, BodyFilter.class, AttributeFilter.class, UniqueIdFilter.class, DefaultHTMLCleaner.class, LinkFilter.class })
public class AmplDefaultHTMLCleanerTest {
    public static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" " + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");

    private static final String HEADER_FULL = (AmplDefaultHTMLCleanerTest.HEADER) + "<html><head></head><body>";

    private static final String FOOTER = "</body></html>\n";

    @InjectMockComponents
    private DefaultHTMLCleaner cleaner;

    @Test
    public void restrictedHtml() throws Exception {
        HTMLCleanerConfiguration configuration = this.cleaner.getDefaultConfiguration();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.putAll(configuration.getParameters());
        String o_restrictedHtml__7 = parameters.put("restricted", "true");
        Assertions.assertNull(o_restrictedHtml__7);
        configuration.setParameters(parameters);
        String result = HTMLUtils.toString(this.cleaner.clean(new StringReader("<script>alert(\"foo\")</script>"), configuration));
        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>alert(\"foo\")</pre></body></html>\n", result);
        String String_6 = ((AmplDefaultHTMLCleanerTest.HEADER_FULL) + "<pre>alert(\"foo\")</pre>") + (AmplDefaultHTMLCleanerTest.FOOTER);
        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>alert(\"foo\")</pre></body></html>\n", String_6);
        result = HTMLUtils.toString(this.cleaner.clean(new StringReader("<style>p {color:white;}</style>"), configuration));
        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>p {color:white;}</pre></body></html>\n", result);
        String String_7 = ((AmplDefaultHTMLCleanerTest.HEADER_FULL) + "<pre>p {color:white;}</pre>") + (AmplDefaultHTMLCleanerTest.FOOTER);
        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>p {color:white;}</pre></body></html>\n", String_7);
        Assertions.assertNull(o_restrictedHtml__7);
        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>p {color:white;}</pre></body></html>\n", result);
        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>alert(\"foo\")</pre></body></html>\n", String_6);
        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>p {color:white;}</pre></body></html>\n", result);
    }

    @Test
    public void duplicateIds(ComponentManager componentManager) throws Exception {
        String actual = "<p id=\"x\">1</p><p id=\"xy\">2</p><p id=\"x\">3</p>";
        String expected = "<p id=\"x\">1</p><p id=\"xy\">2</p><p id=\"x0\">3</p>";
        HTMLCleanerConfiguration config = this.cleaner.getDefaultConfiguration();
        List<HTMLFilter> filters = new ArrayList<>(config.getFilters());
        filters.add(componentManager.getInstance(HTMLFilter.class, "uniqueId"));
        config.setFilters(filters);
        Assertions.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER_FULL) + expected) + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.cleaner.clean(new StringReader(actual), config)));
    }

    @Test
    @Disabled("See https://jira.xwiki.org/browse/XWIKI-9753")
    public void cleanTitleWithNamespace() {
        String input = "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">\n" + (((((((((((("  <head>\n" + "    <title>Title test</title>\n") + "  </head>\n") + "  <body>\n") + "    <p>before</p>\n") + "    <svg xmlns=\"http://www.w3.org/2000/svg\" height=\"300\" width=\"500\">\n") + "      <g>\n") + "        <title>SVG Title Demo example</title>\n") + "        <rect height=\"50\" style=\"fill:none; stroke:blue; stroke-width:1px\" width=\"200\" x=\"10\" ") + "y=\"10\"></rect>\n") + "      </g>\n") + "    </svg>\n") + "    <p>after</p>\n");
        Assertions.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER) + input) + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.cleaner.clean(new StringReader(input))));
    }

    @Test
    @Disabled("See https://sourceforge.net/p/htmlcleaner/bugs/168/")
    public void cleanHTMLTagWithNamespace() {
        String input = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head></head><body>";
        Assertions.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER) + input) + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.cleaner.clean(new StringReader(input))));
        HTMLCleanerConfiguration config = this.cleaner.getDefaultConfiguration();
        config.setParameters(Collections.singletonMap(HTMLCleanerConfiguration.NAMESPACES_AWARE, "false"));
        Assertions.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER) + "<html><head></head><body>") + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.cleaner.clean(new StringReader(input), config)));
    }

    private void assertHTML(String expected, String actual) {
        Assertions.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER_FULL) + expected) + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.cleaner.clean(new StringReader(actual))));
    }

    private void assertHTMLWithHeadContent(String expected, String actual) {
        Assertions.assertEquals((((((AmplDefaultHTMLCleanerTest.HEADER) + "<html><head>") + expected) + "</head><body>") + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.cleaner.clean(new StringReader(actual))));
    }
}

