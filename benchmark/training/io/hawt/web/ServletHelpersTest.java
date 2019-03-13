package io.hawt.web;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import org.hamcrest.CoreMatchers;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


public class ServletHelpersTest {
    @Test
    public void readObject() throws Exception {
        String data = "{ string: 'text', number: 2, boolean: true }";
        JSONObject json = ServletHelpers.readObject(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data.getBytes()))));
        Assert.assertThat(json.get("string"), CoreMatchers.equalTo("text"));
        Assert.assertThat(json.get("number"), CoreMatchers.equalTo(2));
        Assert.assertThat(json.get("boolean"), CoreMatchers.equalTo(true));
    }
}

