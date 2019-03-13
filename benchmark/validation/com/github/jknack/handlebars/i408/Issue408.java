package com.github.jknack.handlebars.i408;


import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.HandlebarsException;
import java.io.IOException;
import org.junit.Test;


public class Issue408 extends AbstractTest {
    public static class Car {
        private boolean isCar;

        private Double carPrice;

        public Car(final boolean isCar, final Double carPrice) {
            this.isCar = isCar;
            this.carPrice = carPrice;
        }

        public boolean isCar() {
            return isCar;
        }

        public Double getCarPrice() {
            return (carPrice) + 10.0;
        }
    }

    @Test
    public void shouldNotThrowNPE() throws IOException {
        shouldCompileTo(("{{#car}}\n" + (" Car Price: {{carPrice}}\n" + "{{/car}}")), new Issue408.Car(false, null), "");
    }

    @Test(expected = HandlebarsException.class)
    public void shouldThrowNPE() throws IOException {
        shouldCompileTo(("{{#car}}\n" + (" Car Price: {{carPrice}}\n" + "{{/car}}")), new Issue408.Car(true, null), "");
    }
}

