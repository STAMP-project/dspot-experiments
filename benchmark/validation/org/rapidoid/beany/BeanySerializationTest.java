package org.rapidoid.beany;


import java.util.Map;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;
import org.rapidoid.var.Var;
import org.rapidoid.var.Vars;


@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class BeanySerializationTest extends BeanyTestCommons {
    @Test
    public void testPrimitivesSerialization() {
        eq(Beany.serialize(123), 123);
        eq(Beany.serialize(324L), 324L);
        eq(Beany.serialize("bb"), "bb");
        eq(Beany.serialize(true), true);
        eq(Beany.serialize(null), null);
    }

    @Test
    public void testSetSerialization() {
        eq(Beany.serialize(U.set(1, 3, 5)), U.set(1, 3, 5));
    }

    @Test
    public void testListSerialization() {
        eq(Beany.serialize(U.list("a", "b", "c")), U.list("a", "b", "c"));
    }

    @Test
    public void testMapSerialization() {
        eq(Beany.serialize(U.map("a", 123, "b", 56)), U.map("a", 123, "b", 56));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testArraySerialization() {
        eq(((Object[]) (Beany.serialize(U.array("f", 3, true)))), U.array("f", 3, true));
        int[] a1 = new int[]{ 1, 2, 3 };
        int[] a2 = new int[]{ 1, 2, 3 };
        eq(((int[]) (Beany.serialize(a1))), a2);
    }

    @Test
    public void testBeanSerialization() {
        Map<String, ? extends Object> foo = U.map("x", 12, "g", "gg", "abc", ABC.B, "abcd", 111);
        Map<String, ? extends Object> bar = U.map("ff", foo);
        eq(Beany.serialize(new Foo()), foo);
        eq(Beany.serialize(new Bar()), bar);
    }

    @Test
    public void testPlainObjectSerialization() {
        eq(Beany.serialize(new Object()), U.map());
    }

    @Test
    public void testVarsSerialization() {
        Var<?> var = Vars.var("abc", 123);
        Object ser = Beany.serialize(var);
        eq(ser, U.map("abc", 123));
    }
}

