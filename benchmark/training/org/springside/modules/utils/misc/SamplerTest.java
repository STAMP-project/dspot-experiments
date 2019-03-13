package org.springside.modules.utils.misc;


import org.junit.Test;
import org.springside.modules.utils.concurrent.Sampler;
import org.springside.modules.utils.concurrent.Sampler.AlwaysSampler;
import org.springside.modules.utils.concurrent.Sampler.NeverSampler;


public class SamplerTest {
    @Test
    public void test() {
        Sampler sampler = Sampler.create(10.5);
        int hits = 0;
        for (int i = 0; i < 10000; i++) {
            if (sampler.select()) {
                hits++;
            }
        }
        System.out.println(("sample 10.5% in 10000 hits should close to 1050, actual is " + hits));
        assertThat(hits).isBetween(900, 1200);
        // ////////
        Sampler sampler2 = Sampler.create(0.5);
        hits = 0;
        for (int i = 0; i < 10000; i++) {
            if (sampler2.select()) {
                hits++;
            }
        }
        System.out.println(("sample 0.5% in 10000 hits should close to 50, actual is " + hits));
        assertThat(hits).isBetween(20, 100);
    }

    @Test
    public void always() {
        Sampler sampler = Sampler.create(0.0);
        assertThat(sampler).isInstanceOf(NeverSampler.class);
        sampler = Sampler.create(100.0);
        assertThat(sampler).isInstanceOf(AlwaysSampler.class);
        try {
            sampler = Sampler.create(101.0);
            fail("shoud fail before");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
        }
        try {
            sampler = Sampler.create((-2.2));
            fail("shoud fail before");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
        }
    }
}

