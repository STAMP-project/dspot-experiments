package com.shekhargulati.codegolf;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class AcronymMakerTest {
    @Test
    public void acronymOfUnitedStatesOfAmericaIsUSA() throws Exception {
        final String input = "United States of America";
        final String acronym = AcronymMaker.acronym(input);
        Assert.assertThat(acronym, CoreMatchers.equalTo("USA"));
    }

    @Test
    public void acronymOfUnitedStatesOfAmericaIsUSA_lowercase() throws Exception {
        final String input = "united states of america";
        final String acronym = AcronymMaker.acronym(input);
        Assert.assertThat(acronym, CoreMatchers.equalTo("USA"));
    }

    @Test
    public void acronymOfLightAmplificationByStimulationOfEmittedRadiationIsLASER() throws Exception {
        final String input = "Light Amplification by Stimulation of Emitted Radiation";
        final String acronym = AcronymMaker.acronym(input);
        Assert.assertThat(acronym, CoreMatchers.equalTo("LASER"));
    }

    @Test
    public void acronymOfJordanOfTheWorldIsJTW() throws Exception {
        final String input = "Jordan Of the World";
        final String acronym = AcronymMaker.acronym(input);
        Assert.assertThat(acronym, CoreMatchers.equalTo("JTW"));
    }
}

