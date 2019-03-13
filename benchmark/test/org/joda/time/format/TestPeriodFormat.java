/**
 * Copyright 2001-2014 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.time.format;


import java.util.Locale;
import junit.framework.TestCase;
import org.joda.time.Period;


/**
 * This class is a Junit unit test for PeriodFormat.
 *
 * @author Stephen Colebourne
 */
public class TestPeriodFormat extends TestCase {
    private static final Locale EN = new Locale("en");

    private static final Locale FR = new Locale("fr");

    private static final Locale PT = new Locale("pt");

    private static final Locale ES = new Locale("es");

    private static final Locale DE = new Locale("de");

    private static final Locale NL = new Locale("nl");

    private static final Locale DA = new Locale("da");

    private static final Locale JA = new Locale("ja");

    private static final Locale PL = new Locale("pl");

    private static final Locale BG = new Locale("bg");

    private static final Locale CS = new Locale("cs");

    private Locale originalLocale = null;

    public TestPeriodFormat(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testSubclassableConstructor() {
        PeriodFormat f = new PeriodFormat() {};
        TestCase.assertNotNull(f);
    }

    // -----------------------------------------------------------------------
    // getDefault()
    // -----------------------------------------------------------------------
    public void test_getDefault_formatStandard() {
        Period p = new Period(0, 0, 0, 1, 5, 6, 7, 8);
        TestCase.assertEquals("1 day, 5 hours, 6 minutes, 7 seconds and 8 milliseconds", PeriodFormat.getDefault().print(p));
    }

    // -----------------------------------------------------------------------
    public void test_getDefault_FormatOneField() {
        Period p = Period.days(2);
        TestCase.assertEquals("2 days", PeriodFormat.getDefault().print(p));
    }

    // -----------------------------------------------------------------------
    public void test_getDefault_formatTwoFields() {
        Period p = Period.days(2).withHours(5);
        TestCase.assertEquals("2 days and 5 hours", PeriodFormat.getDefault().print(p));
    }

    // -----------------------------------------------------------------------
    public void test_getDefault_parseOneField() {
        Period p = Period.days(2);
        TestCase.assertEquals(p, PeriodFormat.getDefault().parsePeriod("2 days"));
    }

    // -----------------------------------------------------------------------
    public void test_getDefault_parseTwoFields() {
        Period p = Period.days(2).withHours(5);
        TestCase.assertEquals(p, PeriodFormat.getDefault().parsePeriod("2 days and 5 hours"));
    }

    // -----------------------------------------------------------------------
    public void test_getDefault_checkRedundantSeparator() {
        try {
            PeriodFormat.getDefault().parsePeriod("2 days and 5 hours ");
            TestCase.fail("No exception was caught");
        } catch (Exception e) {
            TestCase.assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    // -----------------------------------------------------------------------
    public void test_getDefault_cached() {
        TestCase.assertSame(PeriodFormat.getDefault(), PeriodFormat.getDefault());
    }

    // -----------------------------------------------------------------------
    // wordBased() - default locale (de)
    // -----------------------------------------------------------------------
    public void test_wordBased_default() {
        Period p = new Period(0, 0, 0, 1, 5, 6, 7, 8);
        TestCase.assertEquals("1 Tag, 5 Stunden, 6 Minuten, 7 Sekunden und 8 Millisekunden", PeriodFormat.wordBased().print(p));
    }

    // -----------------------------------------------------------------------
    // wordBased(Locale.FRENCH)
    // -----------------------------------------------------------------------
    public void test_wordBased_fr_formatStandard() {
        Period p = new Period(0, 0, 0, 1, 5, 6, 7, 8);
        TestCase.assertEquals("1 jour, 5 heures, 6 minutes, 7 secondes et 8 millisecondes", PeriodFormat.wordBased(TestPeriodFormat.FR).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_fr_FormatOneField() {
        Period p = Period.days(2);
        TestCase.assertEquals("2 jours", PeriodFormat.wordBased(TestPeriodFormat.FR).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_fr_formatTwoFields() {
        Period p = Period.days(2).withHours(5);
        TestCase.assertEquals("2 jours et 5 heures", PeriodFormat.wordBased(TestPeriodFormat.FR).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_fr_parseOneField() {
        Period p = Period.days(2);
        TestCase.assertEquals(p, PeriodFormat.wordBased(TestPeriodFormat.FR).parsePeriod("2 jours"));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_fr_parseTwoFields() {
        Period p = Period.days(2).withHours(5);
        TestCase.assertEquals(p, PeriodFormat.wordBased(TestPeriodFormat.FR).parsePeriod("2 jours et 5 heures"));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_fr_cached() {
        TestCase.assertSame(PeriodFormat.wordBased(TestPeriodFormat.FR), PeriodFormat.wordBased(TestPeriodFormat.FR));
    }

    // -----------------------------------------------------------------------
    // wordBased(Locale pt)
    // -----------------------------------------------------------------------
    public void test_wordBased_pt_formatStandard() {
        Period p = new Period(0, 0, 0, 1, 5, 6, 7, 8);
        TestCase.assertEquals("1 dia, 5 horas, 6 minutos, 7 segundos e 8 milissegundos", PeriodFormat.wordBased(TestPeriodFormat.PT).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_pt_FormatOneField() {
        Period p = Period.days(2);
        TestCase.assertEquals("2 dias", PeriodFormat.wordBased(TestPeriodFormat.PT).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_pt_formatTwoFields() {
        Period p = Period.days(2).withHours(5);
        TestCase.assertEquals("2 dias e 5 horas", PeriodFormat.wordBased(TestPeriodFormat.PT).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_pt_parseOneField() {
        Period p = Period.days(2);
        TestCase.assertEquals(p, PeriodFormat.wordBased(TestPeriodFormat.PT).parsePeriod("2 dias"));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_pt_parseTwoFields() {
        Period p = Period.days(2).withHours(5);
        TestCase.assertEquals(p, PeriodFormat.wordBased(TestPeriodFormat.PT).parsePeriod("2 dias e 5 horas"));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_pt_cached() {
        TestCase.assertSame(PeriodFormat.wordBased(TestPeriodFormat.PT), PeriodFormat.wordBased(TestPeriodFormat.PT));
    }

    // -----------------------------------------------------------------------
    // wordBased(Locale es)
    // -----------------------------------------------------------------------
    public void test_wordBased_es_formatStandard() {
        Period p = new Period(0, 0, 0, 1, 5, 6, 7, 8);
        TestCase.assertEquals("1 d\u00eda, 5 horas, 6 minutos, 7 segundos y 8 milisegundos", PeriodFormat.wordBased(TestPeriodFormat.ES).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_es_FormatOneField() {
        Period p = Period.days(2);
        TestCase.assertEquals("2 d\u00edas", PeriodFormat.wordBased(TestPeriodFormat.ES).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_es_formatTwoFields() {
        Period p = Period.days(2).withHours(5);
        TestCase.assertEquals("2 d\u00edas y 5 horas", PeriodFormat.wordBased(TestPeriodFormat.ES).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_es_parseOneField() {
        Period p = Period.days(2);
        TestCase.assertEquals(p, PeriodFormat.wordBased(TestPeriodFormat.ES).parsePeriod("2 d\u00edas"));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_es_parseTwoFields() {
        Period p = Period.days(2).withHours(5);
        TestCase.assertEquals(p, PeriodFormat.wordBased(TestPeriodFormat.ES).parsePeriod("2 d\u00edas y 5 horas"));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_es_cached() {
        TestCase.assertSame(PeriodFormat.wordBased(TestPeriodFormat.ES), PeriodFormat.wordBased(TestPeriodFormat.ES));
    }

    // -----------------------------------------------------------------------
    // wordBased(Locale de)
    // -----------------------------------------------------------------------
    public void test_wordBased_de_formatStandard() {
        Period p = new Period(0, 0, 0, 1, 5, 6, 7, 8);
        TestCase.assertEquals("1 Tag, 5 Stunden, 6 Minuten, 7 Sekunden und 8 Millisekunden", PeriodFormat.wordBased(TestPeriodFormat.DE).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_de_FormatOneField() {
        Period p = Period.days(2);
        TestCase.assertEquals("2 Tage", PeriodFormat.wordBased(TestPeriodFormat.DE).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_de_formatTwoFields() {
        Period p = Period.days(2).withHours(5);
        TestCase.assertEquals("2 Tage und 5 Stunden", PeriodFormat.wordBased(TestPeriodFormat.DE).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_de_parseOneField() {
        Period p = Period.days(2);
        TestCase.assertEquals(p, PeriodFormat.wordBased(TestPeriodFormat.DE).parsePeriod("2 Tage"));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_de_parseTwoFields() {
        Period p = Period.days(2).withHours(5);
        TestCase.assertEquals(p, PeriodFormat.wordBased(TestPeriodFormat.DE).parsePeriod("2 Tage und 5 Stunden"));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_de_cached() {
        TestCase.assertSame(PeriodFormat.wordBased(TestPeriodFormat.DE), PeriodFormat.wordBased(TestPeriodFormat.DE));
    }

    // -----------------------------------------------------------------------
    // wordBased(Locale nl)
    // -----------------------------------------------------------------------
    public void test_wordBased_nl_formatStandard() {
        Period p = new Period(0, 0, 0, 1, 5, 6, 7, 8);
        TestCase.assertEquals("1 dag, 5 uur, 6 minuten, 7 seconden en 8 milliseconden", PeriodFormat.wordBased(TestPeriodFormat.NL).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_nl_FormatOneField() {
        Period p = Period.days(2);
        TestCase.assertEquals("2 dagen", PeriodFormat.wordBased(TestPeriodFormat.NL).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_nl_formatTwoFields() {
        Period p = Period.days(2).withHours(5);
        TestCase.assertEquals("2 dagen en 5 uur", PeriodFormat.wordBased(TestPeriodFormat.NL).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_nl_parseOneField() {
        Period p = Period.days(2);
        TestCase.assertEquals(p, PeriodFormat.wordBased(TestPeriodFormat.NL).parsePeriod("2 dagen"));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_nl_parseTwoFields() {
        Period p = Period.days(2).withHours(5);
        TestCase.assertEquals(p, PeriodFormat.wordBased(TestPeriodFormat.NL).parsePeriod("2 dagen en 5 uur"));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_nl_cached() {
        TestCase.assertSame(PeriodFormat.wordBased(TestPeriodFormat.NL), PeriodFormat.wordBased(TestPeriodFormat.NL));
    }

    // -----------------------------------------------------------------------
    // wordBased(Locale da)
    // -----------------------------------------------------------------------
    public void test_wordBased_da_formatMultiple() {
        Period p = new Period(2, 3, 4, 2, 5, 6, 7, 8);
        TestCase.assertEquals("2 \u00e5r, 3 m\u00e5neder, 4 uger, 2 dage, 5 timer, 6 minutter, 7 sekunder og 8 millisekunder", PeriodFormat.wordBased(TestPeriodFormat.DA).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_da_formatSinglular() {
        Period p = new Period(1, 1, 1, 1, 1, 1, 1, 1);
        TestCase.assertEquals("1 \u00e5r, 1 m\u00e5ned, 1 uge, 1 dag, 1 time, 1 minut, 1 sekund og 1 millisekund", PeriodFormat.wordBased(TestPeriodFormat.DA).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_da_cached() {
        TestCase.assertSame(PeriodFormat.wordBased(TestPeriodFormat.DA), PeriodFormat.wordBased(TestPeriodFormat.DA));
    }

    // -----------------------------------------------------------------------
    // wordBased(Locale ja)
    // -----------------------------------------------------------------------
    public void test_wordBased_ja_formatMultiple() {
        Period p = new Period(2, 3, 4, 2, 5, 6, 7, 8);
        TestCase.assertEquals("2\u5e743\u304b\u67084\u9031\u95932\u65e55\u6642\u95936\u52067\u79d28\u30df\u30ea\u79d2", PeriodFormat.wordBased(TestPeriodFormat.JA).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_ja_formatSingular() {
        Period p = new Period(1, 1, 1, 1, 1, 1, 1, 1);
        TestCase.assertEquals("1\u5e741\u304b\u67081\u9031\u95931\u65e51\u6642\u95931\u52061\u79d21\u30df\u30ea\u79d2", PeriodFormat.wordBased(TestPeriodFormat.JA).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_ja_cached() {
        TestCase.assertSame(PeriodFormat.wordBased(TestPeriodFormat.JA), PeriodFormat.wordBased(TestPeriodFormat.JA));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_ja_parseOneField() {
        Period p = Period.days(2);
        TestCase.assertEquals(p, PeriodFormat.wordBased(TestPeriodFormat.JA).parsePeriod("2\u65e5"));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_ja_parseTwoFields() {
        Period p = Period.days(2).withHours(5);
        TestCase.assertEquals(p, PeriodFormat.wordBased(TestPeriodFormat.JA).parsePeriod("2\u65e55\u6642\u9593"));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_ja_checkRedundantSeparator() {
        try {
            // Spaces are not valid separators in Japanese
            PeriodFormat.wordBased(TestPeriodFormat.JA).parsePeriod("2\u65e5 ");
            TestCase.fail("No exception was caught");
        } catch (Exception e) {
            TestCase.assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    // -----------------------------------------------------------------------
    // wordBased(new Locale("pl")
    // -----------------------------------------------------------------------
    public void test_wordBased_pl_formatStandard() {
        Period p = new Period(0, 0, 0, 1, 5, 6, 7, 8);
        TestCase.assertEquals("1 dzie\u0144, 5 godzin, 6 minut, 7 sekund i 8 milisekund", PeriodFormat.wordBased(TestPeriodFormat.PL).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_pl_FormatOneField() {
        Period p = Period.days(2);
        TestCase.assertEquals("2 dni", PeriodFormat.wordBased(TestPeriodFormat.PL).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_pl_formatTwoFields() {
        Period p = Period.days(2).withHours(5);
        TestCase.assertEquals("2 dni i 5 godzin", PeriodFormat.wordBased(TestPeriodFormat.PL).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_pl_parseOneField() {
        Period p = Period.days(2);
        TestCase.assertEquals(p, PeriodFormat.wordBased(TestPeriodFormat.PL).parsePeriod("2 dni"));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_pl_parseTwoFields() {
        Period p = Period.days(2).withHours(5);
        TestCase.assertEquals(p, PeriodFormat.wordBased(TestPeriodFormat.PL).parsePeriod("2 dni i 5 godzin"));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_pl_checkRedundantSeparator() {
        try {
            PeriodFormat.wordBased(TestPeriodFormat.PL).parsePeriod("2 dni and 5 godzin ");
            TestCase.fail("No exception was caught");
        } catch (Exception e) {
            TestCase.assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_pl_cached() {
        TestCase.assertSame(PeriodFormat.wordBased(TestPeriodFormat.PL), PeriodFormat.wordBased(TestPeriodFormat.PL));
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_pl_regEx() {
        PeriodFormatter pf = PeriodFormat.wordBased(TestPeriodFormat.PL);
        TestCase.assertEquals("1 rok", pf.print(Period.years(1)));
        TestCase.assertEquals("2 lata", pf.print(Period.years(2)));
        TestCase.assertEquals("5 lat", pf.print(Period.years(5)));
        TestCase.assertEquals("12 lat", pf.print(Period.years(12)));
        TestCase.assertEquals("15 lat", pf.print(Period.years(15)));
        TestCase.assertEquals("1112 lat", pf.print(Period.years(1112)));
        TestCase.assertEquals("1115 lat", pf.print(Period.years(1115)));
        TestCase.assertEquals("2112 lat", pf.print(Period.years(2112)));
        TestCase.assertEquals("2115 lat", pf.print(Period.years(2115)));
        TestCase.assertEquals("2212 lat", pf.print(Period.years(2212)));
        TestCase.assertEquals("2215 lat", pf.print(Period.years(2215)));
        TestCase.assertEquals("22 lata", pf.print(Period.years(22)));
        TestCase.assertEquals("25 lat", pf.print(Period.years(25)));
        TestCase.assertEquals("1122 lata", pf.print(Period.years(1122)));
        TestCase.assertEquals("1125 lat", pf.print(Period.years(1125)));
        TestCase.assertEquals("2122 lata", pf.print(Period.years(2122)));
        TestCase.assertEquals("2125 lat", pf.print(Period.years(2125)));
        TestCase.assertEquals("2222 lata", pf.print(Period.years(2222)));
        TestCase.assertEquals("2225 lat", pf.print(Period.years(2225)));
        TestCase.assertEquals("1 miesi\u0105c", pf.print(Period.months(1)));
        TestCase.assertEquals("2 miesi\u0105ce", pf.print(Period.months(2)));
        TestCase.assertEquals("5 miesi\u0119cy", pf.print(Period.months(5)));
        TestCase.assertEquals("12 miesi\u0119cy", pf.print(Period.months(12)));
        TestCase.assertEquals("15 miesi\u0119cy", pf.print(Period.months(15)));
        TestCase.assertEquals("1112 miesi\u0119cy", pf.print(Period.months(1112)));
        TestCase.assertEquals("1115 miesi\u0119cy", pf.print(Period.months(1115)));
        TestCase.assertEquals("2112 miesi\u0119cy", pf.print(Period.months(2112)));
        TestCase.assertEquals("2115 miesi\u0119cy", pf.print(Period.months(2115)));
        TestCase.assertEquals("2212 miesi\u0119cy", pf.print(Period.months(2212)));
        TestCase.assertEquals("2215 miesi\u0119cy", pf.print(Period.months(2215)));
        TestCase.assertEquals("22 miesi\u0105ce", pf.print(Period.months(22)));
        TestCase.assertEquals("25 miesi\u0119cy", pf.print(Period.months(25)));
        TestCase.assertEquals("1122 miesi\u0105ce", pf.print(Period.months(1122)));
        TestCase.assertEquals("1125 miesi\u0119cy", pf.print(Period.months(1125)));
        TestCase.assertEquals("2122 miesi\u0105ce", pf.print(Period.months(2122)));
        TestCase.assertEquals("2125 miesi\u0119cy", pf.print(Period.months(2125)));
        TestCase.assertEquals("2222 miesi\u0105ce", pf.print(Period.months(2222)));
        TestCase.assertEquals("2225 miesi\u0119cy", pf.print(Period.months(2225)));
        TestCase.assertEquals("1 tydzie\u0144", pf.print(Period.weeks(1)));
        TestCase.assertEquals("2 tygodnie", pf.print(Period.weeks(2)));
        TestCase.assertEquals("5 tygodni", pf.print(Period.weeks(5)));
        TestCase.assertEquals("12 tygodni", pf.print(Period.weeks(12)));
        TestCase.assertEquals("15 tygodni", pf.print(Period.weeks(15)));
        TestCase.assertEquals("1112 tygodni", pf.print(Period.weeks(1112)));
        TestCase.assertEquals("1115 tygodni", pf.print(Period.weeks(1115)));
        TestCase.assertEquals("2112 tygodni", pf.print(Period.weeks(2112)));
        TestCase.assertEquals("2115 tygodni", pf.print(Period.weeks(2115)));
        TestCase.assertEquals("2212 tygodni", pf.print(Period.weeks(2212)));
        TestCase.assertEquals("2215 tygodni", pf.print(Period.weeks(2215)));
        TestCase.assertEquals("22 tygodnie", pf.print(Period.weeks(22)));
        TestCase.assertEquals("25 tygodni", pf.print(Period.weeks(25)));
        TestCase.assertEquals("1122 tygodnie", pf.print(Period.weeks(1122)));
        TestCase.assertEquals("1125 tygodni", pf.print(Period.weeks(1125)));
        TestCase.assertEquals("2122 tygodnie", pf.print(Period.weeks(2122)));
        TestCase.assertEquals("2125 tygodni", pf.print(Period.weeks(2125)));
        TestCase.assertEquals("2222 tygodnie", pf.print(Period.weeks(2222)));
        TestCase.assertEquals("2225 tygodni", pf.print(Period.weeks(2225)));
        TestCase.assertEquals("1 dzie\u0144", pf.print(Period.days(1)));
        TestCase.assertEquals("2 dni", pf.print(Period.days(2)));
        TestCase.assertEquals("5 dni", pf.print(Period.days(5)));
        TestCase.assertEquals("12 dni", pf.print(Period.days(12)));
        TestCase.assertEquals("15 dni", pf.print(Period.days(15)));
        TestCase.assertEquals("22 dni", pf.print(Period.days(22)));
        TestCase.assertEquals("25 dni", pf.print(Period.days(25)));
        TestCase.assertEquals("1 godzina", pf.print(Period.hours(1)));
        TestCase.assertEquals("2 godziny", pf.print(Period.hours(2)));
        TestCase.assertEquals("5 godzin", pf.print(Period.hours(5)));
        TestCase.assertEquals("12 godzin", pf.print(Period.hours(12)));
        TestCase.assertEquals("15 godzin", pf.print(Period.hours(15)));
        TestCase.assertEquals("1112 godzin", pf.print(Period.hours(1112)));
        TestCase.assertEquals("1115 godzin", pf.print(Period.hours(1115)));
        TestCase.assertEquals("2112 godzin", pf.print(Period.hours(2112)));
        TestCase.assertEquals("2115 godzin", pf.print(Period.hours(2115)));
        TestCase.assertEquals("2212 godzin", pf.print(Period.hours(2212)));
        TestCase.assertEquals("2215 godzin", pf.print(Period.hours(2215)));
        TestCase.assertEquals("22 godziny", pf.print(Period.hours(22)));
        TestCase.assertEquals("25 godzin", pf.print(Period.hours(25)));
        TestCase.assertEquals("1122 godziny", pf.print(Period.hours(1122)));
        TestCase.assertEquals("1125 godzin", pf.print(Period.hours(1125)));
        TestCase.assertEquals("2122 godziny", pf.print(Period.hours(2122)));
        TestCase.assertEquals("2125 godzin", pf.print(Period.hours(2125)));
        TestCase.assertEquals("2222 godziny", pf.print(Period.hours(2222)));
        TestCase.assertEquals("2225 godzin", pf.print(Period.hours(2225)));
        TestCase.assertEquals("1 minuta", pf.print(Period.minutes(1)));
        TestCase.assertEquals("2 minuty", pf.print(Period.minutes(2)));
        TestCase.assertEquals("5 minut", pf.print(Period.minutes(5)));
        TestCase.assertEquals("12 minut", pf.print(Period.minutes(12)));
        TestCase.assertEquals("15 minut", pf.print(Period.minutes(15)));
        TestCase.assertEquals("1112 minut", pf.print(Period.minutes(1112)));
        TestCase.assertEquals("1115 minut", pf.print(Period.minutes(1115)));
        TestCase.assertEquals("2112 minut", pf.print(Period.minutes(2112)));
        TestCase.assertEquals("2115 minut", pf.print(Period.minutes(2115)));
        TestCase.assertEquals("2212 minut", pf.print(Period.minutes(2212)));
        TestCase.assertEquals("2215 minut", pf.print(Period.minutes(2215)));
        TestCase.assertEquals("22 minuty", pf.print(Period.minutes(22)));
        TestCase.assertEquals("25 minut", pf.print(Period.minutes(25)));
        TestCase.assertEquals("1122 minuty", pf.print(Period.minutes(1122)));
        TestCase.assertEquals("1125 minut", pf.print(Period.minutes(1125)));
        TestCase.assertEquals("2122 minuty", pf.print(Period.minutes(2122)));
        TestCase.assertEquals("2125 minut", pf.print(Period.minutes(2125)));
        TestCase.assertEquals("2222 minuty", pf.print(Period.minutes(2222)));
        TestCase.assertEquals("2225 minut", pf.print(Period.minutes(2225)));
        TestCase.assertEquals("1 sekunda", pf.print(Period.seconds(1)));
        TestCase.assertEquals("2 sekundy", pf.print(Period.seconds(2)));
        TestCase.assertEquals("5 sekund", pf.print(Period.seconds(5)));
        TestCase.assertEquals("12 sekund", pf.print(Period.seconds(12)));
        TestCase.assertEquals("15 sekund", pf.print(Period.seconds(15)));
        TestCase.assertEquals("1112 sekund", pf.print(Period.seconds(1112)));
        TestCase.assertEquals("1115 sekund", pf.print(Period.seconds(1115)));
        TestCase.assertEquals("2112 sekund", pf.print(Period.seconds(2112)));
        TestCase.assertEquals("2115 sekund", pf.print(Period.seconds(2115)));
        TestCase.assertEquals("2212 sekund", pf.print(Period.seconds(2212)));
        TestCase.assertEquals("2215 sekund", pf.print(Period.seconds(2215)));
        TestCase.assertEquals("22 sekundy", pf.print(Period.seconds(22)));
        TestCase.assertEquals("25 sekund", pf.print(Period.seconds(25)));
        TestCase.assertEquals("1122 sekundy", pf.print(Period.seconds(1122)));
        TestCase.assertEquals("1125 sekund", pf.print(Period.seconds(1125)));
        TestCase.assertEquals("2122 sekundy", pf.print(Period.seconds(2122)));
        TestCase.assertEquals("2125 sekund", pf.print(Period.seconds(2125)));
        TestCase.assertEquals("2222 sekundy", pf.print(Period.seconds(2222)));
        TestCase.assertEquals("2225 sekund", pf.print(Period.seconds(2225)));
        TestCase.assertEquals("1 milisekunda", pf.print(Period.millis(1)));
        TestCase.assertEquals("2 milisekundy", pf.print(Period.millis(2)));
        TestCase.assertEquals("5 milisekund", pf.print(Period.millis(5)));
        TestCase.assertEquals("12 milisekund", pf.print(Period.millis(12)));
        TestCase.assertEquals("15 milisekund", pf.print(Period.millis(15)));
        TestCase.assertEquals("1112 milisekund", pf.print(Period.millis(1112)));
        TestCase.assertEquals("1115 milisekund", pf.print(Period.millis(1115)));
        TestCase.assertEquals("2112 milisekund", pf.print(Period.millis(2112)));
        TestCase.assertEquals("2115 milisekund", pf.print(Period.millis(2115)));
        TestCase.assertEquals("2212 milisekund", pf.print(Period.millis(2212)));
        TestCase.assertEquals("2215 milisekund", pf.print(Period.millis(2215)));
        TestCase.assertEquals("22 milisekundy", pf.print(Period.millis(22)));
        TestCase.assertEquals("25 milisekund", pf.print(Period.millis(25)));
        TestCase.assertEquals("1122 milisekundy", pf.print(Period.millis(1122)));
        TestCase.assertEquals("1125 milisekund", pf.print(Period.millis(1125)));
        TestCase.assertEquals("2122 milisekundy", pf.print(Period.millis(2122)));
        TestCase.assertEquals("2125 milisekund", pf.print(Period.millis(2125)));
        TestCase.assertEquals("2222 milisekundy", pf.print(Period.millis(2222)));
        TestCase.assertEquals("2225 milisekund", pf.print(Period.millis(2225)));
    }

    // -----------------------------------------------------------------------
    // wordBased(new Locale("bg")
    // -----------------------------------------------------------------------
    public void test_wordBased_bg_formatStandard() {
        Period p = new Period(0, 0, 0, 1, 5, 6, 7, 8);
        TestCase.assertEquals("1 ???, 5 ????, 6 ??????, 7 ??????? ? 8 ???????????", PeriodFormat.wordBased(TestPeriodFormat.BG).print(p));
    }

    // -----------------------------------------------------------------------
    // wordBased(new Locale("cs")
    // -----------------------------------------------------------------------
    public void test_wordBased_cs_formatStandard() {
        Period p = new Period(0, 0, 0, 1, 5, 6, 7, 8);
        TestCase.assertEquals("1 den, 5 hodin, 6 minut, 7 sekund a 8 milisekund", PeriodFormat.wordBased(TestPeriodFormat.CS).print(p));
    }

    // -----------------------------------------------------------------------
    // Cross check languages
    // -----------------------------------------------------------------------
    public void test_wordBased_fr_from_de() {
        Locale.setDefault(TestPeriodFormat.DE);
        Period p = new Period(0, 0, 0, 1, 5, 6, 7, 8);
        TestCase.assertEquals("1 jour, 5 heures, 6 minutes, 7 secondes et 8 millisecondes", PeriodFormat.wordBased(TestPeriodFormat.FR).print(p));
    }

    public void test_wordBased_fr_from_nl() {
        Locale.setDefault(TestPeriodFormat.NL);
        Period p = new Period(0, 0, 0, 1, 5, 6, 7, 8);
        TestCase.assertEquals("1 jour, 5 heures, 6 minutes, 7 secondes et 8 millisecondes", PeriodFormat.wordBased(TestPeriodFormat.FR).print(p));
    }

    public void test_wordBased_en_from_de() {
        Locale.setDefault(TestPeriodFormat.DE);
        Period p = new Period(0, 0, 0, 1, 5, 6, 7, 8);
        TestCase.assertEquals("1 day, 5 hours, 6 minutes, 7 seconds and 8 milliseconds", PeriodFormat.wordBased(TestPeriodFormat.EN).print(p));
    }

    public void test_wordBased_en_from_nl() {
        Locale.setDefault(TestPeriodFormat.NL);
        Period p = new Period(0, 0, 0, 1, 5, 6, 7, 8);
        TestCase.assertEquals("1 day, 5 hours, 6 minutes, 7 seconds and 8 milliseconds", PeriodFormat.wordBased(TestPeriodFormat.EN).print(p));
    }

    public void test_wordBased_en_from_pl() {
        Locale.setDefault(TestPeriodFormat.PL);
        Period p = new Period(0, 0, 0, 1, 5, 6, 7, 8);
        TestCase.assertEquals("1 day, 5 hours, 6 minutes, 7 seconds and 8 milliseconds", PeriodFormat.wordBased(TestPeriodFormat.EN).print(p));
    }

    public void test_wordBased_pl_from_fr() {
        Locale.setDefault(TestPeriodFormat.FR);
        Period p = new Period(0, 0, 0, 1, 5, 6, 7, 8);
        TestCase.assertEquals("1 dzie\u0144, 5 godzin, 6 minut, 7 sekund i 8 milisekund", PeriodFormat.wordBased(TestPeriodFormat.PL).print(p));
    }

    // -----------------------------------------------------------------------
    public void test_getDefault_localeValue() {
        PeriodFormatter pf = PeriodFormat.getDefault();
        TestCase.assertEquals(Locale.ENGLISH, pf.getLocale());
    }

    public void test_wordBased_localeValue() {
        PeriodFormatter pf = PeriodFormat.wordBased();
        TestCase.assertEquals(TestPeriodFormat.DE, pf.getLocale());
    }

    public void test_wordBasedWithLocale_localeValue() {
        PeriodFormatter pf = PeriodFormat.wordBased(TestPeriodFormat.FR);
        TestCase.assertEquals(TestPeriodFormat.FR, pf.getLocale());
    }

    // -----------------------------------------------------------------------
    public void test_wordBased_en_withLocale_pt() {
        Period p = Period.days(2).withHours(5);
        PeriodFormatter format1 = PeriodFormat.wordBased(TestPeriodFormat.EN);
        TestCase.assertEquals("2 days and 5 hours", format1.print(p));
        TestCase.assertEquals(p, format1.parsePeriod("2 days and 5 hours"));
        TestCase.assertEquals(TestPeriodFormat.EN, format1.getLocale());
        PeriodFormatter format2 = format1.withLocale(TestPeriodFormat.PT);
        TestCase.assertEquals("2 dias e 5 horas", format2.print(p));
        TestCase.assertEquals(p, format2.parsePeriod("2 dias e 5 horas"));
        TestCase.assertEquals(TestPeriodFormat.PT, format2.getLocale());
        PeriodFormatter format3 = format1.withLocale(TestPeriodFormat.DE);
        TestCase.assertEquals("2 Tage und 5 Stunden", format3.print(p));
        TestCase.assertEquals(p, format3.parsePeriod("2 Tage und 5 Stunden"));
        TestCase.assertEquals(TestPeriodFormat.DE, format3.getLocale());
        PeriodFormatter format4 = format1.withLocale(null);
        TestCase.assertEquals("2 days and 5 hours", format4.print(p));
        TestCase.assertEquals(p, format4.parsePeriod("2 days and 5 hours"));
        TestCase.assertEquals(null, format4.getLocale());
    }
}

