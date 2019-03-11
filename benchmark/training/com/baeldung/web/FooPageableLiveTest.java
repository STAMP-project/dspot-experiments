package com.baeldung.web;


import com.baeldung.common.web.AbstractBasicLiveTest;
import com.baeldung.persistence.model.Foo;
import com.baeldung.spring.ConfigIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ConfigIntegrationTest.class }, loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class FooPageableLiveTest extends AbstractBasicLiveTest<Foo> {
    public FooPageableLiveTest() {
        super(Foo.class);
    }

    @Override
    @Test
    public void whenResourcesAreRetrievedPaged_then200IsReceived() {
        this.create();
        final Response response = RestAssured.get(((getPageableURL()) + "?page=0&size=10"));
        Assert.assertThat(response.getStatusCode(), Matchers.is(200));
    }

    @Override
    @Test
    public void whenPageOfResourcesAreRetrievedOutOfBounds_then404IsReceived() {
        final String url = (((getPageableURL()) + "?page=") + (RandomStringUtils.randomNumeric(5))) + "&size=10";
        final Response response = RestAssured.get(url);
        Assert.assertThat(response.getStatusCode(), Matchers.is(404));
    }

    @Override
    @Test
    public void givenResourcesExist_whenFirstPageIsRetrieved_thenPageContainsResources() {
        create();
        final Response response = RestAssured.get(((getPageableURL()) + "?page=0&size=10"));
        Assert.assertFalse(response.body().as(List.class).isEmpty());
    }
}

