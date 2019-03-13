package com.airbnb.lottie;


import org.junit.Test;


public class KeyPathTest extends BaseTest {
    private static final String[] V = new String[]{ "Shape Layer 1", "Group 1", "Rectangle", "Stroke" };

    private static final String I = "INVALID";

    private static final String W = "*";

    private static final String G = "**";

    private LottieDrawable lottieDrawable;

    // <editor-fold desc="Basic Tests">
    @Test
    public void testV() {
        assertSize(1, KeyPathTest.V[0]);
    }

    @Test
    public void testI() {
        assertSize(0, KeyPathTest.I);
    }

    @Test
    public void testII() {
        assertSize(0, KeyPathTest.I, KeyPathTest.I);
    }

    @Test
    public void testIV() {
        assertSize(0, KeyPathTest.I, KeyPathTest.V[1]);
    }

    @Test
    public void testVI() {
        assertSize(0, KeyPathTest.V[0], KeyPathTest.I);
    }

    @Test
    public void testVV() {
        assertSize(1, KeyPathTest.V[0], KeyPathTest.V[1]);
    }

    @Test
    public void testIII() {
        assertSize(0, KeyPathTest.I, KeyPathTest.I, KeyPathTest.I);
    }

    @Test
    public void testIIV() {
        assertSize(0, KeyPathTest.I, KeyPathTest.I, KeyPathTest.V[3]);
    }

    @Test
    public void testIVI() {
        assertSize(0, KeyPathTest.I, KeyPathTest.V[2], KeyPathTest.I);
    }

    @Test
    public void testIVV() {
        assertSize(0, KeyPathTest.I, KeyPathTest.V[2], KeyPathTest.V[3]);
    }

    @Test
    public void testVII() {
        assertSize(0, KeyPathTest.V[0], KeyPathTest.I, KeyPathTest.I);
    }

    @Test
    public void testVIV() {
        assertSize(0, KeyPathTest.V[0], KeyPathTest.I, KeyPathTest.V[2]);
    }

    @Test
    public void testVVI() {
        assertSize(0, KeyPathTest.V[0], KeyPathTest.V[1], KeyPathTest.I);
    }

    @Test
    public void testVVV() {
        assertSize(1, KeyPathTest.V[0], KeyPathTest.V[1], KeyPathTest.V[2]);
    }

    @Test
    public void testIIII() {
        assertSize(0, KeyPathTest.I, KeyPathTest.I, KeyPathTest.I, KeyPathTest.I);
    }

    @Test
    public void testIIIV() {
        assertSize(0, KeyPathTest.I, KeyPathTest.I, KeyPathTest.I, KeyPathTest.V[3]);
    }

    @Test
    public void testIIVI() {
        assertSize(0, KeyPathTest.I, KeyPathTest.I, KeyPathTest.V[2], KeyPathTest.I);
    }

    @Test
    public void testIIVV() {
        assertSize(0, KeyPathTest.I, KeyPathTest.I, KeyPathTest.V[2], KeyPathTest.V[3]);
    }

    @Test
    public void testIVII() {
        assertSize(0, KeyPathTest.I, KeyPathTest.V[1], KeyPathTest.I, KeyPathTest.I);
    }

    @Test
    public void testIVIV() {
        assertSize(0, KeyPathTest.I, KeyPathTest.V[1], KeyPathTest.I, KeyPathTest.V[3]);
    }

    @Test
    public void testIVVI() {
        assertSize(0, KeyPathTest.I, KeyPathTest.V[1], KeyPathTest.V[2], KeyPathTest.V[3]);
    }

    @Test
    public void testIVVV() {
        assertSize(0, KeyPathTest.I, KeyPathTest.V[1], KeyPathTest.V[2], KeyPathTest.V[3]);
    }

    @Test
    public void testVIII() {
        assertSize(0, KeyPathTest.V[0], KeyPathTest.I, KeyPathTest.I, KeyPathTest.I);
    }

    @Test
    public void testVIIV() {
        assertSize(0, KeyPathTest.V[0], KeyPathTest.I, KeyPathTest.I, KeyPathTest.V[3]);
    }

    @Test
    public void testVIVI() {
        assertSize(0, KeyPathTest.V[0], KeyPathTest.I, KeyPathTest.V[2], KeyPathTest.I);
    }

    @Test
    public void testVIVV() {
        assertSize(0, KeyPathTest.V[0], KeyPathTest.I, KeyPathTest.V[2], KeyPathTest.V[3]);
    }

    @Test
    public void testVVII() {
        assertSize(0, KeyPathTest.V[0], KeyPathTest.V[1], KeyPathTest.I, KeyPathTest.I);
    }

    @Test
    public void testVVIV() {
        assertSize(0, KeyPathTest.V[0], KeyPathTest.V[1], KeyPathTest.I, KeyPathTest.V[3]);
    }

    @Test
    public void testVVVI() {
        assertSize(0, KeyPathTest.V[0], KeyPathTest.V[1], KeyPathTest.V[2], KeyPathTest.I);
    }

    @Test
    public void testVVVV() {
        assertSize(1, KeyPathTest.V[0], KeyPathTest.V[1], KeyPathTest.V[2], KeyPathTest.V[3]);
    }

