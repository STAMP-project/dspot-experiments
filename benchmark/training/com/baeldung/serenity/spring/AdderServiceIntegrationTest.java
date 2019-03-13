package com.baeldung.serenity.spring;


import com.baeldung.serenity.spring.steps.AdderServiceSteps;
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
public class AdderServiceIntegrationTest {
    @Steps
    private AdderServiceSteps adderServiceSteps;

    @Test
    public void givenNumber_whenAdd_thenSummedUp() {
        adderServiceSteps.givenBaseAndAdder(RandomNumberUtil.randomInt(), RandomNumberUtil.randomInt());
        adderServiceSteps.whenAdd();
        adderServiceSteps.summedUp();
    }
}

