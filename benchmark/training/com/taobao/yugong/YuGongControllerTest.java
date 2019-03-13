package com.taobao.yugong;


import com.taobao.yugong.controller.YuGongController;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;


/**
 *
 *
 * @author agapple 2014?2?25? ??11:38:06
 * @since 1.0.0
 */
public class YuGongControllerTest {
    @Test
    public void testSimple() throws Exception {
        PropertiesConfiguration config = new PropertiesConfiguration();
        config.load(YuGongLauncher.class.getClassLoader().getResourceAsStream("yugong.properties"));
        YuGongController controller = new YuGongController(config);
        controller.start();
        controller.waitForDone();
        Thread.sleep((3 * 1000));// ??3s??????

        controller.stop();
    }
}

