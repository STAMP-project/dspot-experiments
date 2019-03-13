package org.mockserver.model;


import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import static TemplateType.JAVASCRIPT;
import static TemplateType.VELOCITY;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpTemplateTest {
    @Test
    public void shouldAlwaysCreateNewObject() {
        Assert.assertEquals(new HttpTemplate(JAVASCRIPT).template(TemplateType.JAVASCRIPT), HttpTemplate.template(TemplateType.JAVASCRIPT));
        Assert.assertEquals(new HttpTemplate(VELOCITY).template(TemplateType.VELOCITY), HttpTemplate.template(TemplateType.VELOCITY));
        Assert.assertNotSame(HttpTemplate.template(TemplateType.JAVASCRIPT), HttpTemplate.template(TemplateType.JAVASCRIPT));
        Assert.assertNotSame(HttpTemplate.template(TemplateType.VELOCITY), HttpTemplate.template(TemplateType.VELOCITY));
    }

    @Test
    public void returnsTemplate() {
        Assert.assertEquals("some_template", withTemplate("some_template").getTemplate());
    }

    @Test
    public void returnsTemplateType() {
        Assert.assertEquals(TemplateType.JAVASCRIPT, getTemplateType());
    }

    @Test
    public void returnsDelay() {
        Assert.assertEquals(new Delay(TimeUnit.HOURS, 1), new HttpForward().withDelay(new Delay(TimeUnit.HOURS, 1)).getDelay());
        Assert.assertEquals(new Delay(TimeUnit.HOURS, 1), new HttpForward().withDelay(TimeUnit.HOURS, 1).getDelay());
    }

    @Test
    public void shouldReturnFormattedRequestInToString() {
        TestCase.assertEquals((((((((((((((("{" + (NEW_LINE)) + "  \"delay\" : {") + (NEW_LINE)) + "    \"timeUnit\" : \"HOURS\",") + (NEW_LINE)) + "    \"value\" : 1") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"templateType\" : \"JAVASCRIPT\",") + (NEW_LINE)) + "  \"template\" : \"some_template\"") + (NEW_LINE)) + "}"), withTemplate("some_template").withDelay(TimeUnit.HOURS, 1).toString());
    }
}

