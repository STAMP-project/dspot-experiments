package fj.test;


import fj.data.test.PropertyAssert;
import org.junit.Test;


public class TestBool {
    @Test
    public void testBool() {
        final Property p = Property.property(Arbitrary.arbBoolean, Arbitrary.arbBoolean, ( m1, m2) -> bool(m1.equals(m2)).implies((m1 == m2)));
        PropertyAssert.assertResult(p);
    }
}

