package com.baeldung.casting;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class CastingUnitTest {
    @Test
    public void whenPrimitiveConverted_thenValueChanged() {
        double myDouble = 1.1;
        int myInt = ((int) (myDouble));
        Assert.assertNotEquals(myDouble, myInt);
    }

    @Test
    public void whenUpcast_thenInstanceUnchanged() {
        Cat cat = new Cat();
        Animal animal = cat;
        animal = ((Animal) (cat));
        Assert.assertTrue((animal instanceof Cat));
    }

    @Test
    public void whenUpcastToObject_thenInstanceUnchanged() {
        Object object = new Animal();
        Assert.assertTrue((object instanceof Animal));
    }

    @Test
    public void whenUpcastToInterface_thenInstanceUnchanged() {
        Mew mew = new Cat();
        Assert.assertTrue((mew instanceof Cat));
    }

    @Test
    public void whenUpcastToAnimal_thenOverridenMethodsCalled() {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Cat());
        animals.add(new Dog());
        new AnimalFeeder().feed(animals);
    }

    @Test
    public void whenDowncastToCat_thenMeowIsCalled() {
        Animal animal = new Cat();
        meow();
    }

    @Test(expected = ClassCastException.class)
    public void whenDownCastWithoutCheck_thenExceptionThrown() {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Cat());
        animals.add(new Dog());
        new AnimalFeeder().uncheckedFeed(animals);
    }

    @Test
    public void whenDowncastToCatWithCastMethod_thenMeowIsCalled() {
        Animal animal = new Cat();
        if (Cat.class.isInstance(animal)) {
            Cat cat = Cat.class.cast(animal);
            cat.meow();
        }
    }

    @Test
    public void whenParameterCat_thenOnlyCatsFed() {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Cat());
        animals.add(new Dog());
        AnimalFeederGeneric<Cat> catFeeder = new AnimalFeederGeneric<Cat>(Cat.class);
        List<Cat> fedAnimals = catFeeder.feed(animals);
        Assert.assertTrue(((fedAnimals.size()) == 1));
        Assert.assertTrue(((fedAnimals.get(0)) instanceof Cat));
    }
}

