package edu.umd.cs.findbugs.ba;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class FrameTest {
    @Test
    public void testToString() {
        Frame<String> frame = new Frame<String>(1) {
            @Override
            public String getValue(int n) {
                return "value";
            }
        };
        Assert.assertThat(frame.toString(), CoreMatchers.is(CoreMatchers.equalTo("[value]")));
    }
}

