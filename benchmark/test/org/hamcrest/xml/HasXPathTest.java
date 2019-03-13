package org.hamcrest.xml;


import java.util.HashSet;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 *
 *
 * @author Joe Walnes
 * @author Tom Denley
 */
public final class HasXPathTest {
    private final Document xml = HasXPathTest.parse(("" + (((((("<root type=\'food\'>\n" + "  <something id=\'a\'><cheese>Edam</cheese></something>\n") + "  <something id=\'b\'><cheese>Cheddar</cheese></something>\n") + "  <f:foreignSomething xmlns:f=\"http://cheese.com\" milk=\"camel\">Caravane</f:foreignSomething>\n") + "  <emptySomething />\n") + "  <f:emptySomething xmlns:f=\"http://cheese.com\" />") + "</root>\n")));

    private final NamespaceContext ns = new NamespaceContext() {
        @Override
        public String getNamespaceURI(String prefix) {
            return "cheese".equals(prefix) ? "http://cheese.com" : null;
        }

        @Override
        public String getPrefix(String namespaceURI) {
            return "http://cheese.com".equals(namespaceURI) ? "cheese" : null;
        }

        @Override
        public Iterator<String> getPrefixes(String namespaceURI) {
            HashSet<String> prefixes = new HashSet<String>();
            String prefix = getPrefix(namespaceURI);
            if (prefix != null) {
                prefixes.add(prefix);
            }
            return prefixes.iterator();
        }
    };

    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<Node> matcher = HasXPath.hasXPath("//irrelevant");
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void appliesMatcherToXPathInDocument() {
        AbstractMatcherTest.assertMatches(HasXPath.hasXPath("/root/something[2]/cheese", IsEqual.equalTo("Cheddar")), xml);
        AbstractMatcherTest.assertMatches(HasXPath.hasXPath("//something[1]/cheese", StringContains.containsString("dam")), xml);
        AbstractMatcherTest.assertMatches(HasXPath.hasXPath("//something[2]/cheese", IsNot.not(StringContains.containsString("dam"))), xml);
        AbstractMatcherTest.assertMatches(HasXPath.hasXPath("/root/@type", IsEqual.equalTo("food")), xml);
        AbstractMatcherTest.assertMatches(HasXPath.hasXPath("//something[@id='b']/cheese", IsEqual.equalTo("Cheddar")), xml);
        AbstractMatcherTest.assertMatches(HasXPath.hasXPath("//something[@id='b']/cheese"), xml);
    }

    @Test
    public void matchesEmptyElement() {
        AbstractMatcherTest.assertMatches(HasXPath.hasXPath("//emptySomething"), xml);
    }

    @Test
    public void matchesEmptyElementInNamespace() {
        AbstractMatcherTest.assertMatches(HasXPath.hasXPath("//cheese:emptySomething", ns), xml);
    }

    @Test
    public void failsIfNodeIsMissing() {
        AbstractMatcherTest.assertDoesNotMatch(HasXPath.hasXPath("/root/something[3]/cheese", ns, IsEqual.equalTo("Cheddar")), xml);
        AbstractMatcherTest.assertDoesNotMatch(HasXPath.hasXPath("//something[@id='c']/cheese", ns), xml);
    }

    @Test
    public void failsIfNodeIsMissingInNamespace() {
        AbstractMatcherTest.assertDoesNotMatch(HasXPath.hasXPath("//cheese:foreignSomething", IsEqual.equalTo("Badger")), xml);
        AbstractMatcherTest.assertDoesNotMatch(HasXPath.hasXPath("//cheese:foreignSomething"), xml);
    }

    @Test
    public void matchesWithNamespace() {
        AbstractMatcherTest.assertMatches(HasXPath.hasXPath("//cheese:foreignSomething", ns), xml);
        AbstractMatcherTest.assertMatches(HasXPath.hasXPath("//cheese:foreignSomething/@milk", ns, IsEqual.equalTo("camel")), xml);
        AbstractMatcherTest.assertMatches(HasXPath.hasXPath("//cheese:foreignSomething/text()", ns, IsEqual.equalTo("Caravane")), xml);
    }

    @Test
    public void throwsIllegalArgumentExceptionIfGivenIllegalExpression() {
        try {
            HasXPath.hasXPath("\\g:dfgd::DSgf/root/something[2]/cheese", IsEqual.equalTo("blah"));
            Assert.fail("Expected exception");
        } catch (IllegalArgumentException expectedException) {
            // expected exception
        }
    }

    @Test
    public void describesItself() {
        AbstractMatcherTest.assertDescription("an XML document with XPath /some/path \"Cheddar\"", HasXPath.hasXPath("/some/path", IsEqual.equalTo("Cheddar")));
        AbstractMatcherTest.assertDescription("an XML document with XPath /some/path", HasXPath.hasXPath("/some/path"));
    }

    @Test
    public void describesMissingNodeMismatch() {
        AbstractMatcherTest.assertMismatchDescription("xpath returned no results.", HasXPath.hasXPath("//honky"), xml);
    }

    @Test
    public void describesIncorrectNodeValueMismatch() {
        AbstractMatcherTest.assertMismatchDescription("was \"Edam\"", HasXPath.hasXPath("//something[1]/cheese", IsEqual.equalTo("parmesan")), xml);
    }
}

