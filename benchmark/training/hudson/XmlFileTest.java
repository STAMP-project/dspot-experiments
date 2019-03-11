package hudson;


import hudson.model.Node;
import hudson.util.XStream2;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import jenkins.model.Jenkins;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class XmlFileTest {
    @Test
    public void canReadXml1_0Test() throws IOException {
        URL configUrl = getClass().getResource("/hudson/config_1_0.xml");
        XStream2 xs = new XStream2();
        xs.alias("hudson", Jenkins.class);
        XmlFile xmlFile = new XmlFile(xs, new File(configUrl.getFile()));
        if (xmlFile.exists()) {
            Node n = ((Node) (xmlFile.read()));
            Assert.assertThat(n.getNumExecutors(), CoreMatchers.is(2));
            Assert.assertThat(n.getMode().toString(), CoreMatchers.is("NORMAL"));
        }
    }

    @Test
    public void canReadXml1_1Test() throws IOException {
        URL configUrl = getClass().getResource("/hudson/config_1_1.xml");
        XStream2 xs = new XStream2();
        xs.alias("hudson", Jenkins.class);
        XmlFile xmlFile = new XmlFile(xs, new File(configUrl.getFile()));
        if (xmlFile.exists()) {
            Node n = ((Node) (xmlFile.read()));
            Assert.assertThat(n.getNumExecutors(), CoreMatchers.is(2));
            Assert.assertThat(n.getMode().toString(), CoreMatchers.is("NORMAL"));
        }
    }

    @Test
    public void canReadXmlWithControlCharsTest() throws IOException {
        URL configUrl = getClass().getResource("/hudson/config_1_1_with_special_chars.xml");
        XStream2 xs = new XStream2();
        xs.alias("hudson", Jenkins.class);
        XmlFile xmlFile = new XmlFile(xs, new File(configUrl.getFile()));
        if (xmlFile.exists()) {
            Node n = ((Node) (xmlFile.read()));
            Assert.assertThat(n.getNumExecutors(), CoreMatchers.is(2));
            Assert.assertThat(n.getMode().toString(), CoreMatchers.is("NORMAL"));
            Assert.assertThat(n.getLabelString(), CoreMatchers.is("LESS_TERMCAP_mb=\u001b[01;31m"));
        }
    }
}

