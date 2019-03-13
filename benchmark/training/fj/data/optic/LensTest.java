package fj.data.optic;


import fj.F;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class LensTest {
    @Test
    public void testLensPersonGet() {
        final String oldName = "Joe";
        final int oldNumber = 10;
        final String oldStreet = "Main St";
        final LensTest.Address oldAddress = new LensTest.Address(oldNumber, oldStreet);
        final LensTest.Person oldPerson = new LensTest.Person(oldName, oldAddress);
        final Lens<LensTest.Person, String> personNameLens = Lens.lens(( p) -> p.name, ( s) -> ( p) -> new fj.data.optic.Person(s, p.address));
        final Lens<LensTest.Person, LensTest.Address> personAddressLens = Lens.lens(( p) -> p.address, ( a) -> ( p) -> new fj.data.optic.Person(p.name, a));
        final Lens<LensTest.Address, Integer> addressNumberLens = Lens.lens(( a) -> a.number, ( n) -> ( a) -> new fj.data.optic.Address(n, a.street));
        final Lens<LensTest.Address, String> addressStreetLens = Lens.lens(( a) -> a.street, ( s) -> ( a) -> new fj.data.optic.Address(a.number, s));
        final Lens<LensTest.Person, Integer> personNumberLens = personAddressLens.composeLens(addressNumberLens);
        final Lens<LensTest.Person, String> personStreetLens = personAddressLens.composeLens(addressStreetLens);
        Assert.assertThat(personNameLens.get(oldPerson), Is.is(oldName));
        Assert.assertThat(personNumberLens.get(oldPerson), Is.is(oldNumber));
        Assert.assertThat(personStreetLens.get(oldPerson), Is.is(oldStreet));
    }

    @Test
    public void testLensPersonSetName() {
        final String oldName = "Joe";
        final int oldNumber = 10;
        final String oldStreet = "Main St";
        final LensTest.Address oldAddress = new LensTest.Address(oldNumber, oldStreet);
        final LensTest.Person oldPerson = new LensTest.Person(oldName, oldAddress);
        final Lens<LensTest.Person, String> personNameLens = Lens.lens(( p) -> p.name, ( s) -> ( p) -> new fj.data.optic.Person(s, p.address));
        String newName = "Bill";
        LensTest.Person p = personNameLens.set(newName).f(oldPerson);
        Assert.assertThat(p.name, Is.is(newName));
        Assert.assertThat(p.address, Is.is(oldPerson.address));
    }

    @Test
    public void testLensPersonSetNumber() {
        final String oldName = "Joe";
        final int oldNumber = 10;
        final String oldStreet = "Main St";
        final LensTest.Address oldAddress = new LensTest.Address(oldNumber, oldStreet);
        final LensTest.Person oldPerson = new LensTest.Person(oldName, oldAddress);
        final Lens<LensTest.Person, LensTest.Address> personAddressLens = Lens.lens(( p) -> p.address, ( a) -> ( p) -> new fj.data.optic.Person(p.name, a));
        final Lens<LensTest.Address, Integer> addressNumberLens = Lens.lens(( a) -> a.number, ( n) -> ( a) -> new fj.data.optic.Address(n, a.street));
        final Lens<LensTest.Person, Integer> personNumberLens = personAddressLens.composeLens(addressNumberLens);
        int newNumber = 20;
        LensTest.Person p = personNumberLens.set(newNumber).f(oldPerson);
        Assert.assertThat(p.name, Is.is(oldName));
        Assert.assertThat(p.address.number, Is.is(newNumber));
        Assert.assertThat(p.address.street, Is.is(oldStreet));
    }

    @Test
    public void testLensPersonSetStreet() {
        final String oldName = "Joe";
        final int oldNumber = 10;
        final String oldStreet = "Main St";
        final LensTest.Address oldAddress = new LensTest.Address(oldNumber, oldStreet);
        final LensTest.Person oldPerson = new LensTest.Person(oldName, oldAddress);
        final Lens<LensTest.Person, LensTest.Address> personAddressLens = Lens.lens(( p) -> p.address, ( a) -> ( p) -> new fj.data.optic.Person(p.name, a));
        final Lens<LensTest.Address, Integer> addressNumberLens = Lens.lens(( a) -> a.number, ( n) -> ( a) -> new fj.data.optic.Address(n, a.street));
        final Lens<LensTest.Address, String> addressStreetLens = Lens.lens(( a) -> a.street, ( s) -> ( a) -> new fj.data.optic.Address(a.number, s));
        final Lens<LensTest.Person, String> personStreetLens = personAddressLens.composeLens(addressStreetLens);
        String newStreet = "First St";
        LensTest.Person p = personStreetLens.set(newStreet).f(oldPerson);
        Assert.assertThat(p.name, Is.is(oldName));
        Assert.assertThat(p.address.number, Is.is(oldPerson.address.number));
        Assert.assertThat(p.address.street, Is.is(newStreet));
    }

    @Test
    public void testLensPersonSetter() {
        final String oldName = "Joe";
        final int oldNumber = 10;
        final String oldStreet = "Main St";
        final LensTest.Address oldAddress = new LensTest.Address(oldNumber, oldStreet);
        final LensTest.Person oldPerson = new LensTest.Person(oldName, oldAddress);
        final Lens<LensTest.Person, String> personNameLens = Lens.lens(( p) -> p.name, ( s) -> ( p) -> new fj.data.optic.Person(s, p.address));
        String newName = "Bill";
        F<LensTest.Person, LensTest.Person> setter = personNameLens.asSetter().set(newName);
        LensTest.Person p = setter.f(oldPerson);
        Assert.assertThat(p.name, Is.is(newName));
        Assert.assertThat(p.address, Is.is(oldPerson.address));
    }

    @Test
    public void testLensPersonGetter() {
        final String oldName = "Joe";
        final int oldNumber = 10;
        final String oldStreet = "Main St";
        final LensTest.Address oldAddress = new LensTest.Address(oldNumber, oldStreet);
        final LensTest.Person oldPerson = new LensTest.Person(oldName, oldAddress);
        final Lens<LensTest.Person, String> personNameLens = Lens.lens(( p) -> p.name, ( s) -> ( p) -> new fj.data.optic.Person(s, p.address));
        Assert.assertThat(personNameLens.asGetter().get(oldPerson), Is.is(oldName));
    }

    static final class Person {
        String name;

        LensTest.Address address;

        Person(String name, LensTest.Address address) {
            this.name = name;
            this.address = address;
        }
    }

    static final class Address {
        int number;

        String street;

        public Address(int number, String street) {
            this.number = number;
            this.street = street;
        }
    }
}

