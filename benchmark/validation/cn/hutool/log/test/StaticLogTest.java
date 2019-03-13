package cn.hutool.log.test;


import cn.hutool.log.StaticLog;
import org.junit.Test;


public class StaticLogTest {
    @Test
    public void test() {
        StaticLog.debug("This is static {} log", "debug");
        StaticLog.info("This is static {} log", "info");
    }
}

