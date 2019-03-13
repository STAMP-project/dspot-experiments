package org.rapidoid.beany;


import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;


@Authors("Daniel Kalevski")
@Since("4.0.1")
public class BeanyPropertiesTest extends BeanyTestCommons {
    @Test
    public void testGetPropValue() {
        Baz baz = new Baz();
        Object bazX = Beany.getPropValue(baz, "x");
        eq(baz.x, bazX);
    }
}

