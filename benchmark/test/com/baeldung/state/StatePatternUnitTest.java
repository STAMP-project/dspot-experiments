package com.baeldung.state;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class StatePatternUnitTest {
    @Test
    public void givenNewPackage_whenPackageReceived_thenStateReceived() {
        Package pkg = new Package();
        Assert.assertThat(getState(), CoreMatchers.instanceOf(OrderedState.class));
        nextState();
        Assert.assertThat(getState(), CoreMatchers.instanceOf(DeliveredState.class));
        nextState();
        Assert.assertThat(getState(), CoreMatchers.instanceOf(ReceivedState.class));
    }

    @Test
    public void givenDeliveredPackage_whenPrevState_thenStateOrdered() {
        Package pkg = new Package();
        pkg.setState(new DeliveredState());
        previousState();
        Assert.assertThat(getState(), CoreMatchers.instanceOf(OrderedState.class));
    }
}

