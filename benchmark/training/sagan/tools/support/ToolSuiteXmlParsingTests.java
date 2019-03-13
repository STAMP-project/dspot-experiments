package sagan.tools.support;


import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import sagan.support.Fixtures;
import sagan.tools.Release;


public class ToolSuiteXmlParsingTests {
    private String responseXml = Fixtures.load("/fixtures/tools/sts_downloads.xml");

    @Test
    public void unmarshal() throws Exception {
        XmlMapper serializer = new XmlMapper();
        ToolSuiteXml toolSuiteXml = serializer.readValue(responseXml, ToolSuiteXml.class);
        MatcherAssert.assertThat(toolSuiteXml.getReleases(), Matchers.notNullValue());
        MatcherAssert.assertThat(toolSuiteXml.getReleases().size(), Matchers.equalTo(4));
        Release release = toolSuiteXml.getReleases().get(0);
        MatcherAssert.assertThat(release.getDownloads().size(), Matchers.equalTo(7));
        MatcherAssert.assertThat(release.getWhatsnew(), Matchers.notNullValue());
        MatcherAssert.assertThat(toolSuiteXml.getOthers(), Matchers.notNullValue());
        MatcherAssert.assertThat(toolSuiteXml.getOthers().size(), Matchers.equalTo(43));
        Release oldRelease = toolSuiteXml.getOthers().get(0);
        MatcherAssert.assertThat(oldRelease.getDownloads().size(), Matchers.equalTo(17));
        MatcherAssert.assertThat(oldRelease.getWhatsnew(), Matchers.notNullValue());
    }
}

