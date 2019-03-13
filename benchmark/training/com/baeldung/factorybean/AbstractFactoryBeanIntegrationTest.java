package com.baeldung.factorybean;


import javax.annotation.Resource;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:factorybean-abstract-spring-ctx.xml" })
public class AbstractFactoryBeanIntegrationTest {
    @Resource(name = "singleTool")
    private Tool tool1;

    @Resource(name = "singleTool")
    private Tool tool2;

    @Resource(name = "nonSingleTool")
    private Tool tool3;

    @Resource(name = "nonSingleTool")
    private Tool tool4;

    @Test
    public void testSingleToolFactory() {
        Assert.assertThat(tool1.getId(), IsEqual.equalTo(1));
        Assert.assertTrue(((tool1) == (tool2)));
    }

    @Test
    public void testNonSingleToolFactory() {
        Assert.assertThat(tool3.getId(), IsEqual.equalTo(2));
        Assert.assertThat(tool4.getId(), IsEqual.equalTo(2));
        Assert.assertTrue(((tool3) != (tool4)));
    }
}

