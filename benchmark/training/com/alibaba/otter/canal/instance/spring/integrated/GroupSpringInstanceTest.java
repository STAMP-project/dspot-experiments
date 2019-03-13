package com.alibaba.otter.canal.instance.spring.integrated;


import com.alibaba.otter.canal.instance.core.CanalInstance;
import com.alibaba.otter.canal.instance.core.CanalInstanceGenerator;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;


/**
 *
 *
 * @author zebin.xuzb @ 2012-7-13
 * @version 1.0.0
 */
public class GroupSpringInstanceTest {
    private ApplicationContext context;

    @Test
    public void testInstance() {
        CanalInstanceGenerator generator = ((CanalInstanceGenerator) (context.getBean("canalInstanceGenerator")));
        CanalInstance canalInstance = generator.generate("instance");
        Assert.notNull(canalInstance);
        canalInstance.start();
        try {
            Thread.sleep((10 * 1000));
        } catch (InterruptedException e) {
        }
        canalInstance.stop();
    }
}

