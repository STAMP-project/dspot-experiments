package org.web3j.abi;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class EventEncoderTest {
    @Test
    public void testBuildEventSignature() {
        MatcherAssert.assertThat(EventEncoder.buildEventSignature("Deposit(address,hash256,uint256)"), CoreMatchers.is("0x50cb9fe53daa9737b786ab3646f04d0150dc50ef4e75f59509d83667ad5adb20"));
        MatcherAssert.assertThat(EventEncoder.buildEventSignature("Notify(uint256,uint256)"), CoreMatchers.is("0x71e71a8458267085d5ab16980fd5f114d2d37f232479c245d523ce8d23ca40ed"));
    }
}

