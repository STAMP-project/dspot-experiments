package junit.samples.money;


import junit.framework.TestCase;


public class MoneyTest extends TestCase {
    private Money f12CHF;

    private Money f14CHF;

    private Money f7USD;

    private Money f21USD;

    private IMoney fMB1;

    private IMoney fMB2;

    public void testBagMultiply() {
        // {[12 CHF][7 USD]} *2 == {[24 CHF][14 USD]}
        IMoney expected = MoneyBag.create(new Money(24, "CHF"), new Money(14, "USD"));
        TestCase.assertEquals(expected, fMB1.multiply(2));
        TestCase.assertEquals(fMB1, fMB1.multiply(1));
        TestCase.assertTrue(fMB1.multiply(0).isZero());
    }

    public void testBagNegate() {
        // {[12 CHF][7 USD]} negate == {[-12 CHF][-7 USD]}
        IMoney expected = MoneyBag.create(new Money((-12), "CHF"), new Money((-7), "USD"));
        TestCase.assertEquals(expected, fMB1.negate());
    }

    public void testBagSimpleAdd() {
        // {[12 CHF][7 USD]} + [14 CHF] == {[26 CHF][7 USD]}
        IMoney expected = MoneyBag.create(new Money(26, "CHF"), new Money(7, "USD"));
        TestCase.assertEquals(expected, fMB1.add(f14CHF));
    }

    public void testBagSubtract() {
        // {[12 CHF][7 USD]} - {[14 CHF][21 USD] == {[-2 CHF][-14 USD]}
        IMoney expected = MoneyBag.create(new Money((-2), "CHF"), new Money((-14), "USD"));
        TestCase.assertEquals(expected, fMB1.subtract(fMB2));
    }

    public void testBagSumAdd() {
        // {[12 CHF][7 USD]} + {[14 CHF][21 USD]} == {[26 CHF][28 USD]}
        IMoney expected = MoneyBag.create(new Money(26, "CHF"), new Money(28, "USD"));
        TestCase.assertEquals(expected, fMB1.add(fMB2));
    }

    public void testIsZero() {
        TestCase.assertTrue(fMB1.subtract(fMB1).isZero());
        TestCase.assertTrue(MoneyBag.create(new Money(0, "CHF"), new Money(0, "USD")).isZero());
    }

    public void testMixedSimpleAdd() {
        // [12 CHF] + [7 USD] == {[12 CHF][7 USD]}
        IMoney expected = MoneyBag.create(f12CHF, f7USD);
        TestCase.assertEquals(expected, f12CHF.add(f7USD));
    }

    public void testBagNotEquals() {
        IMoney bag = MoneyBag.create(f12CHF, f7USD);
        TestCase.assertFalse(bag.equals(new Money(12, "DEM").add(f7USD)));
    }

    public void testMoneyBagEquals() {
        TestCase.assertTrue((!(fMB1.equals(null))));
        TestCase.assertEquals(fMB1, fMB1);
        IMoney equal = MoneyBag.create(new Money(12, "CHF"), new Money(7, "USD"));
        TestCase.assertTrue(fMB1.equals(equal));
        TestCase.assertTrue((!(fMB1.equals(f12CHF))));
        TestCase.assertTrue((!(f12CHF.equals(fMB1))));
        TestCase.assertTrue((!(fMB1.equals(fMB2))));
    }

    public void testMoneyBagHash() {
        IMoney equal = MoneyBag.create(new Money(12, "CHF"), new Money(7, "USD"));
        TestCase.assertEquals(fMB1.hashCode(), equal.hashCode());
    }

    public void testMoneyEquals() {
        TestCase.assertTrue((!(f12CHF.equals(null))));
        Money equalMoney = new Money(12, "CHF");
        TestCase.assertEquals(f12CHF, f12CHF);
        TestCase.assertEquals(f12CHF, equalMoney);
        TestCase.assertEquals(f12CHF.hashCode(), equalMoney.hashCode());
        TestCase.assertTrue((!(f12CHF.equals(f14CHF))));
    }

    public void testMoneyHash() {
        TestCase.assertTrue((!(f12CHF.equals(null))));
        Money equal = new Money(12, "CHF");
        TestCase.assertEquals(f12CHF.hashCode(), equal.hashCode());
    }

    public void testSimplify() {
        IMoney money = MoneyBag.create(new Money(26, "CHF"), new Money(28, "CHF"));
        TestCase.assertEquals(new Money(54, "CHF"), money);
    }

    public void testNormalize2() {
        // {[12 CHF][7 USD]} - [12 CHF] == [7 USD]
        Money expected = new Money(7, "USD");
        TestCase.assertEquals(expected, fMB1.subtract(f12CHF));
    }

    public void testNormalize3() {
        // {[12 CHF][7 USD]} - {[12 CHF][3 USD]} == [4 USD]
        IMoney ms1 = MoneyBag.create(new Money(12, "CHF"), new Money(3, "USD"));
        Money expected = new Money(4, "USD");
        TestCase.assertEquals(expected, fMB1.subtract(ms1));
    }

    public void testNormalize4() {
        // [12 CHF] - {[12 CHF][3 USD]} == [-3 USD]
        IMoney ms1 = MoneyBag.create(new Money(12, "CHF"), new Money(3, "USD"));
        Money expected = new Money((-3), "USD");
        TestCase.assertEquals(expected, f12CHF.subtract(ms1));
    }

    public void testPrint() {
        TestCase.assertEquals("[12 CHF]", f12CHF.toString());
    }

    public void testSimpleAdd() {
        // [12 CHF] + [14 CHF] == [26 CHF]
        Money expected = new Money(26, "CHF");
        TestCase.assertEquals(expected, f12CHF.add(f14CHF));
    }

    public void testSimpleBagAdd() {
        // [14 CHF] + {[12 CHF][7 USD]} == {[26 CHF][7 USD]}
        IMoney expected = MoneyBag.create(new Money(26, "CHF"), new Money(7, "USD"));
        TestCase.assertEquals(expected, f14CHF.add(fMB1));
    }

    public void testSimpleMultiply() {
        // [14 CHF] *2 == [28 CHF]
        Money expected = new Money(28, "CHF");
        TestCase.assertEquals(expected, f14CHF.multiply(2));
    }

    public void testSimpleNegate() {
        // [14 CHF] negate == [-14 CHF]
        Money expected = new Money((-14), "CHF");
        TestCase.assertEquals(expected, f14CHF.negate());
    }

    public void testSimpleSubtract() {
        // [14 CHF] - [12 CHF] == [2 CHF]
        Money expected = new Money(2, "CHF");
        TestCase.assertEquals(expected, f14CHF.subtract(f12CHF));
    }
}

