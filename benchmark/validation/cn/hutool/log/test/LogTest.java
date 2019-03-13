package cn.hutool.log.test;


import Level.DEBUG;
import Level.INFO;
import Level.WARN;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.junit.Test;


/**
 * ????????
 *
 * @author Looly
 */
public class LogTest {
    @Test
    public void logTest() {
        Log log = LogFactory.get();
        // ????????
        log.debug("This is {} log", DEBUG);
        log.info("This is {} log", INFO);
        log.warn("This is {} log", WARN);
        // Exception e = new Exception("test Exception");
        // log.error(e, "This is {} log", Level.ERROR);
    }
}

