

package org.traccar.geocoder;


public class AmplAddressFormatTest {
    private void test(org.traccar.geocoder.Address address, java.lang.String format, java.lang.String expected) {
        org.junit.Assert.assertEquals(expected, new org.traccar.geocoder.AddressFormat(format).format(address));
    }

    @org.junit.Test
    public void testFormat() {
        org.traccar.geocoder.Address address = new org.traccar.geocoder.Address();
        address.setCountry("NZ");
        address.setSettlement("Auckland");
        address.setStreet("Queen St");
        address.setHouse("1A");
        test(address, "%h %r %t %d %s %c %p", "1A Queen St Auckland NZ");
        test(address, "%h %r %t", "1A Queen St Auckland");
        test(address, "%h %r, %t", "1A Queen St, Auckland");
        test(address, "%h %r, %t %p", "1A Queen St, Auckland");
        test(address, "%t %d %c", "Auckland NZ");
        test(address, "%t, %d, %c", "Auckland, NZ");
        test(address, "%d %c", "NZ");
        test(address, "%d, %c", "NZ");
        test(address, "%p", "");
    }
}

