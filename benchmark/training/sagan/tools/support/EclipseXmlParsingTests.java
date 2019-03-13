package sagan.tools.support;


import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import sagan.support.Fixtures;


public class EclipseXmlParsingTests {
    private String responseXml = Fixtures.load("/fixtures/tools/eclipse.xml");

    @Test
    public void unmarshal() throws Exception {
        XmlMapper serializer = new XmlMapper();
        EclipseXml eclipseXml = serializer.readValue(responseXml, EclipseXml.class);
        MatcherAssert.assertThat(eclipseXml.getEclipseXmlProducts(), Matchers.notNullValue());
        MatcherAssert.assertThat(eclipseXml.getEclipseXmlProducts().size(), Matchers.equalTo(6));
        EclipseXmlProduct eclipseXmlProduct = eclipseXml.getEclipseXmlProducts().get(0);
        MatcherAssert.assertThat(eclipseXmlProduct.getName(), Matchers.equalTo("SpringSource Tool Suites Downloads"));
        MatcherAssert.assertThat(eclipseXmlProduct.getPackages().size(), Matchers.equalTo(4));
        MatcherAssert.assertThat(eclipseXmlProduct.getPackages().get(0).getDescription(), Matchers.equalTo("Spring Tool Suite&trade; (STS) provides the best Eclipse-powered development environment for building Spring-based enterprise applications. STS includes tools for all of the latest enterprise Java and Spring based technologies. STS supports application targeting to local, and cloud-based servers and provides built in support for vFabric tc Server. Spring Tool Suite is freely available for development and internal business operations use with no time limits."));
        MatcherAssert.assertThat(eclipseXmlProduct.getPackages().get(0).getEclipseXmlDownloads().get(0).getFile(), Matchers.equalTo("release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-win32-installer.exe"));
    }
}

