package com.baeldung.factorybean;


import javax.annotation.Resource;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = FactoryBeanAppConfig.class)
public class FactoryBeanJavaConfigIntegrationTest {
    @Autowired
    private Tool tool;

    @Resource(name = "&tool")
    private ToolFactory toolFactory;

    @Test
    public void testConstructWorkerByJava() {
        Assert.assertThat(tool.getId(), IsEqual.equalTo(2));
        Assert.assertThat(toolFactory.getFactoryId(), IsEqual.equalTo(7070));
    }
}

