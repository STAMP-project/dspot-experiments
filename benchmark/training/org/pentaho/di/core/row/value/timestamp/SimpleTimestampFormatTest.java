/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.row.value.timestamp;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;

import static java.util.Locale.Category.FORMAT;


/**
 * User: Dzmitry Stsiapanau Date: 3/17/14 Time: 4:46 PM
 */
public class SimpleTimestampFormatTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    private static Locale formatLocale;

    private Set<Locale> locales = new HashSet<Locale>(Arrays.asList(Locale.US, Locale.GERMANY, Locale.JAPANESE, Locale.CHINESE));

    private ResourceBundle tdb;

    private static String stringNinePrecision = "2014-03-15 15:30:45.123456789";

    private static String stringFourPrecision = "2014-03-15 15:30:45.1234";

    private static String stringThreePrecision = "2014-03-15 15:30:45.123";

    private static String stringWithoutPrecision = "2014-03-15 15:30:45";

    private static String stringWithoutPrecisionWithDot = "2014-11-15 15:30:45.000";

    private Timestamp timestampNinePrecision = Timestamp.valueOf(SimpleTimestampFormatTest.stringNinePrecision);

    private Timestamp timestampFourPrecision = Timestamp.valueOf(SimpleTimestampFormatTest.stringFourPrecision);

    private Timestamp timestampThreePrecision = Timestamp.valueOf(SimpleTimestampFormatTest.stringThreePrecision);

    private Timestamp timestampWithoutPrecision = Timestamp.valueOf(SimpleTimestampFormatTest.stringWithoutPrecision);

    private Timestamp timestampWithoutPrecisionWithDot = Timestamp.valueOf(SimpleTimestampFormatTest.stringWithoutPrecisionWithDot);

    private Date dateThreePrecision = new Date(timestampThreePrecision.getTime());

    private Date dateWithoutPrecision = new Date(timestampWithoutPrecision.getTime());

    @Test
    public void testFormat() throws Exception {
        for (Locale locale : locales) {
            Locale.setDefault(FORMAT, locale);
            tdb = ResourceBundle.getBundle("org/pentaho/di/core/row/value/timestamp/messages/testdates", locale);
            checkFormat("KETTLE.LONG");
            checkFormat("LOCALE.DATE", new SimpleTimestampFormat(new SimpleDateFormat().toPattern()));
            // checkFormat( "LOCALE.TIMESTAMP", new SimpleTimestampFormat( new SimpleDateFormat().toPattern() ) );
            checkFormat("KETTLE");
            checkFormat("DB.DEFAULT");
            checkFormat("LOCALE.DEFAULT");
        }
    }

    @Test
    public void testParse() throws Exception {
        for (Locale locale : locales) {
            Locale.setDefault(FORMAT, locale);
            tdb = ResourceBundle.getBundle("org/pentaho/di/core/row/value/timestamp/messages/testdates", locale);
            checkParseKettle();
            checkParseKettleLong();
            checkParseDbDefault();
            checkParseLocaleDefault();
            // Uncomment in case of locale timestamp format is defined for the most locales as in SimpleDateFormat()
            // checkParseLocalTimestamp();
        }
    }

    @Test
    public void testToPattern() throws Exception {
        for (Locale locale : locales) {
            Locale.setDefault(FORMAT, locale);
            tdb = ResourceBundle.getBundle("org/pentaho/di/core/row/value/timestamp/messages/testdates", locale);
            String patternExample = tdb.getString("PATTERN.KETTLE");
            SimpleTimestampFormat stf = new SimpleTimestampFormat(new SimpleDateFormat().toPattern());
            Assert.assertEquals(locale.toLanguageTag(), tdb.getString("PATTERN.LOCALE.DATE"), stf.toPattern());
            stf = new SimpleTimestampFormat(patternExample, Locale.GERMANY);
            Assert.assertEquals(locale.toLanguageTag(), patternExample, stf.toPattern());
            stf = new SimpleTimestampFormat(patternExample, Locale.US);
            Assert.assertEquals(locale.toLanguageTag(), patternExample, stf.toPattern());
        }
    }

    @Test
    public void testToLocalizedPattern() throws Exception {
        for (Locale locale : locales) {
            Locale.setDefault(FORMAT, locale);
            tdb = ResourceBundle.getBundle("org/pentaho/di/core/row/value/timestamp/messages/testdates", locale);
            SimpleTimestampFormat stf = new SimpleTimestampFormat(new SimpleDateFormat().toPattern());
            Assert.assertEquals(locale.toLanguageTag(), tdb.getString("PATTERN.LOCALE.COMPILED"), stf.toLocalizedPattern());
            String patternExample = tdb.getString("PATTERN.KETTLE");
            stf = new SimpleTimestampFormat(patternExample);
            Assert.assertEquals(locale.toLanguageTag(), tdb.getString("PATTERN.LOCALE.COMPILED_DATE"), stf.toLocalizedPattern());
        }
    }

    @Test
    public void testApplyPattern() throws Exception {
        for (Locale locale : locales) {
            Locale.setDefault(FORMAT, locale);
            tdb = ResourceBundle.getBundle("org/pentaho/di/core/row/value/timestamp/messages/testdates", locale);
            String patternExample = tdb.getString("PATTERN.KETTLE");
            SimpleTimestampFormat stf = new SimpleTimestampFormat(new SimpleDateFormat().toPattern());
            Assert.assertEquals(locale.toLanguageTag(), tdb.getString("PATTERN.LOCALE.DATE"), stf.toPattern());
            stf.applyPattern(patternExample);
            checkFormat("KETTLE", stf);
        }
    }

    @Test
    public void testApplyLocalizedPattern() throws Exception {
        Locale.setDefault(FORMAT, Locale.US);
        SimpleTimestampFormat stf = new SimpleTimestampFormat(new SimpleDateFormat().toPattern());
        for (Locale locale : locales) {
            Locale.setDefault(FORMAT, locale);
            tdb = ResourceBundle.getBundle("org/pentaho/di/core/row/value/timestamp/messages/testdates", locale);
            stf.applyLocalizedPattern(tdb.getString("PATTERN.LOCALE.DEFAULT"));
            Assert.assertEquals(locale.toLanguageTag(), tdb.getString("PATTERN.LOCALE.DEFAULT"), stf.toLocalizedPattern());
            checkFormat("LOCALE.DEFAULT", stf);
        }
    }
}

