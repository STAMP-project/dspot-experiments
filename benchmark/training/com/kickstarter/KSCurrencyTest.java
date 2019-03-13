package com.kickstarter;


import com.kickstarter.libs.KSCurrency;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.models.Project;
import java.math.RoundingMode;
import junit.framework.TestCase;


public class KSCurrencyTest extends TestCase {
    public void testFormatCurrency_withUserInUS() {
        final KSCurrency currency = KSCurrencyTest.createKSCurrency("US");
        TestCase.assertEquals("$100", currency.format(100.0F, ProjectFactory.project()));
        TestCase.assertEquals("$100 CAD", currency.format(100.0F, ProjectFactory.caProject()));
        TestCase.assertEquals("?100", currency.format(100.0F, ProjectFactory.ukProject()));
        TestCase.assertEquals("$100", currency.formatWithUserPreference(100.0F, ProjectFactory.project(), RoundingMode.DOWN));
        TestCase.assertEquals("CA$ 100", currency.formatWithUserPreference(100.0F, ProjectFactory.caProject(), RoundingMode.DOWN));
        TestCase.assertEquals("?100", currency.formatWithUserPreference(100.0F, ProjectFactory.ukProject(), RoundingMode.DOWN));
    }

    public void testFormatCurrency_withUserInCA() {
        final KSCurrency currency = KSCurrencyTest.createKSCurrency("CA");
        TestCase.assertEquals("$100 USD", currency.format(100.0F, ProjectFactory.project()));
        TestCase.assertEquals("$100 CAD", currency.format(100.0F, ProjectFactory.caProject()));
        TestCase.assertEquals("?100", currency.format(100.0F, ProjectFactory.ukProject()));
        TestCase.assertEquals("US$ 100", currency.formatWithUserPreference(100.0F, ProjectFactory.project(), RoundingMode.DOWN));
        TestCase.assertEquals("?100", currency.formatWithUserPreference(100.0F, ProjectFactory.ukProject(), RoundingMode.DOWN));
        TestCase.assertEquals("CA$ 100", currency.formatWithUserPreference(100.0F, ProjectFactory.caProject(), RoundingMode.DOWN));
    }

    public void testFormatCurrency_withUserInUK() {
        final KSCurrency currency = KSCurrencyTest.createKSCurrency("UK");
        TestCase.assertEquals("$100 USD", currency.format(100.0F, ProjectFactory.project()));
        TestCase.assertEquals("$100 CAD", currency.format(100.0F, ProjectFactory.caProject()));
        TestCase.assertEquals("?100", currency.format(100.0F, ProjectFactory.ukProject()));
        TestCase.assertEquals("CA$ 100", currency.formatWithUserPreference(100.0F, ProjectFactory.caProject(), RoundingMode.DOWN));
        TestCase.assertEquals("?100", currency.formatWithUserPreference(100.0F, ProjectFactory.ukProject(), RoundingMode.DOWN));
    }

    public void testFormatCurrency_withUserInUnlaunchedCountry() {
        final KSCurrency currency = KSCurrencyTest.createKSCurrency("XX");
        TestCase.assertEquals("$100 USD", currency.format(100.0F, ProjectFactory.project()));
        TestCase.assertEquals("$100 CAD", currency.format(100.0F, ProjectFactory.caProject()));
        TestCase.assertEquals("?100", currency.format(100.0F, ProjectFactory.ukProject()));
        TestCase.assertEquals("US$ 100", currency.formatWithUserPreference(100.0F, ProjectFactory.project(), RoundingMode.DOWN));
        TestCase.assertEquals("US$ 100", currency.formatWithUserPreference(100.0F, ProjectFactory.caProject(), RoundingMode.DOWN));
        TestCase.assertEquals("US$ 100", currency.formatWithUserPreference(100.0F, ProjectFactory.ukProject(), RoundingMode.DOWN));
    }

    public void testFormatCurrency_withCurrencyCodeExcluded() {
        final KSCurrency currency = KSCurrencyTest.createKSCurrency("CA");
        TestCase.assertEquals("$100", currency.format(100.0F, ProjectFactory.project(), true));
    }

    public void testFormatCurrency_withUserInUSAndUSDPreferred() {
        final KSCurrency currency = KSCurrencyTest.createKSCurrency("US");
        TestCase.assertEquals("$150", currency.format(100.0F, ProjectFactory.ukProject(), false, true, RoundingMode.DOWN));
    }

    public void testFormatCurrency_withUserInUKAndUSDPreferred() {
        final KSCurrency currency = KSCurrencyTest.createKSCurrency("UK");
        TestCase.assertEquals("?100", currency.format(100.0F, ProjectFactory.ukProject(), false, true, RoundingMode.DOWN));
    }

    public void testFormatCurrency_roundsDown() {
        final KSCurrency currency = KSCurrencyTest.createKSCurrency("US");
        final Project project = ProjectFactory.project();
        TestCase.assertEquals("$100", currency.format(100.4F, project));
        TestCase.assertEquals("$100", currency.format(100.5F, project));
        TestCase.assertEquals("$101", currency.format(101.5F, project));
        TestCase.assertEquals("$100", currency.format(100.9F, project));
    }
}

