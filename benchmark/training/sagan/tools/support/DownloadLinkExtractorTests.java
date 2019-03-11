package sagan.tools.support;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import sagan.tools.Download;


public class DownloadLinkExtractorTests {
    private Download download;

    private DownloadLinkExtractor extractor;

    @Test
    public void extractsUrl() throws Exception {
        MatcherAssert.assertThat(extractor.createDownloadLink(download).getUrl(), Matchers.equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.dmg"));
    }

    @Test
    public void extractsFileSize() throws Exception {
        MatcherAssert.assertThat(extractor.createDownloadLink(download).getFileSize(), Matchers.equalTo("373MB"));
    }

    @Test
    public void extractsSimpleFileType() throws Exception {
        MatcherAssert.assertThat(extractor.createDownloadLink(download).getFileType(), Matchers.equalTo("dmg"));
    }

    @Test
    public void extractsTarGzFileType() throws Exception {
        download.setFile("release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.tar.gz");
        MatcherAssert.assertThat(extractor.createDownloadLink(download).getFileType(), Matchers.equalTo("tar.gz"));
    }

    @Test
    public void extractsOs() throws Exception {
        MatcherAssert.assertThat(extractor.createDownloadLink(download).getOs(), Matchers.equalTo("mac"));
    }

    @Test
    public void extractsArchitecture() throws Exception {
        MatcherAssert.assertThat(extractor.createDownloadLink(download).getArchitecture(), Matchers.equalTo("32"));
    }
}

