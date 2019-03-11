package us.codecraft.webmagic.processor;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;


/**
 *
 *
 * @author code4crafter@gmail.com
 */
public class GithubRepoProcessor implements PageProcessor {
    @Test
    public void test() {
        us.codecraft.webmagic.model.OOSpider.create(new GithubRepoProcessor()).addPipeline(new us.codecraft.webmagic.pipeline.Pipeline() {
            @Override
            public void process(ResultItems resultItems, Task task) {
                assertEquals("78", ((String) (resultItems.get("star"))).trim());
                assertEquals("65", ((String) (resultItems.get("fork"))).trim());
            }
        }).setDownloader(new us.codecraft.webmagic.downloader.MockGithubDownloader()).test("https://github.com/code4craft/webmagic");
    }
}

