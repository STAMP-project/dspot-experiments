package com.app.mvc.dao;


import com.app.mvc.shortUrl.ShortUrl;
import com.app.mvc.shortUrl.ShortUrlDao;
import javax.annotation.Resource;
import org.junit.Test;


/**
 * Created by jimin on 16/4/7.
 */
public class ShortUrlDaoTest extends BaseJunitTest {
    @Resource
    private ShortUrlDao shortUrlDao;

    @Test
    public void testSave() {
        ShortUrl shortUrl = ShortUrl.builder().origin("test").current("test").status(1).build();
        shortUrlDao.save(shortUrl);
    }
}

