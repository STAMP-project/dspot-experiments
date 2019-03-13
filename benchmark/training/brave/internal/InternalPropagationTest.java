package brave.internal;


import org.junit.Test;


public class InternalPropagationTest {
    @Test
    public void set_sampled_true() {
        assertThat(InternalPropagation.sampled(true, 0)).isEqualTo(((InternalPropagation.FLAG_SAMPLED_SET) + (InternalPropagation.FLAG_SAMPLED)));
    }

    @Test
    public void set_sampled_false() {
        assertThat(InternalPropagation.sampled(false, ((InternalPropagation.FLAG_SAMPLED_SET) | (InternalPropagation.FLAG_SAMPLED)))).isEqualTo(InternalPropagation.FLAG_SAMPLED_SET);
    }
}

