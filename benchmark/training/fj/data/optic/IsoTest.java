package fj.data.optic;


import fj.P;
import fj.P2;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class IsoTest {
    @Test
    public void testIso() {
        final int oldNumber = 10;
        final String oldStreet = "Main St";
        final IsoTest.Address oldAddress = new IsoTest.Address(oldNumber, oldStreet);
        final Iso<IsoTest.Address, P2<Integer, String>> addressIso = Iso.iso(( p) -> P.p(p.number, p.street), ( p) -> new fj.data.optic.Address(p._1(), p._2()));
        final IsoTest.Address a = addressIso.reverseGet(addressIso.get(oldAddress));
        Assert.assertThat(a.number, Is.is(oldAddress.number));
        Assert.assertThat(a.street, Is.is(oldAddress.street));
    }

    static final class Person {
        String name;

        IsoTest.Address address;

        Person(String name, IsoTest.Address address) {
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