    // </editor-fold>
    // <editor-fold desc="One Wildcard">
    @Test
    public void testWVVV() {
        assertSize(2, KeyPathTest.W, KeyPathTest.V[1], KeyPathTest.V[2], KeyPathTest.V[3]);
    }

    @Test
    public void testVWVV() {
        assertSize(2, KeyPathTest.V[0], KeyPathTest.W, KeyPathTest.V[2], KeyPathTest.V[3]);
    }

    @Test
    public void testVVWV() {
        assertSize(1, KeyPathTest.V[0], KeyPathTest.V[1], KeyPathTest.W, KeyPathTest.V[3]);
    }

    @Test
    public void testVVVW() {
        assertSize(2, KeyPathTest.V[0], KeyPathTest.V[1], KeyPathTest.V[2], KeyPathTest.W);
    }

    // </editor-fold>
    // <editor-fold desc="Two Wildcards">
    @Test
    public void testWWVV() {
        assertSize(4, KeyPathTest.W, KeyPathTest.W, KeyPathTest.V[2], KeyPathTest.V[3]);
    }

    @Test
    public void testWVWV() {
        assertSize(2, KeyPathTest.W, KeyPathTest.V[1], KeyPathTest.W, KeyPathTest.V[3]);
    }

    @Test
    public void testWVVW() {
        assertSize(4, KeyPathTest.W, KeyPathTest.V[1], KeyPathTest.V[2], KeyPathTest.W);
    }

    @Test
    public void testWWIV() {
        assertSize(0, KeyPathTest.W, KeyPathTest.W, KeyPathTest.I, KeyPathTest.V[3]);
    }

    @Test
    public void testWWVI() {
        assertSize(0, KeyPathTest.W, KeyPathTest.W, KeyPathTest.V[2], KeyPathTest.I);
    }

    @Test
    public void testWVW() {
        assertSize(2, KeyPathTest.W, KeyPathTest.V[1], KeyPathTest.W);
    }

    // </editor-fold>
    // <editor-fold desc="Three Wildcards">
    @Test
    public void testWWW() {
        assertSize(4, KeyPathTest.W, KeyPathTest.W, KeyPathTest.W);
    }

    @Test
    public void testWWWV() {
        assertSize(4, KeyPathTest.W, KeyPathTest.W, KeyPathTest.W, KeyPathTest.V[3]);
    }

    @Test
    public void testWWWI() {
        assertSize(0, KeyPathTest.W, KeyPathTest.W, KeyPathTest.W, KeyPathTest.I);
    }

    // </editor-fold>
    // <editor-fold desc="Four Wildcards">
    @Test
    public void testWWWW() {
        assertSize(8, KeyPathTest.W, KeyPathTest.W, KeyPathTest.W, KeyPathTest.W);
    }

    // </editor-fold>
    // <editor-fold desc="One Globstar">
    @Test
    public void testG() {
        assertSize(18, KeyPathTest.G);
    }

    @Test
    public void testGI() {
        assertSize(0, KeyPathTest.G, KeyPathTest.I);
    }

    @Test
    public void testGV0() {
        assertSize(1, KeyPathTest.G, KeyPathTest.V[0]);
    }

    @Test
    public void testGV0V0() {
        assertSize(0, KeyPathTest.G, KeyPathTest.V[0], KeyPathTest.V[0]);
    }

    @Test
    public void testGV1() {
        assertSize(2, KeyPathTest.G, KeyPathTest.V[1]);
    }

    @Test
    public void testGV2() {
        assertSize(4, KeyPathTest.G, KeyPathTest.V[2]);
    }

    @Test
    public void testGV3() {
        assertSize(4, KeyPathTest.G, KeyPathTest.V[3]);
    }

    // </editor-fold>
    // <editor-fold desc="Two Globstars">
    @Test
    public void testGV0G() {
        assertSize(9, KeyPathTest.G, KeyPathTest.V[0], KeyPathTest.G);
    }

    @Test
    public void testGV1G() {
        assertSize(8, KeyPathTest.G, KeyPathTest.V[1], KeyPathTest.G);
    }

    @Test
    public void testGV2G() {
        assertSize(12, KeyPathTest.G, KeyPathTest.V[2], KeyPathTest.G);
    }

    @Test
    public void testGIG() {
        assertSize(0, KeyPathTest.G, KeyPathTest.I, KeyPathTest.G);
    }

    // </editor-fold>
    // <editor-fold desc="Wildcard and Globstar">
    @Test
    public void testWG() {
        assertSize(18, KeyPathTest.W, KeyPathTest.G);
    }

    @Test
    public void testGV0W() {
        assertSize(2, KeyPathTest.G, KeyPathTest.V[0], KeyPathTest.W);
    }

    @Test
    public void testWV0I() {
        assertSize(0, KeyPathTest.W, KeyPathTest.V[0], KeyPathTest.I);
    }

    @Test
    public void testGV1W() {
        assertSize(2, KeyPathTest.G, KeyPathTest.V[1], KeyPathTest.W);
    }

    @Test
    public void testWV1I() {
        assertSize(0, KeyPathTest.W, KeyPathTest.V[1], KeyPathTest.I);
    }

    @Test
    public void testGV2W() {
        assertSize(8, KeyPathTest.G, KeyPathTest.V[2], KeyPathTest.W);
    }

    @Test
    public void testWV2I() {
        assertSize(0, KeyPathTest.W, KeyPathTest.V[2], KeyPathTest.I);
    }
}

