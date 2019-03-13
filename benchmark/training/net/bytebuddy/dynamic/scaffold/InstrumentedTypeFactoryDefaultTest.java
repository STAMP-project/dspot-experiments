package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.dynamic.scaffold.InstrumentedType.Factory.Default.FROZEN;
import static net.bytebuddy.dynamic.scaffold.InstrumentedType.Factory.Default.MODIFIABLE;


public class InstrumentedTypeFactoryDefaultTest {
    @Test
    public void testSubclassModifiable() throws Exception {
        MatcherAssert.assertThat(MODIFIABLE.subclass("foo", 0, OBJECT), CoreMatchers.instanceOf(InstrumentedType.Default.class));
    }

    @Test
    public void testSubclassFrozen() throws Exception {
        MatcherAssert.assertThat(FROZEN.subclass("foo", 0, OBJECT), CoreMatchers.instanceOf(InstrumentedType.Default.class));
    }

    @Test
    public void testRepresentModifiable() throws Exception {
        MatcherAssert.assertThat(MODIFIABLE.represent(TypeDescription.OBJECT), CoreMatchers.instanceOf(InstrumentedType.Default.class));
    }

    @Test
    public void testRepresentFrozen() throws Exception {
        MatcherAssert.assertThat(FROZEN.represent(TypeDescription.OBJECT), CoreMatchers.instanceOf(InstrumentedType.Frozen.class));
    }
}

