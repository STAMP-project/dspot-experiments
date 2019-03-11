package org.robolectric;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class JavaVersionTest {
    @Test
    public void jdk8() {
        JavaVersionTest.check("1.8.1u40", "1.8.5u60");
        JavaVersionTest.check("1.8.0u40", "1.8.0u60");
        JavaVersionTest.check("1.8.0u40", "1.8.0u100");
    }

    @Test
    public void jdk9() {
        JavaVersionTest.check("9.0.1+12", "9.0.2+12");
        JavaVersionTest.check("9.0.2+60", "9.0.2+100");
    }

    @Test
    public void differentJdk() {
        JavaVersionTest.check("1.7.0", "1.8.0u60");
        JavaVersionTest.check("1.8.1u40", "9.0.2+12");
    }

    @Test
    public void longer() {
        JavaVersionTest.check("1.8.0", "1.8.0.1");
    }

    @Test
    public void longerEquality() {
        JavaVersionTest.checkEqual("1.8.0", "1.8.0");
        JavaVersionTest.checkEqual("1.8.0u33", "1.8.0u33");
        JavaVersionTest.checkEqual("5", "5");
    }
}

