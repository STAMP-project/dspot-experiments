package com.baidu.disconf.web.test.service.config.dao;


import com.baidu.disconf.web.service.config.bo.Config;
import com.baidu.disconf.web.service.config.dao.ConfigDao;
import com.baidu.disconf.web.test.common.BaseTestCase;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 *
 * @author liaoqiqi
 * @version 2014-6-17
 */
public class ConfigDaoTestCase extends BaseTestCase {
    protected static final Logger LOG = LoggerFactory.getLogger(ConfigDaoTestCase.class);

    @Autowired
    private ConfigDao configDao;

    @Test
    public void test() {
        URL url = ClassLoader.getSystemResource("file2/confA.properties");
        byte[] btyes = readFileContent(url.getPath());
        try {
            // read data
            String str = new String(btyes, "UTF-8");
            // save to db
            Config config = configDao.get(1L);
            config.setValue(str);
            configDao.update(config);
            // read
            ConfigDaoTestCase.LOG.info(configDao.get(1L).getValue());
        } catch (UnsupportedEncodingException e) {
            Assert.assertTrue(false);
        }
    }
}

