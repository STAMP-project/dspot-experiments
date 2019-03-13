package org.robolectric.internal.bytecode;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.shadows.ShadowActivity;


@RunWith(JUnit4.class)
public class ShadowMapTest {
    private static final String A = ShadowMapTest.A.class.getName();

    private static final String A1 = ShadowMapTest.A1.class.getName();

    private static final String A2 = ShadowMapTest.A2.class.getName();

    private static final String B = ShadowMapTest.B.class.getName();

    private static final String B1 = ShadowMapTest.B1.class.getName();

    private static final String B2 = ShadowMapTest.B2.class.getName();

    private static final String C1 = ShadowMapTest.C1.class.getName();

    private static final String C2 = ShadowMapTest.C2.class.getName();

    private static final String C3 = ShadowMapTest.C3.class.getName();

    private static final String X = ShadowMapTest.X.class.getName();

    private ShadowMap baseShadowMap;

    @Test
    public void shouldLookUpShadowClassesByNamingConvention() throws Exception {
        ShadowMap map = baseShadowMap.newBuilder().build();
        assertThat(map.getShadowInfo(ShadowMapTest.Activity.class, (-1))).isNull();
    }

    @Test
    public void shouldNotReturnMismatchedClassesJustBecauseTheSimpleNameMatches() throws Exception {
        ShadowMap map = baseShadowMap.newBuilder().addShadowClasses(ShadowActivity.class).build();
        assertThat(map.getShadowInfo(android.app.Activity.class, (-1)).shadowClassName).isEqualTo(ShadowActivity.class.getName());
    }

    @Test
    public void getInvalidatedClasses_disjoin() {
        ShadowMap current = baseShadowMap.newBuilder().addShadowClass(ShadowMapTest.A1, ShadowMapTest.A2, true, false).build();
        ShadowMap previous = baseShadowMap.newBuilder().addShadowClass(ShadowMapTest.B1, ShadowMapTest.B2, true, false).build();
        assertThat(current.getInvalidatedClasses(previous)).containsExactly(ShadowMapTest.A1, ShadowMapTest.B1);
    }

    @Test
    public void getInvalidatedClasses_overlap() {
        ShadowMap current = baseShadowMap.newBuilder().addShadowClass(ShadowMapTest.A1, ShadowMapTest.A2, true, false).addShadowClass(ShadowMapTest.C1, ShadowMapTest.C2, true, false).build();
        ShadowMap previous = baseShadowMap.newBuilder().addShadowClass(ShadowMapTest.A1, ShadowMapTest.A2, true, false).addShadowClass(ShadowMapTest.C1, ShadowMapTest.C3, true, false).build();
        assertThat(current.getInvalidatedClasses(previous)).containsExactly(ShadowMapTest.C1);
    }

    @Test
    public void equalsHashCode() throws Exception {
        ShadowMap a = baseShadowMap.newBuilder().addShadowClass(ShadowMapTest.A, ShadowMapTest.B, true, false).build();
        ShadowMap b = baseShadowMap.newBuilder().addShadowClass(ShadowMapTest.A, ShadowMapTest.B, true, false).build();
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        ShadowMap c = b.newBuilder().build();
        assertThat(c).isEqualTo(b);
        assertThat(c.hashCode()).isEqualTo(b.hashCode());
        ShadowMap d = baseShadowMap.newBuilder().addShadowClass(ShadowMapTest.A, ShadowMapTest.X, true, false).build();
        assertThat(d).isNotEqualTo(a);
        assertThat(d.hashCode()).isNotEqualTo(b.hashCode());
    }

    static class Activity {}

    static class A {}

    static class A1 {}

    static class A2 {}

    static class B {}

    static class B1 {}

    static class B2 {}

    static class C1 {}

    static class C2 {}

    static class C3 {}

    static class X {}
}

