package com.baeldung.singletonbean;


import java.util.Arrays;
import java.util.List;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import org.junit.Assert;
import org.junit.Test;


public class CountryStateCacheBeanUnitTest {
    private EJBContainer ejbContainer = null;

    private Context context = null;

    @Test
    public void whenCallGetStatesFromContainerManagedBean_ReturnsStatesForCountry() throws Exception {
        String[] expectedStates = new String[]{ "Texas", "Alabama", "Alaska", "Arizona", "Arkansas" };
        CountryState countryStateBean = ((CountryState) (context.lookup("java:global/ejb-beans/CountryStateContainerManagedBean")));
        List<String> actualStates = countryStateBean.getStates("UnitedStates");
        Assert.assertNotNull(actualStates);
        Assert.assertArrayEquals(expectedStates, actualStates.toArray());
    }

    @Test
    public void whenCallGetStatesFromBeanManagedBean_ReturnsStatesForCountry() throws Exception {
        String[] expectedStates = new String[]{ "Texas", "Alabama", "Alaska", "Arizona", "Arkansas" };
        CountryState countryStateBean = ((CountryState) (context.lookup("java:global/ejb-beans/CountryStateBeanManagedBean")));
        List<String> actualStates = countryStateBean.getStates("UnitedStates");
        Assert.assertNotNull(actualStates);
        Assert.assertArrayEquals(expectedStates, actualStates.toArray());
    }

    @Test
    public void whenCallSetStatesFromContainerManagedBean_SetsStatesForCountry() throws Exception {
        String[] expectedStates = new String[]{ "California", "Florida", "Hawaii", "Pennsylvania", "Michigan" };
        CountryState countryStateBean = ((CountryState) (context.lookup("java:global/ejb-beans/CountryStateContainerManagedBean")));
        countryStateBean.setStates("UnitedStates", Arrays.asList(expectedStates));
        List<String> actualStates = countryStateBean.getStates("UnitedStates");
        Assert.assertNotNull(actualStates);
        Assert.assertArrayEquals(expectedStates, actualStates.toArray());
    }

    @Test
    public void whenCallSetStatesFromBeanManagedBean_SetsStatesForCountry() throws Exception {
        String[] expectedStates = new String[]{ "California", "Florida", "Hawaii", "Pennsylvania", "Michigan" };
        CountryState countryStateBean = ((CountryState) (context.lookup("java:global/ejb-beans/CountryStateBeanManagedBean")));
        countryStateBean.setStates("UnitedStates", Arrays.asList(expectedStates));
        List<String> actualStates = countryStateBean.getStates("UnitedStates");
        Assert.assertNotNull(actualStates);
        Assert.assertArrayEquals(expectedStates, actualStates.toArray());
    }
}

