package org.hswebframework.web.service;


import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author zhouhao
 * @since 3.0
 */
@SpringBootTest(classes = SpringTestApplication.class)
@RunWith(SpringRunner.class)
@Component
public class ModifyEventTests {
    @Autowired
    private TestModifyEntityService modifyEntityService;

    private AtomicInteger counter = new AtomicInteger();

    @Test
    public void modifyTest() {
        TestModifyEntity entity = new TestModifyEntity();
        setId("testId");
        setAge(((byte) (10)));
        updateByPk("test", entity);
        Assert.assertTrue(((TestModifyEntityService.eventCounter.get()) != 0));
    }
}

