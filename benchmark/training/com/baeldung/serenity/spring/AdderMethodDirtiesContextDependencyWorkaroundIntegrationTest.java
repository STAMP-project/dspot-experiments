package com.baeldung.serenity.spring;


import com.baeldung.serenity.spring.steps.AdderConstructorDependencySteps;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.junit.spring.integration.SpringIntegrationMethodRule;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;


/**
 *
 *
 * @author aiet
 */
@RunWith(SerenityRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(classes = AdderService.class)
public class AdderMethodDirtiesContextDependencyWorkaroundIntegrationTest {
    private AdderConstructorDependencySteps adderSteps;

    @Autowired
    private AdderService adderService;

    @Test
    public void _1_givenNumber_whenAdd_thenSumWrong() {
        adderSteps.whenAdd();
        adderSteps.summedUp();
    }

    @Rule
    public SpringIntegrationMethodRule springIntegration = new SpringIntegrationMethodRule();

    @DirtiesContext
    @Test
    public void _0_givenNumber_whenAddAndAccumulate_thenSummedUp() {
        adderSteps.givenBaseAndAdder(RandomNumberUtil.randomInt(), RandomNumberUtil.randomInt());
        adderSteps.whenAccumulate();
        adderSteps.summedUp();
        adderSteps.whenAdd();
        adderSteps.sumWrong();
    }
}

