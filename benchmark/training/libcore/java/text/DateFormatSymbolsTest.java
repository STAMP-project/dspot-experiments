/**
 * Copyright (C) 2010 The Android Open Source Project
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
package libcore.java.text;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import junit.framework.TestCase;


public class DateFormatSymbolsTest extends TestCase {
    /**
     * http://b/3056586
     */
    public void test_getInstance_unknown_locale() throws Exception {
        // TODO: we fail this test. on Android, the root locale uses GMT offsets as names.
        // see the invalid locale test below. on the RI, the root locale uses English names.
        assertLocaleIsEquivalentToRoot(new Locale("xx", "XX"));
    }

    public void test_getInstance_invalid_locale() throws Exception {
        assertLocaleIsEquivalentToRoot(new Locale("not exist language", "not exist country"));
    }

    public void testSerialization() throws Exception {
        // The Polish language needs stand-alone month and weekday names.
        Locale pl = new Locale("pl");
        DateFormatSymbols originalDfs = new DateFormatSymbols(pl);
        // Serialize...
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(originalDfs);
        byte[] bytes = out.toByteArray();
        // Deserialize...
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
        DateFormatSymbols deserializedDfs = ((DateFormatSymbols) (in.readObject()));
        TestCase.assertEquals((-1), in.read());
        // The two objects should claim to be equal, even though they aren't really.
        TestCase.assertEquals(originalDfs, deserializedDfs);
        // The original differentiates between regular month names and stand-alone month names...
        TestCase.assertEquals("stycznia", formatDate(pl, "MMMM", originalDfs));
        TestCase.assertEquals("stycze\u0144", formatDate(pl, "LLLL", originalDfs));
        // But the deserialized object is screwed because the RI's serialized form doesn't
        // contain the locale or the necessary strings. Don't serialize DateFormatSymbols, folks!
        TestCase.assertEquals("stycznia", formatDate(pl, "MMMM", deserializedDfs));
        TestCase.assertEquals("January", formatDate(pl, "LLLL", deserializedDfs));
    }

    public void test_getZoneStrings_cloning() throws Exception {
        // Check that corrupting our array doesn't affect other callers.
        // Kill a row.
        {
            String[][] originalZoneStrings = DateFormatSymbols.getInstance(Locale.US).getZoneStrings();
            TestCase.assertNotNull(originalZoneStrings[0]);
            originalZoneStrings[0] = null;
            String[][] currentZoneStrings = DateFormatSymbols.getInstance(Locale.US).getZoneStrings();
            TestCase.assertNotNull(currentZoneStrings[0]);
        }
        // Kill an element.
        {
            String[][] originalZoneStrings = DateFormatSymbols.getInstance(Locale.US).getZoneStrings();
            TestCase.assertNotNull(originalZoneStrings[0][0]);
            originalZoneStrings[0][0] = null;
            String[][] currentZoneStrings = DateFormatSymbols.getInstance(Locale.US).getZoneStrings();
            TestCase.assertNotNull(currentZoneStrings[0][0]);
        }
    }

    public void test_getZoneStrings_UTC() throws Exception {
        HashMap<String, String[]> zoneStrings = new HashMap<String, String[]>();
        for (String[] row : DateFormatSymbols.getInstance(Locale.US).getZoneStrings()) {
            zoneStrings.put(row[0], row);
        }
        DateFormatSymbolsTest.assertUtc(zoneStrings.get("Etc/UCT"));
        DateFormatSymbolsTest.assertUtc(zoneStrings.get("Etc/UTC"));
        DateFormatSymbolsTest.assertUtc(zoneStrings.get("Etc/Universal"));
        DateFormatSymbolsTest.assertUtc(zoneStrings.get("Etc/Zulu"));
        DateFormatSymbolsTest.assertUtc(zoneStrings.get("UCT"));
        DateFormatSymbolsTest.assertUtc(zoneStrings.get("UTC"));
        DateFormatSymbolsTest.assertUtc(zoneStrings.get("Universal"));
        DateFormatSymbolsTest.assertUtc(zoneStrings.get("Zulu"));
    }

    // http://b/8128460
    // If icu4c doesn't actually have a name, we arrange to return null from native code rather
    // that use icu4c's probably-out-of-date time zone transition data.
    // getZoneStrings has to paper over this.
    public void test_getZoneStrings_no_nulls() throws Exception {
        String[][] array = DateFormatSymbols.getInstance(Locale.US).getZoneStrings();
        int failCount = 0;
        for (String[] row : array) {
            for (String element : row) {
                if (element == null) {
                    System.err.println(Arrays.toString(row));
                    ++failCount;
                }
            }
        }
        TestCase.assertEquals(0, failCount);
    }

    // http://b/7955614
    public void test_getZoneStrings_Apia() throws Exception {
        String[][] array = DateFormatSymbols.getInstance(Locale.US).getZoneStrings();
        for (int i = 0; i < (array.length); ++i) {
            String[] row = array[i];
            // Pacific/Apia is somewhat arbitrary; we just want a zone we have to generate
            // "GMT" strings for the short names.
            if (row[0].equals("Pacific/Apia")) {
                TestCase.assertEquals("Samoa Standard Time", row[1]);
                TestCase.assertEquals("GMT+13:00", row[2]);
                TestCase.assertEquals("Samoa Daylight Time", row[3]);
                TestCase.assertEquals("GMT+14:00", row[4]);
            }
        }
    }
}

