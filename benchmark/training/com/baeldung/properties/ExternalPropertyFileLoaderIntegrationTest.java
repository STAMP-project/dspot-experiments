package com.baeldung.properties;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ExternalPropertyFileLoader.class)
public class ExternalPropertyFileLoaderIntegrationTest {
    @Autowired
    ConfProperties props;

    @Test
    public void whenExternalisedPropertiesLoaded_thenReadValues() throws IOException {
        Assert.assertEquals("jdbc:postgresql://localhost:5432/", props.getUrl());
        Assert.assertEquals("admin", props.getUsername());
        Assert.assertEquals("root", props.getPassword());
    }
}

