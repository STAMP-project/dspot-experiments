package fj;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class PTest {
    @Test
    public void testPF() {
        final F<Integer, P1<Integer>> p1f = P.p1();
        final P1<Integer> p1 = p1f.f(1);
        F<Integer, F<Integer, P2<Integer, Integer>>> p2f = P.p2();
        final P2<Integer, Integer> p2 = p2f.f(1).f(2);
        Assert.assertThat(P2.<Integer, Integer>__1().f(p2), Is.is(P1.<Integer>__1().f(p1)));
        final F<Integer, F<Integer, F<Integer, P3<Integer, Integer, Integer>>>> p3f = P.p3();
        final P3<Integer, Integer, Integer> p3 = p3f.f(1).f(2).f(3);
        Assert.assertThat(P3.<Integer, Integer, Integer>__1().f(p3), Is.is(P2.<Integer, Integer>__1().f(p2)));
        Assert.assertThat(P3.<Integer, Integer, Integer>__2().f(p3), Is.is(P2.<Integer, Integer>__2().f(p2)));
        final F<Integer, F<Integer, F<Integer, F<Integer, P4<Integer, Integer, Integer, Integer>>>>> p4f = P.p4();
        final P4<Integer, Integer, Integer, Integer> p4 = p4f.f(1).f(2).f(3).f(4);
        Assert.assertThat(P4.<Integer, Integer, Integer, Integer>__1().f(p4), Is.is(P3.<Integer, Integer, Integer>__1().f(p3)));
        Assert.assertThat(P4.<Integer, Integer, Integer, Integer>__2().f(p4), Is.is(P3.<Integer, Integer, Integer>__2().f(p3)));
        Assert.assertThat(P4.<Integer, Integer, Integer, Integer>__3().f(p4), Is.is(P3.<Integer, Integer, Integer>__3().f(p3)));
        final F<Integer, F<Integer, F<Integer, F<Integer, F<Integer, P5<Integer, Integer, Integer, Integer, Integer>>>>>> p5f = P.p5();
        final P5<Integer, Integer, Integer, Integer, Integer> p5 = p5f.f(1).f(2).f(3).f(4).f(5);
        Assert.assertThat(P5.<Integer, Integer, Integer, Integer, Integer>__1().f(p5), Is.is(P4.<Integer, Integer, Integer, Integer>__1().f(p4)));
        Assert.assertThat(P5.<Integer, Integer, Integer, Integer, Integer>__2().f(p5), Is.is(P4.<Integer, Integer, Integer, Integer>__2().f(p4)));
        Assert.assertThat(P5.<Integer, Integer, Integer, Integer, Integer>__3().f(p5), Is.is(P4.<Integer, Integer, Integer, Integer>__3().f(p4)));
        Assert.assertThat(P5.<Integer, Integer, Integer, Integer, Integer>__4().f(p5), Is.is(P4.<Integer, Integer, Integer, Integer>__4().f(p4)));
        final F<Integer, F<Integer, F<Integer, F<Integer, F<Integer, F<Integer, P6<Integer, Integer, Integer, Integer, Integer, Integer>>>>>>> p6f = P.p6();
        final P6<Integer, Integer, Integer, Integer, Integer, Integer> p6 = p6f.f(1).f(2).f(3).f(4).f(5).f(6);
        Assert.assertThat(P6.<Integer, Integer, Integer, Integer, Integer, Integer>__1().f(p6), Is.is(P5.<Integer, Integer, Integer, Integer, Integer>__1().f(p5)));
        Assert.assertThat(P6.<Integer, Integer, Integer, Integer, Integer, Integer>__2().f(p6), Is.is(P5.<Integer, Integer, Integer, Integer, Integer>__2().f(p5)));
        Assert.assertThat(P6.<Integer, Integer, Integer, Integer, Integer, Integer>__3().f(p6), Is.is(P5.<Integer, Integer, Integer, Integer, Integer>__3().f(p5)));
        Assert.assertThat(P6.<Integer, Integer, Integer, Integer, Integer, Integer>__4().f(p6), Is.is(P5.<Integer, Integer, Integer, Integer, Integer>__4().f(p5)));
        Assert.assertThat(P6.<Integer, Integer, Integer, Integer, Integer, Integer>__5().f(p6), Is.is(P5.<Integer, Integer, Integer, Integer, Integer>__5().f(p5)));
        final F<Integer, F<Integer, F<Integer, F<Integer, F<Integer, F<Integer, F<Integer, P7<Integer, Integer, Integer, Integer, Integer, Integer, Integer>>>>>>>> p7f = P.p7();
        final P7<Integer, Integer, Integer, Integer, Integer, Integer, Integer> p7 = p7f.f(1).f(2).f(3).f(4).f(5).f(6).f(7);
        Assert.assertThat(P7.<Integer, Integer, Integer, Integer, Integer, Integer, Integer>__1().f(p7), Is.is(P6.<Integer, Integer, Integer, Integer, Integer, Integer>__1().f(p6)));
        Assert.assertThat(P7.<Integer, Integer, Integer, Integer, Integer, Integer, Integer>__2().f(p7), Is.is(P6.<Integer, Integer, Integer, Integer, Integer, Integer>__2().f(p6)));
        Assert.assertThat(P7.<Integer, Integer, Integer, Integer, Integer, Integer, Integer>__3().f(p7), Is.is(P6.<Integer, Integer, Integer, Integer, Integer, Integer>__3().f(p6)));
        Assert.assertThat(P7.<Integer, Integer, Integer, Integer, Integer, Integer, Integer>__4().f(p7), Is.is(P6.<Integer, Integer, Integer, Integer, Integer, Integer>__4().f(p6)));
        Assert.assertThat(P7.<Integer, Integer, Integer, Integer, Integer, Integer, Integer>__5().f(p7), Is.is(P6.<Integer, Integer, Integer, Integer, Integer, Integer>__5().f(p6)));
        Assert.assertThat(P7.<Integer, Integer, Integer, Integer, Integer, Integer, Integer>__6().f(p7), Is.is(P6.<Integer, Integer, Integer, Integer, Integer, Integer>__6().f(p6)));
        final F<Integer, F<Integer, F<Integer, F<Integer, F<Integer, F<Integer, F<Integer, F<Integer, P8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>>>>>>>> p8f = P.p8();
        final P8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> p8 = p8f.f(1).f(2).f(3).f(4).f(5).f(6).f(7).f(8);
        Assert.assertThat(P8.<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>__1().f(p8), Is.is(P7.<Integer, Integer, Integer, Integer, Integer, Integer, Integer>__1().f(p7)));
        Assert.assertThat(P8.<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>__2().f(p8), Is.is(P7.<Integer, Integer, Integer, Integer, Integer, Integer, Integer>__2().f(p7)));
        Assert.assertThat(P8.<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>__3().f(p8), Is.is(P7.<Integer, Integer, Integer, Integer, Integer, Integer, Integer>__3().f(p7)));
        Assert.assertThat(P8.<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>__4().f(p8), Is.is(P7.<Integer, Integer, Integer, Integer, Integer, Integer, Integer>__4().f(p7)));
        Assert.assertThat(P8.<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>__5().f(p8), Is.is(P7.<Integer, Integer, Integer, Integer, Integer, Integer, Integer>__5().f(p7)));
        Assert.assertThat(P8.<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>__6().f(p8), Is.is(P7.<Integer, Integer, Integer, Integer, Integer, Integer, Integer>__6().f(p7)));
        Assert.assertThat(P8.<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>__7().f(p8), Is.is(P7.<Integer, Integer, Integer, Integer, Integer, Integer, Integer>__7().f(p7)));
        Assert.assertThat(P8.<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>__8().f(p8), Is.is(8));
    }

    @Test
    public void testPProject1() {
        final P1<Integer> p1 = P.p(1);
        Assert.assertThat(p1.map(Function.identity()), Is.is(p1));
    }

    @Test
    public void testPProject2() {
        final P2<Integer, Integer> p2 = P.p(1, 2);
        Assert.assertThat(p2.map1(Function.identity()), Is.is(p2));
        Assert.assertThat(p2.map2(Function.identity()), Is.is(p2));
    }

    @Test
    public void testPProject3() {
        final P3<Integer, Integer, Integer> p3 = P.p(1, 2, 3);
        Assert.assertThat(p3.map1(Function.identity()), Is.is(p3));
        Assert.assertThat(p3.map2(Function.identity()), Is.is(p3));
        Assert.assertThat(p3.map3(Function.identity()), Is.is(p3));
    }

    @Test
    public void testPProject4() {
        final P4<Integer, Integer, Integer, Integer> p4 = P.p(1, 2, 3, 4);
        Assert.assertThat(p4.map1(Function.identity()), Is.is(p4));
        Assert.assertThat(p4.map2(Function.identity()), Is.is(p4));
        Assert.assertThat(p4.map3(Function.identity()), Is.is(p4));
        Assert.assertThat(p4.map4(Function.identity()), Is.is(p4));
    }

    @Test
    public void testPProject5() {
        final P5<Integer, Integer, Integer, Integer, Integer> p5 = P.p(1, 2, 3, 4, 5);
        Assert.assertThat(p5.map1(Function.identity()), Is.is(p5));
        Assert.assertThat(p5.map2(Function.identity()), Is.is(p5));
        Assert.assertThat(p5.map3(Function.identity()), Is.is(p5));
        Assert.assertThat(p5.map4(Function.identity()), Is.is(p5));
        Assert.assertThat(p5.map5(Function.identity()), Is.is(p5));
    }

    @Test
    public void testPProject6() {
        final P6<Integer, Integer, Integer, Integer, Integer, Integer> p6 = P.p(1, 2, 3, 4, 5, 6);
        Assert.assertThat(p6.map1(Function.identity()), Is.is(p6));
        Assert.assertThat(p6.map2(Function.identity()), Is.is(p6));
        Assert.assertThat(p6.map3(Function.identity()), Is.is(p6));
        Assert.assertThat(p6.map4(Function.identity()), Is.is(p6));
        Assert.assertThat(p6.map5(Function.identity()), Is.is(p6));
        Assert.assertThat(p6.map6(Function.identity()), Is.is(p6));
    }

    @Test
    public void testPProject7() {
        final P7<Integer, Integer, Integer, Integer, Integer, Integer, Integer> p7 = P.p(1, 2, 3, 4, 5, 6, 7);
        Assert.assertThat(p7.map1(Function.identity()), Is.is(p7));
        Assert.assertThat(p7.map2(Function.identity()), Is.is(p7));
        Assert.assertThat(p7.map3(Function.identity()), Is.is(p7));
        Assert.assertThat(p7.map4(Function.identity()), Is.is(p7));
        Assert.assertThat(p7.map5(Function.identity()), Is.is(p7));
        Assert.assertThat(p7.map6(Function.identity()), Is.is(p7));
        Assert.assertThat(p7.map7(Function.identity()), Is.is(p7));
    }

    @Test
    public void testPProject8() {
        final P8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> p8 = P.p(1, 2, 3, 4, 5, 6, 7, 8);
        Assert.assertThat(p8.map1(Function.identity()), Is.is(p8));
        Assert.assertThat(p8.map2(Function.identity()), Is.is(p8));
        Assert.assertThat(p8.map3(Function.identity()), Is.is(p8));
        Assert.assertThat(p8.map4(Function.identity()), Is.is(p8));
        Assert.assertThat(p8.map5(Function.identity()), Is.is(p8));
        Assert.assertThat(p8.map6(Function.identity()), Is.is(p8));
        Assert.assertThat(p8.map7(Function.identity()), Is.is(p8));
        Assert.assertThat(p8.map8(Function.identity()), Is.is(p8));
    }
}

