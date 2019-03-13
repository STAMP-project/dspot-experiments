/**
 * The MIT License
 * Copyright (c) 2014 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.collectionpipeline;


import Category.CONVERTIBLE;
import Category.JEEP;
import Category.SEDAN;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static Category.CONVERTIBLE;
import static Category.JEEP;
import static Category.SEDAN;


/**
 * Tests that Collection Pipeline methods work as expected.
 */
public class AppTest {
    private List<Car> cars = CarFactory.createCars();

    @Test
    public void testGetModelsAfter2000UsingFor() {
        List<String> models = ImperativeProgramming.getModelsAfter2000(cars);
        Assertions.assertEquals(Arrays.asList("Avenger", "Wrangler", "Focus", "Cascada"), models);
    }

    @Test
    public void testGetModelsAfter2000UsingPipeline() {
        List<String> models = FunctionalProgramming.getModelsAfter2000(cars);
        Assertions.assertEquals(Arrays.asList("Avenger", "Wrangler", "Focus", "Cascada"), models);
    }

    @Test
    public void testGetGroupingOfCarsByCategory() {
        Map<Category, List<Car>> modelsExpected = new HashMap<>();
        modelsExpected.put(CONVERTIBLE, Arrays.asList(new Car("Buick", "Cascada", 2016, CONVERTIBLE), new Car("Chevrolet", "Geo Metro", 1992, CONVERTIBLE)));
        modelsExpected.put(SEDAN, Arrays.asList(new Car("Dodge", "Avenger", 2010, SEDAN), new Car("Ford", "Focus", 2012, SEDAN)));
        modelsExpected.put(JEEP, Arrays.asList(new Car("Jeep", "Wrangler", 2011, JEEP), new Car("Jeep", "Comanche", 1990, JEEP)));
        Map<Category, List<Car>> modelsFunctional = FunctionalProgramming.getGroupingOfCarsByCategory(cars);
        Map<Category, List<Car>> modelsImperative = ImperativeProgramming.getGroupingOfCarsByCategory(cars);
        System.out.println(("Category " + modelsFunctional));
        Assertions.assertEquals(modelsExpected, modelsFunctional);
        Assertions.assertEquals(modelsExpected, modelsImperative);
    }

    @Test
    public void testGetSedanCarsOwnedSortedByDate() {
        Person john = new Person(cars);
        List<Car> modelsExpected = Arrays.asList(new Car("Dodge", "Avenger", 2010, SEDAN), new Car("Ford", "Focus", 2012, SEDAN));
        List<Car> modelsFunctional = FunctionalProgramming.getSedanCarsOwnedSortedByDate(Arrays.asList(john));
        List<Car> modelsImperative = ImperativeProgramming.getSedanCarsOwnedSortedByDate(Arrays.asList(john));
        Assertions.assertEquals(modelsExpected, modelsFunctional);
        Assertions.assertEquals(modelsExpected, modelsImperative);
    }
}

