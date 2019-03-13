package com.baeldung.test;


import com.baeldung.spring.configuration.AppConfig;
import com.baeldung.spring.service.SpringSuperService;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
public class SpringInterceptorIntegrationTest {
    @Autowired
    SpringSuperService springSuperService;

    @Autowired
    private List<String> accumulator;

    // 
    @Test
    public void givenService_whenServiceAndAspectExecuted_thenOk() {
        String code = "123456";
        String result = springSuperService.getInfoFromService(code);
        Assert.assertThat(accumulator.size(), CoreMatchers.is(2));
        Assert.assertThat(accumulator.get(0), CoreMatchers.is("Call to getInfoFromService"));
        Assert.assertThat(accumulator.get(1), CoreMatchers.is("Method called successfully: getInfoFromService"));
    }
}

