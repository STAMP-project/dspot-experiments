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

    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat */
    @org.junit.Test(timeout = 10000)
    public void testFormat_sd150() {
        java.lang.String __DSPOT_district_1 = "U^r,Jp9Flz5*yC=M]:bM";
        org.traccar.geocoder.Address address = new org.traccar.geocoder.Address();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getCountry());
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
        // StatementAdd: add invocation of a method
        address.setDistrict(__DSPOT_district_1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("U^r,Jp9Flz5*yC=M]:bM", ((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("1A", ((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Auckland", ((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Queen St", ((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("NZ", ((org.traccar.geocoder.Address)address).getCountry());
    }

    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat */
    @org.junit.Test(timeout = 10000)
    public void testFormat_sd154() {
        java.lang.String __DSPOT_state_5 = "LDWP=,y4JV)d4}^w[&oD";
        org.traccar.geocoder.Address address = new org.traccar.geocoder.Address();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getCountry());
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
        // StatementAdd: add invocation of a method
        address.setState(__DSPOT_state_5);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("1A", ((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Auckland", ((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Queen St", ((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("LDWP=,y4JV)d4}^w[&oD", ((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("NZ", ((org.traccar.geocoder.Address)address).getCountry());
    }

    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat */
    @org.junit.Test(timeout = 10000)
    public void testFormat_sd152() {
        java.lang.String __DSPOT_postcode_3 = "#yO[*WW4JN-$nw<}7EGp";
        org.traccar.geocoder.Address address = new org.traccar.geocoder.Address();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getCountry());
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
        // StatementAdd: add invocation of a method
        address.setPostcode(__DSPOT_postcode_3);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("1A", ((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("#yO[*WW4JN-$nw<}7EGp", ((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Auckland", ((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Queen St", ((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("NZ", ((org.traccar.geocoder.Address)address).getCountry());
    }

    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat */
    @org.junit.Test(timeout = 10000)
    public void testFormat_sd156() {
        java.lang.String __DSPOT_suburb_7 = "E_&Ml%;sG#Ahw*&z*$G`";
        org.traccar.geocoder.Address address = new org.traccar.geocoder.Address();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getCountry());
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
        // StatementAdd: add invocation of a method
        address.setSuburb(__DSPOT_suburb_7);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("1A", ((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("E_&Ml%;sG#Ahw*&z*$G`", ((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Auckland", ((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Queen St", ((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("NZ", ((org.traccar.geocoder.Address)address).getCountry());
    }

    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat */
    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat_sd150 */
    @org.junit.Test(timeout = 10000)
    public void testFormat_sd150_remove6246() // removed test(address, "%h %r %t", "1A Queen St Auckland") at line 22
    {
        java.lang.String __DSPOT_district_1 = "U^r,Jp9Flz5*yC=M]:bM";
        org.traccar.geocoder.Address address = new org.traccar.geocoder.Address();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getCountry());
        address.setCountry("NZ");
        address.setSettlement("Auckland");
        address.setStreet("Queen St");
        address.setHouse("1A");
        test(address, "%h %r %t %d %s %c %p", "1A Queen St Auckland NZ");
        test(address, "%h %r, %t", "1A Queen St, Auckland");
        test(address, "%h %r, %t %p", "1A Queen St, Auckland");
        test(address, "%t %d %c", "Auckland NZ");
        test(address, "%t, %d, %c", "Auckland, NZ");
        test(address, "%d %c", "NZ");
        test(address, "%d, %c", "NZ");
        test(address, "%p", "");
        // StatementAdd: add invocation of a method
        address.setDistrict(__DSPOT_district_1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("U^r,Jp9Flz5*yC=M]:bM", ((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("1A", ((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Auckland", ((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Queen St", ((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("NZ", ((org.traccar.geocoder.Address)address).getCountry());
    }

    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat */
    @org.junit.Test(timeout = 10000)
    public void testFormat_remove134_sd3410() // removed test(address, "%h %r, %t", "1A Queen St, Auckland") at line 23
    {
        java.lang.String __DSPOT_postcode_147 = "owV3*r!]2^:mx<|+DB*`";
        org.traccar.geocoder.Address address = new org.traccar.geocoder.Address();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getCountry());
        address.setCountry("NZ");
        address.setSettlement("Auckland");
        address.setStreet("Queen St");
        address.setHouse("1A");
        test(address, "%h %r %t %d %s %c %p", "1A Queen St Auckland NZ");
        test(address, "%h %r %t", "1A Queen St Auckland");
        test(address, "%h %r, %t %p", "1A Queen St, Auckland");
        test(address, "%t %d %c", "Auckland NZ");
        test(address, "%t, %d, %c", "Auckland, NZ");
        test(address, "%d %c", "NZ");
        test(address, "%d, %c", "NZ");
        test(address, "%p", "");
        // StatementAdd: add invocation of a method
        address.setPostcode(__DSPOT_postcode_147);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("1A", ((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("owV3*r!]2^:mx<|+DB*`", ((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Auckland", ((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Queen St", ((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("NZ", ((org.traccar.geocoder.Address)address).getCountry());
    }

    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat */
    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat_sd153 */
    @org.junit.Test(timeout = 10000)
    public void testFormat_sd153_sd6760() {
        java.lang.String __DSPOT_state_821 = "#7=N21-))165q4s?Z:28";
        java.lang.String __DSPOT_settlement_4 = "wmm(EQndBdj-qEHp!#I]";
        org.traccar.geocoder.Address address = new org.traccar.geocoder.Address();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getCountry());
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
        // StatementAdd: add invocation of a method
        address.setSettlement(__DSPOT_settlement_4);
        // StatementAdd: add invocation of a method
        address.setState(__DSPOT_state_821);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("1A", ((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wmm(EQndBdj-qEHp!#I]", ((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Queen St", ((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("#7=N21-))165q4s?Z:28", ((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("NZ", ((org.traccar.geocoder.Address)address).getCountry());
    }

    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat */
    @org.junit.Test(timeout = 10000)
    public void testFormat_remove138_sd3985() // removed test(address, "%d %c", "NZ") at line 27
    {
        java.lang.String __DSPOT_suburb_183 = "bx+<C(Nam#ZOk$_eF0g>";
        org.traccar.geocoder.Address address = new org.traccar.geocoder.Address();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getCountry());
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
        test(address, "%d, %c", "NZ");
        test(address, "%p", "");
        // StatementAdd: add invocation of a method
        address.setSuburb(__DSPOT_suburb_183);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("1A", ((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bx+<C(Nam#ZOk$_eF0g>", ((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Auckland", ((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Queen St", ((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("NZ", ((org.traccar.geocoder.Address)address).getCountry());
    }

    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat */
    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat_sd151 */
    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat_sd151_literalMutationString6274 */
    @org.junit.Test(timeout = 10000)
    public void testFormat_sd151_literalMutationString6274_sd11588() {
        java.lang.String __DSPOT_district_1529 = "]R#(nG[RW?>h%Kgzoz<U";
        java.lang.String __DSPOT_house_2 = "qG[RqJK|<cIr! %)94(c";
        org.traccar.geocoder.Address address = new org.traccar.geocoder.Address();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getCountry());
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
        // StatementAdd: add invocation of a method
        address.setHouse(__DSPOT_house_2);
        // StatementAdd: add invocation of a method
        address.setDistrict(__DSPOT_district_1529);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("]R#(nG[RW?>h%Kgzoz<U", ((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("qG[RqJK|<cIr! %)94(c", ((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Auckland", ((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Queen St", ((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("NZ", ((org.traccar.geocoder.Address)address).getCountry());
    }

    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat */
    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat_remove134_sd3410 */
    @org.junit.Test(timeout = 10000)
    public void testFormat_remove134_sd3410_add15178() // removed test(address, "%h %r, %t", "1A Queen St, Auckland") at line 23
    {
        java.lang.String __DSPOT_postcode_147 = "owV3*r!]2^:mx<|+DB*`";
        org.traccar.geocoder.Address address = new org.traccar.geocoder.Address();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getCountry());
        address.setCountry("NZ");
        // MethodCallAdder
        address.setSettlement("Auckland");
        address.setSettlement("Auckland");
        address.setStreet("Queen St");
        address.setHouse("1A");
        test(address, "%h %r %t %d %s %c %p", "1A Queen St Auckland NZ");
        test(address, "%h %r %t", "1A Queen St Auckland");
        test(address, "%h %r, %t %p", "1A Queen St, Auckland");
        test(address, "%t %d %c", "Auckland NZ");
        test(address, "%t, %d, %c", "Auckland, NZ");
        test(address, "%d %c", "NZ");
        test(address, "%d, %c", "NZ");
        test(address, "%p", "");
        // StatementAdd: add invocation of a method
        address.setPostcode(__DSPOT_postcode_147);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("1A", ((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("owV3*r!]2^:mx<|+DB*`", ((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Auckland", ((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Queen St", ((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("NZ", ((org.traccar.geocoder.Address)address).getCountry());
    }

    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat */
    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat_add118_sd1459 */
    @org.junit.Test(timeout = 10000)
    public void testFormat_add118_sd1459_sd12639() {
        java.lang.String __DSPOT_suburb_1648 = "N#B2f@P{(}2((`;Ot`.U";
        org.traccar.geocoder.Address address = new org.traccar.geocoder.Address();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getCountry());
        address.setCountry("NZ");
        address.setSettlement("Auckland");
        address.setStreet("Queen St");
        // MethodCallAdder
        address.setHouse("1A");
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
        // StatementAdd: add invocation of a method
        address.getSettlement();
        // StatementAdd: add invocation of a method
        address.setSuburb(__DSPOT_suburb_1648);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("1A", ((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("N#B2f@P{(}2((`;Ot`.U", ((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Auckland", ((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Queen St", ((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("NZ", ((org.traccar.geocoder.Address)address).getCountry());
    }

    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat */
    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat_sd153 */
    /* amplification of org.traccar.geocoder.AddressFormatTest#testFormat_sd153_sd6760 */
    @org.junit.Test(timeout = 10000)
    public void testFormat_sd153_sd6760_literalMutationString15020() {
        java.lang.String __DSPOT_state_821 = "#7=N21-))165q4s?Z:28";
        java.lang.String __DSPOT_settlement_4 = "wmm(EQndBdj-qEHp!#I]";
        org.traccar.geocoder.Address address = new org.traccar.geocoder.Address();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getCountry());
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
        // StatementAdd: add invocation of a method
        address.setSettlement(__DSPOT_settlement_4);
        // StatementAdd: add invocation of a method
        address.setState(__DSPOT_state_821);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getDistrict());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("1A", ((org.traccar.geocoder.Address)address).getHouse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getSuburb());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.traccar.geocoder.Address)address).getPostcode());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wmm(EQndBdj-qEHp!#I]", ((org.traccar.geocoder.Address)address).getSettlement());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Queen St", ((org.traccar.geocoder.Address)address).getStreet());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("#7=N21-))165q4s?Z:28", ((org.traccar.geocoder.Address)address).getState());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("NZ", ((org.traccar.geocoder.Address)address).getCountry());
    }
}

