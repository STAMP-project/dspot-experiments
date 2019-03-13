package org.mockserver.serialization.model;


import HttpTemplate.TemplateType;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.Delay;
import org.mockserver.model.HttpTemplate;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpTemplateDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // given
        HttpTemplate.TemplateType type = TemplateType.VELOCITY;
        HttpTemplate httpTemplate = new HttpTemplate(type);
        // when
        HttpTemplateDTO httpTemplateDTO = new HttpTemplateDTO(httpTemplate);
        // then
        MatcherAssert.assertThat(httpTemplateDTO.getTemplateType(), Is.is(type));
    }

    @Test
    public void shouldBuildObject() {
        // given
        String template = "some_random_template";
        HttpTemplate httpTemplate = withTemplate(template).withDelay(TimeUnit.SECONDS, 5);
        // when
        HttpTemplate builtHttpTemplate = buildObject();
        // then
        MatcherAssert.assertThat(builtHttpTemplate.getTemplate(), Is.is(template));
        MatcherAssert.assertThat(builtHttpTemplate.getDelay(), Is.is(Delay.delay(TimeUnit.SECONDS, 5)));
    }

    @Test
    public void shouldReturnValuesSetInSetter() {
        // given
        String template = "some_random_template";
        HttpTemplate httpTemplate = new HttpTemplate(TemplateType.VELOCITY);
        // when
        HttpTemplateDTO httpTemplateDTO = new HttpTemplateDTO(httpTemplate);
        httpTemplateDTO.setTemplate(template);
        httpTemplateDTO.setDelay(new DelayDTO(Delay.delay(TimeUnit.SECONDS, 5)));
        // then
        MatcherAssert.assertThat(httpTemplateDTO.getTemplate(), Is.is(template));
    }

    @Test
    public void shouldHandleNullObjectInput() {
        // when
        HttpTemplateDTO httpTemplateDTO = new HttpTemplateDTO(null);
        // then
        MatcherAssert.assertThat(httpTemplateDTO.getTemplate(), Is.is(CoreMatchers.nullValue()));
    }
}

