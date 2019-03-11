package com.baeldung.serenity.spring;


import com.baeldung.serenity.spring.steps.AdderRestSteps;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author aiet
 */
@RunWith(SerenityRunner.class)
public class AdderMockMvcIntegrationTest {
    @Steps
    AdderRestSteps steps;

    @Test
    public void givenNumber_whenAdd_thenSummedUp() throws Exception {
        steps.givenCurrentNumber();
        steps.whenAddNumber(RandomNumberUtil.randomInt());
        steps.thenSummedUp();
    }
}

