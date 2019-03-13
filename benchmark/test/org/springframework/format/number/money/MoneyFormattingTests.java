/**
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.format.number.money;


import NumberFormat.Style;
import java.util.Locale;
import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.validation.DataBinder;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 4.2
 */
public class MoneyFormattingTests {
    private final FormattingConversionService conversionService = new DefaultFormattingConversionService();

    @Test
    public void testAmountAndUnit() {
        MoneyFormattingTests.MoneyHolder bean = new MoneyFormattingTests.MoneyHolder();
        DataBinder binder = new DataBinder(bean);
        binder.setConversionService(conversionService);
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("amount", "USD 10.50");
        propertyValues.add("unit", "USD");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("USD10.50", binder.getBindingResult().getFieldValue("amount"));
        Assert.assertEquals("USD", binder.getBindingResult().getFieldValue("unit"));
        Assert.assertTrue(((bean.getAmount().getNumber().doubleValue()) == 10.5));
        Assert.assertEquals("USD", bean.getAmount().getCurrency().getCurrencyCode());
        LocaleContextHolder.setLocale(Locale.CANADA);
        binder.bind(propertyValues);
        LocaleContextHolder.setLocale(Locale.US);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("USD10.50", binder.getBindingResult().getFieldValue("amount"));
        Assert.assertEquals("USD", binder.getBindingResult().getFieldValue("unit"));
        Assert.assertTrue(((bean.getAmount().getNumber().doubleValue()) == 10.5));
        Assert.assertEquals("USD", bean.getAmount().getCurrency().getCurrencyCode());
    }

    @Test
    public void testAmountWithNumberFormat1() {
        MoneyFormattingTests.FormattedMoneyHolder1 bean = new MoneyFormattingTests.FormattedMoneyHolder1();
        DataBinder binder = new DataBinder(bean);
        binder.setConversionService(conversionService);
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("amount", "$10.50");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("$10.50", binder.getBindingResult().getFieldValue("amount"));
        Assert.assertTrue(((bean.getAmount().getNumber().doubleValue()) == 10.5));
        Assert.assertEquals("USD", bean.getAmount().getCurrency().getCurrencyCode());
        LocaleContextHolder.setLocale(Locale.CANADA);
        binder.bind(propertyValues);
        LocaleContextHolder.setLocale(Locale.US);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("$10.50", binder.getBindingResult().getFieldValue("amount"));
        Assert.assertTrue(((bean.getAmount().getNumber().doubleValue()) == 10.5));
        Assert.assertEquals("CAD", bean.getAmount().getCurrency().getCurrencyCode());
    }

    @Test
    public void testAmountWithNumberFormat2() {
        MoneyFormattingTests.FormattedMoneyHolder2 bean = new MoneyFormattingTests.FormattedMoneyHolder2();
        DataBinder binder = new DataBinder(bean);
        binder.setConversionService(conversionService);
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("amount", "10.50");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("10.5", binder.getBindingResult().getFieldValue("amount"));
        Assert.assertTrue(((bean.getAmount().getNumber().doubleValue()) == 10.5));
        Assert.assertEquals("USD", bean.getAmount().getCurrency().getCurrencyCode());
    }

    @Test
    public void testAmountWithNumberFormat3() {
        MoneyFormattingTests.FormattedMoneyHolder3 bean = new MoneyFormattingTests.FormattedMoneyHolder3();
        DataBinder binder = new DataBinder(bean);
        binder.setConversionService(conversionService);
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("amount", "10%");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("10%", binder.getBindingResult().getFieldValue("amount"));
        Assert.assertTrue(((bean.getAmount().getNumber().doubleValue()) == 0.1));
        Assert.assertEquals("USD", bean.getAmount().getCurrency().getCurrencyCode());
    }

    @Test
    public void testAmountWithNumberFormat4() {
        MoneyFormattingTests.FormattedMoneyHolder4 bean = new MoneyFormattingTests.FormattedMoneyHolder4();
        DataBinder binder = new DataBinder(bean);
        binder.setConversionService(conversionService);
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("amount", "010.500");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("010.500", binder.getBindingResult().getFieldValue("amount"));
        Assert.assertTrue(((bean.getAmount().getNumber().doubleValue()) == 10.5));
        Assert.assertEquals("USD", bean.getAmount().getCurrency().getCurrencyCode());
    }

    @Test
    public void testAmountWithNumberFormat5() {
        MoneyFormattingTests.FormattedMoneyHolder5 bean = new MoneyFormattingTests.FormattedMoneyHolder5();
        DataBinder binder = new DataBinder(bean);
        binder.setConversionService(conversionService);
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("amount", "USD 10.50");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("USD 010.500", binder.getBindingResult().getFieldValue("amount"));
        Assert.assertTrue(((bean.getAmount().getNumber().doubleValue()) == 10.5));
        Assert.assertEquals("USD", bean.getAmount().getCurrency().getCurrencyCode());
        LocaleContextHolder.setLocale(Locale.CANADA);
        binder.bind(propertyValues);
        LocaleContextHolder.setLocale(Locale.US);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("USD 010.500", binder.getBindingResult().getFieldValue("amount"));
        Assert.assertTrue(((bean.getAmount().getNumber().doubleValue()) == 10.5));
        Assert.assertEquals("USD", bean.getAmount().getCurrency().getCurrencyCode());
    }

    public static class MoneyHolder {
        private MonetaryAmount amount;

        private CurrencyUnit unit;

        public MonetaryAmount getAmount() {
            return amount;
        }

        public void setAmount(MonetaryAmount amount) {
            this.amount = amount;
        }

        public CurrencyUnit getUnit() {
            return unit;
        }

        public void setUnit(CurrencyUnit unit) {
            this.unit = unit;
        }
    }

    public static class FormattedMoneyHolder1 {
        @NumberFormat
        private MonetaryAmount amount;

        public MonetaryAmount getAmount() {
            return amount;
        }

        public void setAmount(MonetaryAmount amount) {
            this.amount = amount;
        }
    }

    public static class FormattedMoneyHolder2 {
        @NumberFormat(style = Style.NUMBER)
        private MonetaryAmount amount;

        public MonetaryAmount getAmount() {
            return amount;
        }

        public void setAmount(MonetaryAmount amount) {
            this.amount = amount;
        }
    }

    public static class FormattedMoneyHolder3 {
        @NumberFormat(style = Style.PERCENT)
        private MonetaryAmount amount;

        public MonetaryAmount getAmount() {
            return amount;
        }

        public void setAmount(MonetaryAmount amount) {
            this.amount = amount;
        }
    }

    public static class FormattedMoneyHolder4 {
        @NumberFormat(pattern = "#000.000#")
        private MonetaryAmount amount;

        public MonetaryAmount getAmount() {
            return amount;
        }

        public void setAmount(MonetaryAmount amount) {
            this.amount = amount;
        }
    }

    public static class FormattedMoneyHolder5 {
        @NumberFormat(pattern = "\u00a4\u00a4 #000.000#")
        private MonetaryAmount amount;

        public MonetaryAmount getAmount() {
            return amount;
        }

        public void setAmount(MonetaryAmount amount) {
            this.amount = amount;
        }
    }
}

