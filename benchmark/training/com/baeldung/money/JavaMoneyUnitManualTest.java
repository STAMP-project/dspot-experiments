package com.baeldung.money;


import CurrencyStyle.NAME;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.UnknownCurrencyException;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.junit.Assert;
import org.junit.Test;


public class JavaMoneyUnitManualTest {
    @Test
    public void givenCurrencyCode_whenString_thanExist() {
        CurrencyUnit usd = Monetary.getCurrency("USD");
        Assert.assertNotNull(usd);
        Assert.assertEquals(usd.getCurrencyCode(), "USD");
        Assert.assertEquals(usd.getNumericCode(), 840);
        Assert.assertEquals(usd.getDefaultFractionDigits(), 2);
    }

    @Test(expected = UnknownCurrencyException.class)
    public void givenCurrencyCode_whenNoExist_thanThrowsError() {
        Monetary.getCurrency("AAA");
    }

    @Test
    public void givenAmounts_whenStringified_thanEquals() {
        CurrencyUnit usd = Monetary.getCurrency("USD");
        MonetaryAmount fstAmtUSD = Monetary.getDefaultAmountFactory().setCurrency(usd).setNumber(200).create();
        Money moneyof = Money.of(12, usd);
        FastMoney fastmoneyof = FastMoney.of(2, usd);
        Assert.assertEquals("USD", usd.toString());
        Assert.assertEquals("USD 200", fstAmtUSD.toString());
        Assert.assertEquals("USD 12", moneyof.toString());
        Assert.assertEquals("USD 2.00000", fastmoneyof.toString());
    }

    @Test
    public void givenCurrencies_whenCompared_thanNotequal() {
        MonetaryAmount oneDolar = Monetary.getDefaultAmountFactory().setCurrency("USD").setNumber(1).create();
        Money oneEuro = Money.of(1, "EUR");
        Assert.assertFalse(oneEuro.equals(FastMoney.of(1, "EUR")));
        Assert.assertTrue(oneDolar.equals(Money.of(1, "USD")));
    }

    @Test(expected = ArithmeticException.class)
    public void givenAmount_whenDivided_thanThrowsException() {
        MonetaryAmount oneDolar = Monetary.getDefaultAmountFactory().setCurrency("USD").setNumber(1).create();
        oneDolar.divide(3);
        Assert.fail();// if no exception

    }

    @Test
    public void givenAmounts_whenSummed_thanCorrect() {
        List<MonetaryAmount> monetaryAmounts = Arrays.asList(Money.of(100, "CHF"), Money.of(10.2, "CHF"), Money.of(1.15, "CHF"));
        Money sumAmtCHF = ((Money) (monetaryAmounts.stream().reduce(Money.of(0, "CHF"), MonetaryAmount::add)));
        Assert.assertEquals("CHF 111.35", sumAmtCHF.toString());
    }

    @Test
    public void givenArithmetic_whenStringified_thanEqualsAmount() {
        CurrencyUnit usd = Monetary.getCurrency("USD");
        Money moneyof = Money.of(12, usd);
        MonetaryAmount fstAmtUSD = Monetary.getDefaultAmountFactory().setCurrency(usd).setNumber(200.5).create();
        MonetaryAmount oneDolar = Monetary.getDefaultAmountFactory().setCurrency("USD").setNumber(1).create();
        Money subtractedAmount = Money.of(1, "USD").subtract(fstAmtUSD);
        MonetaryAmount multiplyAmount = oneDolar.multiply(0.25);
        MonetaryAmount divideAmount = oneDolar.divide(0.25);
        Assert.assertEquals("USD", usd.toString());
        Assert.assertEquals("USD 1", oneDolar.toString());
        Assert.assertEquals("USD 200.5", fstAmtUSD.toString());
        Assert.assertEquals("USD 12", moneyof.toString());
        Assert.assertEquals("USD -199.5", subtractedAmount.toString());
        Assert.assertEquals("USD 0.25", multiplyAmount.toString());
        Assert.assertEquals("USD 4", divideAmount.toString());
    }

    @Test
    public void givenAmount_whenRounded_thanEquals() {
        MonetaryAmount fstAmtEUR = Monetary.getDefaultAmountFactory().setCurrency("EUR").setNumber(1.30473908).create();
        MonetaryAmount roundEUR = fstAmtEUR.with(Monetary.getDefaultRounding());
        Assert.assertEquals("EUR 1.30473908", fstAmtEUR.toString());
        Assert.assertEquals("EUR 1.3", roundEUR.toString());
    }

    @Test
    public void givenLocale_whenFormatted_thanEquals() {
        MonetaryAmount oneDollar = Monetary.getDefaultAmountFactory().setCurrency("USD").setNumber(1).create();
        MonetaryAmountFormat formatUSD = MonetaryFormats.getAmountFormat(Locale.US);
        String usFormatted = formatUSD.format(oneDollar);
        Assert.assertEquals("USD 1", oneDollar.toString());
        Assert.assertNotNull(formatUSD);
        Assert.assertEquals("USD1.00", usFormatted);
    }

    @Test
    public void givenAmount_whenCustomFormat_thanEquals() {
        MonetaryAmount oneDollar = Monetary.getDefaultAmountFactory().setCurrency("USD").setNumber(1).create();
        MonetaryAmountFormat customFormat = MonetaryFormats.getAmountFormat(AmountFormatQueryBuilder.of(Locale.US).set(NAME).set("pattern", "00000.00 US Dollar").build());
        String customFormatted = customFormat.format(oneDollar);
        Assert.assertNotNull(customFormat);
        Assert.assertEquals("USD 1", oneDollar.toString());
        Assert.assertEquals("00001.00 US Dollar", customFormatted);
    }
}

