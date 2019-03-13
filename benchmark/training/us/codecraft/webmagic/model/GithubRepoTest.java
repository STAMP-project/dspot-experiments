package us.codecraft.webmagic.model;


import org.junit.Test;


/**
 *
 *
 * @author code4crafter@gmail.com <br>
 */
public class GithubRepoTest {
    @Test
    public void test() {
        OOSpider.create(us.codecraft.webmagic.Site.me().setSleepTime(0), new us.codecraft.webmagic.pipeline.PageModelPipeline<GithubRepo>() {
            @Override
            public void process(GithubRepo o, us.codecraft.webmagic.Task task) {
                assertThat(o.getStar()).isEqualTo(86);
                assertThat(o.getFork()).isEqualTo(70);
            }
        }, GithubRepo.class).addUrl("https://github.com/code4craft/webmagic").setDownloader(new us.codecraft.webmagic.downloader.MockGithubDownloader()).test("https://github.com/code4craft/webmagic");
    }
}

