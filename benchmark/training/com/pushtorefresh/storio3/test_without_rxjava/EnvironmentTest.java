package com.pushtorefresh.storio3.test_without_rxjava;


import org.junit.Test;


public class EnvironmentTest {
    @Test
    public void noRxJava2InClassPath() {
        assertThat(Environment.RX_JAVA_2_IS_IN_THE_CLASS_PATH).isFalse();
    }

    @Test(expected = ClassNotFoundException.class)
    public void rxJava2IsReallyNotInClassPath() throws ClassNotFoundException {
        Class.forName("io.reactivex.Flowable");
    }
}

