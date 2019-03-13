/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.dfa;


import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import org.junit.Assert;
import org.junit.Test;


/* Created on 18.08.2004 */
public class AcceptanceTest {
    @Test
    public void testbook() {
        ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.FOO);
    }

    private static final String FOO = ((((((("class Foo {" + (PMD.EOL)) + " void bar() {") + (PMD.EOL)) + "  int x = 2;") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    @Test
    public void testLabelledBreakLockup() {
        ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.LABELLED_BREAK_LOCKUP);
    }

    private static final String LABELLED_BREAK_LOCKUP = ((((((((((("class Foo {" + (PMD.EOL)) + " void bar(int x) {") + (PMD.EOL)) + "  here: if (x>2) {") + (PMD.EOL)) + "   break here;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    @Test
    public void test1() {
        Assert.assertTrue(check(AcceptanceTest.TEST1_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST1)));
    }

    @Test
    public void test2() {
        Assert.assertTrue(check(AcceptanceTest.TEST2_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST2)));
    }

    @Test
    public void test3() {
        Assert.assertTrue(check(AcceptanceTest.TEST3_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST3)));
    }

    @Test
    public void test4() {
        Assert.assertTrue(check(AcceptanceTest.TEST4_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST4)));
    }

    @Test
    public void test6() {
        Assert.assertTrue(check(AcceptanceTest.TEST5_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST6)));
    }

    @Test
    public void test7() {
        Assert.assertTrue(check(AcceptanceTest.TEST5_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST7)));
    }

    @Test
    public void test8() {
        Assert.assertTrue(check(AcceptanceTest.TEST8_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST8)));
    }

    @Test
    public void test9() {
        Assert.assertTrue(check(AcceptanceTest.TEST5_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST9)));
    }

    @Test
    public void test10() {
        Assert.assertTrue(check(AcceptanceTest.TEST8_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST10)));
    }

    @Test
    public void test11() {
        Assert.assertTrue(check(AcceptanceTest.TEST8_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST11)));
    }

    @Test
    public void test12() {
        Assert.assertTrue(check(AcceptanceTest.TEST12_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST12)));
    }

    @Test
    public void test13() {
        Assert.assertTrue(check(AcceptanceTest.TEST13_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST13)));
    }

    @Test
    public void test14() {
        Assert.assertTrue(check(AcceptanceTest.TEST14_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST14)));
    }

    @Test
    public void test15() {
        Assert.assertTrue(check(AcceptanceTest.TEST15_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST15)));
    }

    @Test
    public void test16() {
        Assert.assertTrue(check(AcceptanceTest.TEST16_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST16)));
    }

    @Test
    public void test17() {
        Assert.assertTrue(check(AcceptanceTest.TEST17_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST17)));
    }

    @Test
    public void test18() {
        Assert.assertTrue(check(AcceptanceTest.TEST18_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST18)));
    }

    @Test
    public void test19() {
        Assert.assertTrue(check(AcceptanceTest.TEST19_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST19)));
    }

    @Test
    public void test20() {
        Assert.assertTrue(check(AcceptanceTest.TEST20_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST20)));
    }

    @Test
    public void test21() {
        Assert.assertTrue(check(AcceptanceTest.TEST21_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST21)));
    }

    @Test
    public void test22() {
        Assert.assertTrue(check(AcceptanceTest.TEST22_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST22)));
    }

    @Test
    public void test23() {
        Assert.assertTrue(check(AcceptanceTest.TEST23_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST23)));
    }

    @Test
    public void test24() {
        Assert.assertTrue(check(AcceptanceTest.TEST24_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST24)));
    }

    @Test
    public void test25() {
        Assert.assertTrue(check(AcceptanceTest.TEST25_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST25)));
    }

    @Test
    public void test26() {
        Assert.assertTrue(check(AcceptanceTest.TEST26_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST26)));
    }

    @Test
    public void test27() {
        Assert.assertTrue(check(AcceptanceTest.TEST27_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST27)));
    }

    @Test
    public void test28() {
        Assert.assertTrue(check(AcceptanceTest.TEST28_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST28)));
    }

    @Test
    public void test29() {
        Assert.assertTrue(check(AcceptanceTest.TEST29_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST29)));
    }

    @Test
    public void test30() {
        Assert.assertTrue(check(AcceptanceTest.TEST30_NODES, ParserTstUtil.getOrderedNodes(ASTMethodDeclarator.class, AcceptanceTest.TEST30)));
    }

    // first dimension: the index of a node
    // second dimension: the indices of the children
    private static final int[][] TEST1_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 4, 6 }, new int[]{ 5 }, new int[]{ 6 }, new int[]{  } };

    private static final String TEST1 = ((((((((((((((("class Foo {" + (PMD.EOL)) + " void test_1() {") + (PMD.EOL)) + "  int x = 0;") + (PMD.EOL)) + "  if (x == 0) {") + (PMD.EOL)) + "   x++;") + (PMD.EOL)) + "   x = 0;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST2_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 5, 7 }, new int[]{ 3 }, new int[]{ 6 }, new int[]{ 4 }, new int[]{  } };

    private static final String TEST2 = ((((((((((((("class Foo {" + (PMD.EOL)) + " public void test_2() {") + (PMD.EOL)) + "  for (int i = 0; i < 1; i++) {") + (PMD.EOL)) + "   i++;") + (PMD.EOL)) + "   i = 8;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST3_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 4, 5 }, new int[]{ 3 }, new int[]{  } };

    private static final String TEST3 = ((((((((("public class Foo {" + (PMD.EOL)) + " public void test_3() {") + (PMD.EOL)) + "  for (int i = 0; i < 1; i++) {") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST4_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{  } };

    private static final String TEST4 = ((((((((("public class Foo {" + (PMD.EOL)) + " public void test_4() {") + (PMD.EOL)) + "  for (; ;) {") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST5_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 4 }, new int[]{  } };

    private static final String TEST6 = ((((((((("public class Foo {" + (PMD.EOL)) + " public void test_6() {") + (PMD.EOL)) + "  for (int i = 0; ;) {") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final String TEST7 = ((((((((("public class Foo {" + (PMD.EOL)) + " public void test_7() {") + (PMD.EOL)) + "  for (int i = 0; i < 0;) {") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST8_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 4, 5 }, new int[]{ 3 }, new int[]{  } };

    public static final String TEST8 = ((((((((("public class Foo {" + (PMD.EOL)) + " public void test_8() {") + (PMD.EOL)) + "  for (int i = 0; ; i++) {") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    public static final String TEST9 = ((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_9() {") + (PMD.EOL)) + "  int i = 0;") + (PMD.EOL)) + "  for (; i < 0;) {") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    public static final String TEST10 = ((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_10() {") + (PMD.EOL)) + "  int i = 0;") + (PMD.EOL)) + "  for (; i < 0; i++) {") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    public static final String TEST11 = ((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_11() {") + (PMD.EOL)) + "  int i = 0;") + (PMD.EOL)) + "  for (; ; i++) {") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST12_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 4, 5 }, new int[]{ 3 }, new int[]{  } };

    public static final String TEST12 = ((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_12() {") + (PMD.EOL)) + "  for (; ;) {") + (PMD.EOL)) + "   int i = 0;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST13_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 5, 9 }, new int[]{ 3 }, new int[]{ 6 }, new int[]{ 7, 8 }, new int[]{ 8 }, new int[]{ 4 }, new int[]{  } };

    public static final String TEST13 = ((((((((((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_13() {") + (PMD.EOL)) + "  for (int i = 0; i < 0; i++) {") + (PMD.EOL)) + "   i = 9;") + (PMD.EOL)) + "   if (i < 8) {") + (PMD.EOL)) + "    i = 7;") + (PMD.EOL)) + "   }") + (PMD.EOL)) + "   i = 6;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST14_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 5, 8 }, new int[]{ 3 }, new int[]{ 6 }, new int[]{ 7, 4 }, new int[]{ 4 }, new int[]{  } };

    public static final String TEST14 = ((((((((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_14() {") + (PMD.EOL)) + "  for (int i = 0; i < 0; i++) {") + (PMD.EOL)) + "   i = 9;") + (PMD.EOL)) + "   if (i < 8) {") + (PMD.EOL)) + "    i = 7;") + (PMD.EOL)) + "   }") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST15_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 5, 7 }, new int[]{ 3 }, new int[]{ 6, 4 }, new int[]{ 4 }, new int[]{  } };

    public static final String TEST15 = ((((((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_15() {") + (PMD.EOL)) + "  for (int i = 0; i < 0; i++) {") + (PMD.EOL)) + "   if (i < 8) {") + (PMD.EOL)) + "    i = 7;") + (PMD.EOL)) + "   }") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST16_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 5, 8 }, new int[]{ 3 }, new int[]{ 6, 7 }, new int[]{ 4 }, new int[]{ 4 }, new int[]{  } };

    public static final String TEST16 = ((((((((((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_16() {") + (PMD.EOL)) + "  for (int i = 0; i < 0; i++) {") + (PMD.EOL)) + "   if (i < 8) {") + (PMD.EOL)) + "    i = 7;") + (PMD.EOL)) + "   } else {") + (PMD.EOL)) + "    i = 6;") + (PMD.EOL)) + "   }") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST17_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 5, 10 }, new int[]{ 3 }, new int[]{ 6, 7 }, new int[]{ 4 }, new int[]{ 8, 9 }, new int[]{ 4 }, new int[]{ 4 }, new int[]{  } };

    public static final String TEST17 = ((((((((((((((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_17() {") + (PMD.EOL)) + "  for (int i = 0; i < 0; i++) {") + (PMD.EOL)) + "   if (i < 6) {") + (PMD.EOL)) + "    i = 7;") + (PMD.EOL)) + "   } else if (i > 8) {") + (PMD.EOL)) + "    i = 9;") + (PMD.EOL)) + "   } else {") + (PMD.EOL)) + "    i = 10;") + (PMD.EOL)) + "   }") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST18_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 5, 9 }, new int[]{ 3 }, new int[]{ 6 }, new int[]{ 8, 4 }, new int[]{ 6 }, new int[]{ 7 }, new int[]{  } };

    public static final String TEST18 = ((((((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_18() {") + (PMD.EOL)) + "  for (int i = 0; i < 0; i++) {") + (PMD.EOL)) + "   for (int j = 0; j < 0; j++) {") + (PMD.EOL)) + "    j++;") + (PMD.EOL)) + "   }") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST19_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 4, 5 }, new int[]{ 10 }, new int[]{ 6, 7 }, new int[]{ 10 }, new int[]{ 8, 9 }, new int[]{ 10 }, new int[]{ 10 }, new int[]{  } };

    public static final String TEST19 = ((((((((((((((((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_19() {") + (PMD.EOL)) + "  int i = 0;") + (PMD.EOL)) + "  if (i == 1) {") + (PMD.EOL)) + "   i = 2;") + (PMD.EOL)) + "  } else if (i == 3) {") + (PMD.EOL)) + "   i = 4;") + (PMD.EOL)) + "  } else if (i == 5) {") + (PMD.EOL)) + "   i = 6;") + (PMD.EOL)) + "  } else {") + (PMD.EOL)) + "   i = 7;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST20_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 4, 6 }, new int[]{ 5, 7 }, new int[]{ 7 }, new int[]{ 7 }, new int[]{  } };

    public static final String TEST20 = ((((((((((((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_20() {") + (PMD.EOL)) + "  int i = 0;") + (PMD.EOL)) + "  if (i == 1) {") + (PMD.EOL)) + "   if (i == 2) {") + (PMD.EOL)) + "    i = 3;") + (PMD.EOL)) + "   }") + (PMD.EOL)) + "  } else {") + (PMD.EOL)) + "   i = 7;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST21_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 4, 9 }, new int[]{ 5 }, new int[]{ 7, 8 }, new int[]{ 5 }, new int[]{ 6 }, new int[]{ 11 }, new int[]{ 10, 11 }, new int[]{ 11 }, new int[]{  } };

    public static final String TEST21 = ((((((((((((((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_21() {") + (PMD.EOL)) + "  int i = 0;") + (PMD.EOL)) + "  if (i == 1) {") + (PMD.EOL)) + "   for (i = 3; i < 4; i++) {") + (PMD.EOL)) + "    i = 5;") + (PMD.EOL)) + "   }") + (PMD.EOL)) + "   i++;") + (PMD.EOL)) + "  } else if (i < 6) {") + (PMD.EOL)) + "   i = 7;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST22_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 4, 8 }, new int[]{ 5 }, new int[]{ 7, 9 }, new int[]{ 5 }, new int[]{ 6 }, new int[]{ 9 }, new int[]{  } };

    public static final String TEST22 = ((((((((((((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_22() {") + (PMD.EOL)) + "  int i = 0;") + (PMD.EOL)) + "  if (i == 1) {") + (PMD.EOL)) + "   for (i = 3; i < 4; i++) {") + (PMD.EOL)) + "    i = 5;") + (PMD.EOL)) + "   }") + (PMD.EOL)) + "  } else {") + (PMD.EOL)) + "   i = 7;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST23_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 4, 8 }, new int[]{ 5 }, new int[]{ 7, 10 }, new int[]{ 5 }, new int[]{ 6 }, new int[]{ 9, 10 }, new int[]{ 10 }, new int[]{  } };

    public static final String TEST23 = ((((((((((((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_23() {") + (PMD.EOL)) + "  int i = 0;") + (PMD.EOL)) + "  if (i == 1) {") + (PMD.EOL)) + "   for (i = 3; i < 4; i++) {") + (PMD.EOL)) + "    i = 5;") + (PMD.EOL)) + "   }") + (PMD.EOL)) + "  } else if (i < 6) {") + (PMD.EOL)) + "   i = 7;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST24_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 4, 9 }, new int[]{ 5 }, new int[]{ 7, 11 }, new int[]{ 5 }, new int[]{ 8, 6 }, new int[]{ 6 }, new int[]{ 10, 11 }, new int[]{ 11 }, new int[]{  } };

    public static final String TEST24 = ((((((((((((((((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_24() {") + (PMD.EOL)) + "  int x = 0;") + (PMD.EOL)) + "  if (x > 2) {") + (PMD.EOL)) + "   for (int i = 0; i < 1; i++) {") + (PMD.EOL)) + "    if (x > 3) {") + (PMD.EOL)) + "     x++;") + (PMD.EOL)) + "    }") + (PMD.EOL)) + "   }") + (PMD.EOL)) + "  } else if (x > 4) {") + (PMD.EOL)) + "   x++;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST25_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 4, 5 }, new int[]{ 5 }, new int[]{  } };

    public static final String TEST25 = ((((((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_25() {") + (PMD.EOL)) + "  int x = 0;") + (PMD.EOL)) + "  switch (x) {") + (PMD.EOL)) + "   default:") + (PMD.EOL)) + "    x = 9;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST26_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 4 }, new int[]{ 5, 6 }, new int[]{ 6 }, new int[]{ 7 }, new int[]{ 8, 3 }, new int[]{ 9 }, new int[]{  } };

    public static final String TEST26 = ((((((((((((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_26() {") + (PMD.EOL)) + "  int x = 0;") + (PMD.EOL)) + "  do {") + (PMD.EOL)) + "   if (x > 0) {") + (PMD.EOL)) + "    x++;") + (PMD.EOL)) + "   }") + (PMD.EOL)) + "   x++;") + (PMD.EOL)) + "  } while (x < 9);") + (PMD.EOL)) + "  x++;") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST27_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 5, 9 }, new int[]{ 3 }, new int[]{ 6 }, new int[]{ 7 }, new int[]{ 8 }, new int[]{ 6, 4 }, new int[]{  } };

    public static final String TEST27 = ((((((((((((((((("public class Foo {" + (PMD.EOL)) + " public void test_27() {") + (PMD.EOL)) + "  for (int i = 0; i < 36; i++) {") + (PMD.EOL)) + "   int x = 0;") + (PMD.EOL)) + "   do {") + (PMD.EOL)) + "    x++;") + (PMD.EOL)) + "   } while (x < 9);") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST28_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 5, 14 }, new int[]{ 3 }, new int[]{ 6 }, new int[]{ 7 }, new int[]{ 8, 12 }, new int[]{ 9 }, new int[]{ 10, 12 }, new int[]{ 11 }, new int[]{ 12 }, new int[]{ 13 }, new int[]{ 6, 4 }, new int[]{  } };

    public static final String TEST28 = ((((((((((((((((((((((((((((((((("public class Foo {" + (PMD.EOL)) + " private void test_28() {") + (PMD.EOL)) + "  for (int i = 0; i < 36; i++) {") + (PMD.EOL)) + "   int x = 0;") + (PMD.EOL)) + "   do {") + (PMD.EOL)) + "    if (x > 0) {") + (PMD.EOL)) + "     x++;") + (PMD.EOL)) + "     switch (i) {") + (PMD.EOL)) + "      case 0:") + (PMD.EOL)) + "       x = 0;") + (PMD.EOL)) + "       break;") + (PMD.EOL)) + "     }") + (PMD.EOL)) + "    }") + (PMD.EOL)) + "    x++;") + (PMD.EOL)) + "   } while (x < 9);") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + " }";

    private static final int[][] TEST29_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3, 4, 5 }, new int[]{ 6 }, new int[]{ 6 }, new int[]{ 6 }, new int[]{  } };

    public static final String TEST29 = ((((((((((((((((((((("public class Foo {" + (PMD.EOL)) + " private void test_29() {") + (PMD.EOL)) + "  switch(x) {") + (PMD.EOL)) + "   case 1:") + (PMD.EOL)) + "    break; ") + (PMD.EOL)) + "   default: ") + (PMD.EOL)) + "    break;") + (PMD.EOL)) + "   case 2:") + (PMD.EOL)) + "    break;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";

    private static final int[][] TEST30_NODES = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 }, new int[]{ 4, 7 }, new int[]{ 5, 6 }, new int[]{ 4 }, new int[]{ 3 }, new int[]{  } };

    public static final String TEST30 = ((((((((((((((((((("public class Foo {" + (PMD.EOL)) + " private void test_30() {") + (PMD.EOL)) + "  int x = 0;") + (PMD.EOL)) + "  while (true) {") + (PMD.EOL)) + "   while (x>0) {") + (PMD.EOL)) + "     x++;") + (PMD.EOL)) + "   }") + (PMD.EOL)) + "   continue;") + (PMD.EOL)) + "  }") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}";
}

