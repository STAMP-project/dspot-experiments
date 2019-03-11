package us.codecraft.webmagic.downloader;


import java.net.URI;
import org.junit.Test;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.utils.UrlUtils;


/**
 *
 *
 * @author code4crafter@gmail.com
Date: 2017/7/22
Time: ??5:29
 */
public class HttpUriRequestConverterTest {
    @Test
    public void test_illegal_uri_correct() throws Exception {
        HttpUriRequestConverter httpUriRequestConverter = new HttpUriRequestConverter();
        HttpClientRequestContext requestContext = httpUriRequestConverter.convert(new us.codecraft.webmagic.Request(UrlUtils.fixIllegalCharacterInUrl("http://bj.zhongkao.com/beikao/yimo/##")), Site.me(), null);
        assertThat(requestContext.getHttpUriRequest().getURI()).isEqualTo(new URI("http://bj.zhongkao.com/beikao/yimo/#"));
    }
}

