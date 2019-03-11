package fj.function;


import org.junit.Test;


/**
 * Created by mperry on 28/08/2014.
 */
public class TestEffect {
    @Test
    public void test1() {
        TestEffect.higher(TestEffect::m1);
    }
}

