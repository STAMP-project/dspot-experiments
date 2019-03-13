package org.mockserver.serialization.java;


import HttpTemplate.TemplateType;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Delay;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpResponseTemplateToJavaSerializerTest {
    @Test
    public void shouldSerializeFullObjectWithCallbackAsJava() throws IOException {
        Assert.assertEquals(((((((((NEW_LINE) + "        template(HttpTemplate.TemplateType.JAVASCRIPT)") + (NEW_LINE)) + "                .withTemplate(\"") + (StringEscapeUtils.escapeJava((((((((((((((((((((("if (request.method === 'POST' && request.path === '/somePath') {" + (NEW_LINE)) + "    return {") + (NEW_LINE)) + "        'statusCode': 200,") + (NEW_LINE)) + "        'body': JSON.stringify({name: 'value'})") + (NEW_LINE)) + "    };") + (NEW_LINE)) + "} else {") + (NEW_LINE)) + "    return {") + (NEW_LINE)) + "        'statusCode': 406,") + (NEW_LINE)) + "        'body': request.body") + (NEW_LINE)) + "    };") + (NEW_LINE)) + "}")))) + "\")") + (NEW_LINE)) + "                .withDelay(new Delay(TimeUnit.SECONDS, 5))"), new HttpTemplateToJavaSerializer().serialize(1, new org.mockserver.model.HttpTemplate(TemplateType.JAVASCRIPT).withTemplate((((((((((((((((((((("if (request.method === 'POST' && request.path === '/somePath') {" + (NEW_LINE)) + "    return {") + (NEW_LINE)) + "        'statusCode': 200,") + (NEW_LINE)) + "        'body': JSON.stringify({name: 'value'})") + (NEW_LINE)) + "    };") + (NEW_LINE)) + "} else {") + (NEW_LINE)) + "    return {") + (NEW_LINE)) + "        'statusCode': 406,") + (NEW_LINE)) + "        'body': request.body") + (NEW_LINE)) + "    };") + (NEW_LINE)) + "}")).withDelay(new Delay(TimeUnit.SECONDS, 5))));
    }
}

