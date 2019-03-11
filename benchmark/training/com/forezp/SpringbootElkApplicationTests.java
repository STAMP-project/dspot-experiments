package com.forezp;


import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootElkApplicationTests {
    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void test() throws Exception {
        for (int i = 0; i < 100; i++) {
            logger.info("??info  ");
            logger.debug("??debug+skkkw???kw");
            logger.error("??error  ????");
        }
    }
}

