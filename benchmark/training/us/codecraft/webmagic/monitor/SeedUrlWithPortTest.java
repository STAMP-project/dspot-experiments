package us.codecraft.webmagic.monitor;


import javax.management.JMException;
import org.junit.Test;
import us.codecraft.webmagic.Spider;


/**
 *
 *
 * @author jerry_shenchao@163.com
 */
public class SeedUrlWithPortTest {
    @Test
    public void testSeedUrlWithPort() throws JMException {
        Spider spider = Spider.create(new TempProcessor()).addUrl("http://www.hndpf.org:8889/");
        SpiderMonitor.instance().register(spider);
        spider.run();
    }
}

