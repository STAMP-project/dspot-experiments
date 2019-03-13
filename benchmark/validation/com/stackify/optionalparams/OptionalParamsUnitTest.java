package com.stackify.optionalparams;


import MultiVitaminOverloading.DEFAULT_IRON_AMOUNT;
import MultiVitaminStaticFactoryMethods.CALCIUM_AMT_DEF;
import MultiVitaminStaticFactoryMethods.CALCIUM_AMT_WOMEN;
import MultiVitaminStaticFactoryMethods.IRON_AMT_DEF;
import MultiVitaminStaticFactoryMethods.IRON_AMT_MEN;
import org.junit.Test;


public class OptionalParamsUnitTest {
    @Test
    public void whenCreateMultiVitaminWithOverloading_thenOk() {
        MultiVitaminOverloading multiVitamin = new MultiVitaminOverloading("Default Multivitamin");
        assertThat(multiVitamin.getName()).isEqualTo("Default Multivitamin");
        assertThat(multiVitamin.getVitaminA()).isEqualTo(0);
        assertThat(multiVitamin.getVitaminC()).isEqualTo(0);
        assertThat(multiVitamin.getCalcium()).isEqualTo(0);
        assertThat(multiVitamin.getIron()).isEqualTo(DEFAULT_IRON_AMOUNT);
    }

    @Test
    public void whenCreateMultiVitaminWithStaticFactoryMethods_thenOk() {
        MultiVitaminStaticFactoryMethods mensMultiVitamin = MultiVitaminStaticFactoryMethods.forMen("Complete for Men");
        assertThat(mensMultiVitamin.getName()).isEqualTo("Complete for Men");
        assertThat(mensMultiVitamin.getCalcium()).isEqualTo(CALCIUM_AMT_DEF);
        assertThat(mensMultiVitamin.getIron()).isEqualTo(IRON_AMT_MEN);
        MultiVitaminStaticFactoryMethods womensMultiVitamin = MultiVitaminStaticFactoryMethods.forWomen("Complete for Women");
        assertThat(womensMultiVitamin.getName()).isEqualTo("Complete for Women");
        assertThat(womensMultiVitamin.getCalcium()).isEqualTo(CALCIUM_AMT_WOMEN);
        assertThat(womensMultiVitamin.getIron()).isEqualTo(IRON_AMT_DEF);
    }

    @Test
    public void whenCreateMultiVitaminWithBuilder_thenOk() {
        MultiVitaminWithBuilder vitamin = new MultiVitaminWithBuilder.MultiVitaminBuilder("Maximum Strength").withCalcium(100).withIron(200).withVitaminA(50).withVitaminC(1000).build();
        assertThat(vitamin.getName()).isEqualTo("Maximum Strength");
        assertThat(vitamin.getCalcium()).isEqualTo(100);
        assertThat(vitamin.getIron()).isEqualTo(200);
        assertThat(vitamin.getVitaminA()).isEqualTo(50);
        assertThat(vitamin.getVitaminC()).isEqualTo(1000);
    }

    @Test
    public void whenCreateMutliVitaminWithAccessors_thenOk() {
        MultiVitamin vitamin = new MultiVitamin("Generic");
        vitamin.setVitaminA(50);
        vitamin.setVitaminC(1000);
        vitamin.setCalcium(100);
        vitamin.setIron(200);
        assertThat(vitamin.getName()).isEqualTo("Generic");
        assertThat(vitamin.getCalcium()).isEqualTo(100);
        assertThat(vitamin.getIron()).isEqualTo(200);
        assertThat(vitamin.getVitaminA()).isEqualTo(50);
        assertThat(vitamin.getVitaminC()).isEqualTo(1000);
    }

    @Test
    public void whenCreateMultiVitaminWithNulls_thenOk() {
        MultiVitamin vitamin = new MultiVitamin(null);
        assertThat(vitamin.getName()).isNull();
    }
}

