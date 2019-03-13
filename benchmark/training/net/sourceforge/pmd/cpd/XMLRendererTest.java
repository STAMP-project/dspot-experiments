/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import net.sourceforge.pmd.cpd.renderer.CPDRenderer;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *
 *
 * @author Philippe T'Seyen
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 */
public class XMLRendererTest {
    private static final String ENCODING = ((String) (System.getProperties().get("file.encoding")));

    @Test
    public void testWithNoDuplication() throws IOException {
        CPDRenderer renderer = new XMLRenderer();
        List<Match> list = new ArrayList<>();
        StringWriter sw = new StringWriter();
        renderer.render(list.iterator(), sw);
        String report = sw.toString();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(report.getBytes(XMLRendererTest.ENCODING)));
            NodeList nodes = doc.getChildNodes();
            Node n = nodes.item(0);
            Assert.assertEquals("pmd-cpd", n.getNodeName());
            Assert.assertEquals(0, doc.getElementsByTagName("duplication").getLength());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testWithOneDuplication() throws IOException {
        CPDRenderer renderer = new XMLRenderer();
        List<Match> list = new ArrayList<>();
        int lineCount = 6;
        String codeFragment = "code\nfragment";
        Mark mark1 = createMark("public", "/var/Foo.java", 1, lineCount, codeFragment);
        Mark mark2 = createMark("stuff", "/var/Foo.java", 73, lineCount, codeFragment);
        Match match = new Match(75, mark1, mark2);
        list.add(match);
        StringWriter sw = new StringWriter();
        renderer.render(list.iterator(), sw);
        String report = sw.toString();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(report.getBytes(XMLRendererTest.ENCODING)));
            NodeList dupes = doc.getElementsByTagName("duplication");
            Assert.assertEquals(1, dupes.getLength());
            Node file = dupes.item(0).getFirstChild();
            while ((file != null) && ((file.getNodeType()) != (Node.ELEMENT_NODE))) {
                file = file.getNextSibling();
            } 
            if (file != null) {
                Assert.assertEquals("1", file.getAttributes().getNamedItem("line").getNodeValue());
                Assert.assertEquals("/var/Foo.java", file.getAttributes().getNamedItem("path").getNodeValue());
                file = file.getNextSibling();
                while ((file != null) && ((file.getNodeType()) != (Node.ELEMENT_NODE))) {
                    file = file.getNextSibling();
                } 
            }
            if (file != null) {
                Assert.assertEquals("73", file.getAttributes().getNamedItem("line").getNodeValue());
            }
            Assert.assertEquals(1, doc.getElementsByTagName("codefragment").getLength());
            Assert.assertEquals(codeFragment, doc.getElementsByTagName("codefragment").item(0).getTextContent());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testRenderWithMultipleMatch() throws IOException {
        CPDRenderer renderer = new XMLRenderer();
        List<Match> list = new ArrayList<>();
        int lineCount1 = 6;
        String codeFragment1 = "code fragment";
        Mark mark1 = createMark("public", "/var/Foo.java", 48, lineCount1, codeFragment1);
        Mark mark2 = createMark("void", "/var/Foo.java", 73, lineCount1, codeFragment1);
        Match match1 = new Match(75, mark1, mark2);
        int lineCount2 = 7;
        String codeFragment2 = "code fragment 2";
        Mark mark3 = createMark("void", "/var/Foo2.java", 49, lineCount2, codeFragment2);
        Mark mark4 = createMark("stuff", "/var/Foo2.java", 74, lineCount2, codeFragment2);
        Match match2 = new Match(76, mark3, mark4);
        list.add(match1);
        list.add(match2);
        StringWriter sw = new StringWriter();
        renderer.render(list.iterator(), sw);
        String report = sw.toString();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(report.getBytes(XMLRendererTest.ENCODING)));
            Assert.assertEquals(2, doc.getElementsByTagName("duplication").getLength());
            Assert.assertEquals(4, doc.getElementsByTagName("file").getLength());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testRendererEncodedPath() throws IOException {
        CPDRenderer renderer = new XMLRenderer();
        List<Match> list = new ArrayList<>();
        final String espaceChar = "&lt;";
        Mark mark1 = createMark("public", (("/var/F" + '<') + "oo.java"), 48, 6, "code fragment");
        Mark mark2 = createMark("void", "/var/F<oo.java", 73, 6, "code fragment");
        Match match1 = new Match(75, mark1, mark2);
        list.add(match1);
        StringWriter sw = new StringWriter();
        renderer.render(list.iterator(), sw);
        String report = sw.toString();
        Assert.assertTrue(report.contains(espaceChar));
    }
}

