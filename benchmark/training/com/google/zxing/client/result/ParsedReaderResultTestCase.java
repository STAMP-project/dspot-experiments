/**
 * Copyright 2007 ZXing authors
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
package com.google.zxing.client.result;


import BarcodeFormat.EAN_13;
import BarcodeFormat.EAN_8;
import BarcodeFormat.UPC_A;
import BarcodeFormat.UPC_E;
import ParsedResultType.ADDRESSBOOK;
import ParsedResultType.CALENDAR;
import ParsedResultType.EMAIL_ADDRESS;
import ParsedResultType.GEO;
import ParsedResultType.ISBN;
import ParsedResultType.PRODUCT;
import ParsedResultType.SMS;
import ParsedResultType.TEL;
import ParsedResultType.TEXT;
import ParsedResultType.URI;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link ParsedResult}.
 *
 * @author Sean Owen
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ParsedReaderResultTestCase extends Assert {
    @Test
    public void testTextType() {
        ParsedReaderResultTestCase.doTestResult("", "", TEXT);
        ParsedReaderResultTestCase.doTestResult("foo", "foo", TEXT);
        ParsedReaderResultTestCase.doTestResult("Hi.", "Hi.", TEXT);
        ParsedReaderResultTestCase.doTestResult("This is a test", "This is a test", TEXT);
        ParsedReaderResultTestCase.doTestResult("This is a test\nwith newlines", "This is a test\nwith newlines", TEXT);
        ParsedReaderResultTestCase.doTestResult("This: a test with lots of @ nearly-random punctuation! No? OK then.", "This: a test with lots of @ nearly-random punctuation! No? OK then.", TEXT);
    }

    @Test
    public void testBookmarkType() {
        ParsedReaderResultTestCase.doTestResult("MEBKM:URL:google.com;;", "http://google.com", URI);
        ParsedReaderResultTestCase.doTestResult("MEBKM:URL:google.com;TITLE:Google;;", "Google\nhttp://google.com", URI);
        ParsedReaderResultTestCase.doTestResult("MEBKM:TITLE:Google;URL:google.com;;", "Google\nhttp://google.com", URI);
        ParsedReaderResultTestCase.doTestResult("MEBKM:URL:http://google.com;;", "http://google.com", URI);
        ParsedReaderResultTestCase.doTestResult("MEBKM:URL:HTTPS://google.com;;", "HTTPS://google.com", URI);
    }

    @Test
    public void testURLTOType() {
        ParsedReaderResultTestCase.doTestResult("urlto:foo:bar.com", "foo\nhttp://bar.com", URI);
        ParsedReaderResultTestCase.doTestResult("URLTO:foo:bar.com", "foo\nhttp://bar.com", URI);
        ParsedReaderResultTestCase.doTestResult("URLTO::bar.com", "http://bar.com", URI);
        ParsedReaderResultTestCase.doTestResult("URLTO::http://bar.com", "http://bar.com", URI);
    }

    @Test
    public void testEmailType() {
        ParsedReaderResultTestCase.doTestResult("MATMSG:TO:srowen@example.org;;", "srowen@example.org", EMAIL_ADDRESS);
        ParsedReaderResultTestCase.doTestResult("MATMSG:TO:srowen@example.org;SUB:Stuff;;", "srowen@example.org\nStuff", EMAIL_ADDRESS);
        ParsedReaderResultTestCase.doTestResult("MATMSG:TO:srowen@example.org;SUB:Stuff;BODY:This is some text;;", "srowen@example.org\nStuff\nThis is some text", EMAIL_ADDRESS);
        ParsedReaderResultTestCase.doTestResult("MATMSG:SUB:Stuff;BODY:This is some text;TO:srowen@example.org;;", "srowen@example.org\nStuff\nThis is some text", EMAIL_ADDRESS);
        ParsedReaderResultTestCase.doTestResult("TO:srowen@example.org;SUB:Stuff;BODY:This is some text;;", "TO:srowen@example.org;SUB:Stuff;BODY:This is some text;;", TEXT);
    }

    @Test
    public void testEmailAddressType() {
        ParsedReaderResultTestCase.doTestResult("srowen@example.org", "srowen@example.org", EMAIL_ADDRESS);
        ParsedReaderResultTestCase.doTestResult("mailto:srowen@example.org", "srowen@example.org", EMAIL_ADDRESS);
        ParsedReaderResultTestCase.doTestResult("MAILTO:srowen@example.org", "srowen@example.org", EMAIL_ADDRESS);
        ParsedReaderResultTestCase.doTestResult("srowen@example", "srowen@example", EMAIL_ADDRESS);
        ParsedReaderResultTestCase.doTestResult("srowen", "srowen", TEXT);
        ParsedReaderResultTestCase.doTestResult("Let's meet @ 2", "Let's meet @ 2", TEXT);
    }

    @Test
    public void testAddressBookType() {
        ParsedReaderResultTestCase.doTestResult("MECARD:N:Sean Owen;;", "Sean Owen", ADDRESSBOOK);
        ParsedReaderResultTestCase.doTestResult("MECARD:TEL:+12125551212;N:Sean Owen;;", "Sean Owen\n+12125551212", ADDRESSBOOK);
        ParsedReaderResultTestCase.doTestResult("MECARD:TEL:+12125551212;N:Sean Owen;URL:google.com;;", "Sean Owen\n+12125551212\ngoogle.com", ADDRESSBOOK);
        ParsedReaderResultTestCase.doTestResult("MECARD:TEL:+12125551212;N:Sean Owen;URL:google.com;EMAIL:srowen@example.org;", "Sean Owen\n+12125551212\nsrowen@example.org\ngoogle.com", ADDRESSBOOK);
        ParsedReaderResultTestCase.doTestResult("MECARD:ADR:76 9th Ave;N:Sean Owen;URL:google.com;EMAIL:srowen@example.org;", "Sean Owen\n76 9th Ave\nsrowen@example.org\ngoogle.com", ADDRESSBOOK);
        ParsedReaderResultTestCase.doTestResult("MECARD:BDAY:19760520;N:Sean Owen;URL:google.com;EMAIL:srowen@example.org;", "Sean Owen\nsrowen@example.org\ngoogle.com\n19760520", ADDRESSBOOK);
        ParsedReaderResultTestCase.doTestResult("MECARD:ORG:Google;N:Sean Owen;URL:google.com;EMAIL:srowen@example.org;", "Sean Owen\nGoogle\nsrowen@example.org\ngoogle.com", ADDRESSBOOK);
        ParsedReaderResultTestCase.doTestResult("MECARD:NOTE:ZXing Team;N:Sean Owen;URL:google.com;EMAIL:srowen@example.org;", "Sean Owen\nsrowen@example.org\ngoogle.com\nZXing Team", ADDRESSBOOK);
        ParsedReaderResultTestCase.doTestResult("N:Sean Owen;TEL:+12125551212;;", "N:Sean Owen;TEL:+12125551212;;", TEXT);
    }

    @Test
    public void testAddressBookAUType() {
        ParsedReaderResultTestCase.doTestResult("MEMORY:\r\n", "", ADDRESSBOOK);
        ParsedReaderResultTestCase.doTestResult("MEMORY:foo\r\nNAME1:Sean\r\n", "Sean\nfoo", ADDRESSBOOK);
        ParsedReaderResultTestCase.doTestResult("TEL1:+12125551212\r\nMEMORY:\r\n", "+12125551212", ADDRESSBOOK);
    }

    @Test
    public void testBizcard() {
        ParsedReaderResultTestCase.doTestResult("BIZCARD:N:Sean;X:Owen;C:Google;A:123 Main St;M:+12225551212;E:srowen@example.org;", "Sean Owen\nGoogle\n123 Main St\n+12225551212\nsrowen@example.org", ADDRESSBOOK);
    }

    @Test
    public void testUPCA() {
        ParsedReaderResultTestCase.doTestResult("123456789012", "123456789012", PRODUCT, UPC_A);
        ParsedReaderResultTestCase.doTestResult("1234567890123", "1234567890123", PRODUCT, UPC_A);
        ParsedReaderResultTestCase.doTestResult("12345678901", "12345678901", TEXT);
    }

    @Test
    public void testUPCE() {
        ParsedReaderResultTestCase.doTestResult("01234565", "01234565", PRODUCT, UPC_E);
    }

    @Test
    public void testEAN() {
        ParsedReaderResultTestCase.doTestResult("00393157", "00393157", PRODUCT, EAN_8);
        ParsedReaderResultTestCase.doTestResult("00393158", "00393158", TEXT);
        ParsedReaderResultTestCase.doTestResult("5051140178499", "5051140178499", PRODUCT, EAN_13);
        ParsedReaderResultTestCase.doTestResult("5051140178490", "5051140178490", TEXT);
    }

    @Test
    public void testISBN() {
        ParsedReaderResultTestCase.doTestResult("9784567890123", "9784567890123", ISBN, EAN_13);
        ParsedReaderResultTestCase.doTestResult("9794567890123", "9794567890123", ISBN, EAN_13);
        ParsedReaderResultTestCase.doTestResult("97845678901", "97845678901", TEXT);
        ParsedReaderResultTestCase.doTestResult("97945678901", "97945678901", TEXT);
    }

    @Test
    public void testURI() {
        ParsedReaderResultTestCase.doTestResult("http://google.com", "http://google.com", URI);
        ParsedReaderResultTestCase.doTestResult("google.com", "http://google.com", URI);
        ParsedReaderResultTestCase.doTestResult("https://google.com", "https://google.com", URI);
        ParsedReaderResultTestCase.doTestResult("HTTP://google.com", "HTTP://google.com", URI);
        ParsedReaderResultTestCase.doTestResult("http://google.com/foobar", "http://google.com/foobar", URI);
        ParsedReaderResultTestCase.doTestResult("https://google.com:443/foobar", "https://google.com:443/foobar", URI);
        ParsedReaderResultTestCase.doTestResult("google.com:443", "http://google.com:443", URI);
        ParsedReaderResultTestCase.doTestResult("google.com:443/", "http://google.com:443/", URI);
        ParsedReaderResultTestCase.doTestResult("google.com:443/foobar", "http://google.com:443/foobar", URI);
        ParsedReaderResultTestCase.doTestResult("http://google.com:443/foobar", "http://google.com:443/foobar", URI);
        ParsedReaderResultTestCase.doTestResult("https://google.com:443/foobar", "https://google.com:443/foobar", URI);
        ParsedReaderResultTestCase.doTestResult("ftp://google.com/fake", "ftp://google.com/fake", URI);
        ParsedReaderResultTestCase.doTestResult("gopher://google.com/obsolete", "gopher://google.com/obsolete", URI);
    }

    @Test
    public void testGeo() {
        ParsedReaderResultTestCase.doTestResult("geo:1,2", "1.0, 2.0", GEO);
        ParsedReaderResultTestCase.doTestResult("GEO:1,2", "1.0, 2.0", GEO);
        ParsedReaderResultTestCase.doTestResult("geo:1,2,3", "1.0, 2.0, 3.0m", GEO);
        ParsedReaderResultTestCase.doTestResult("geo:80.33,-32.3344,3.35", "80.33, -32.3344, 3.35m", GEO);
        ParsedReaderResultTestCase.doTestResult("geo", "geo", TEXT);
        ParsedReaderResultTestCase.doTestResult("geography", "geography", TEXT);
    }

    @Test
    public void testTel() {
        ParsedReaderResultTestCase.doTestResult("tel:+15551212", "+15551212", TEL);
        ParsedReaderResultTestCase.doTestResult("TEL:+15551212", "+15551212", TEL);
        ParsedReaderResultTestCase.doTestResult("tel:212 555 1212", "212 555 1212", TEL);
        ParsedReaderResultTestCase.doTestResult("tel:2125551212", "2125551212", TEL);
        ParsedReaderResultTestCase.doTestResult("tel:212-555-1212", "212-555-1212", TEL);
        ParsedReaderResultTestCase.doTestResult("tel", "tel", TEXT);
        ParsedReaderResultTestCase.doTestResult("telephone", "telephone", TEXT);
    }

    @Test
    public void testVCard() {
        ParsedReaderResultTestCase.doTestResult("BEGIN:VCARD\r\nEND:VCARD", "", ADDRESSBOOK);
        ParsedReaderResultTestCase.doTestResult("BEGIN:VCARD\r\nN:Owen;Sean\r\nEND:VCARD", "Sean Owen", ADDRESSBOOK);
        ParsedReaderResultTestCase.doTestResult("BEGIN:VCARD\r\nVERSION:2.1\r\nN:Owen;Sean\r\nEND:VCARD", "Sean Owen", ADDRESSBOOK);
        ParsedReaderResultTestCase.doTestResult("BEGIN:VCARD\r\nADR;HOME:123 Main St\r\nVERSION:2.1\r\nN:Owen;Sean\r\nEND:VCARD", "Sean Owen\n123 Main St", ADDRESSBOOK);
        ParsedReaderResultTestCase.doTestResult("BEGIN:VCARD", "", ADDRESSBOOK);
    }

    @Test
    public void testVEvent() {
        // UTC times
        ParsedReaderResultTestCase.doTestResult(("BEGIN:VCALENDAR\r\nBEGIN:VEVENT\r\nSUMMARY:foo\r\nDTSTART:20080504T123456Z\r\n" + "DTEND:20080505T234555Z\r\nEND:VEVENT\r\nEND:VCALENDAR"), ((("foo\n" + (ParsedReaderResultTestCase.formatTime(2008, 5, 4, 12, 34, 56))) + "\n") + (ParsedReaderResultTestCase.formatTime(2008, 5, 5, 23, 45, 55))), CALENDAR);
        ParsedReaderResultTestCase.doTestResult(("BEGIN:VEVENT\r\nSUMMARY:foo\r\nDTSTART:20080504T123456Z\r\n" + "DTEND:20080505T234555Z\r\nEND:VEVENT"), ((("foo\n" + (ParsedReaderResultTestCase.formatTime(2008, 5, 4, 12, 34, 56))) + "\n") + (ParsedReaderResultTestCase.formatTime(2008, 5, 5, 23, 45, 55))), CALENDAR);
        // Local times
        ParsedReaderResultTestCase.doTestResult(("BEGIN:VEVENT\r\nSUMMARY:foo\r\nDTSTART:20080504T123456\r\n" + "DTEND:20080505T234555\r\nEND:VEVENT"), ((("foo\n" + (ParsedReaderResultTestCase.formatTime(2008, 5, 4, 12, 34, 56))) + "\n") + (ParsedReaderResultTestCase.formatTime(2008, 5, 5, 23, 45, 55))), CALENDAR);
        // Date only (all day event)
        ParsedReaderResultTestCase.doTestResult(("BEGIN:VEVENT\r\nSUMMARY:foo\r\nDTSTART:20080504\r\n" + "DTEND:20080505\r\nEND:VEVENT"), ((("foo\n" + (ParsedReaderResultTestCase.formatDate(2008, 5, 4))) + "\n") + (ParsedReaderResultTestCase.formatDate(2008, 5, 5))), CALENDAR);
        // Start time only
        ParsedReaderResultTestCase.doTestResult("BEGIN:VEVENT\r\nSUMMARY:foo\r\nDTSTART:20080504T123456Z\r\nEND:VEVENT", ("foo\n" + (ParsedReaderResultTestCase.formatTime(2008, 5, 4, 12, 34, 56))), CALENDAR);
        ParsedReaderResultTestCase.doTestResult("BEGIN:VEVENT\r\nSUMMARY:foo\r\nDTSTART:20080504T123456\r\nEND:VEVENT", ("foo\n" + (ParsedReaderResultTestCase.formatTime(2008, 5, 4, 12, 34, 56))), CALENDAR);
        ParsedReaderResultTestCase.doTestResult("BEGIN:VEVENT\r\nSUMMARY:foo\r\nDTSTART:20080504\r\nEND:VEVENT", ("foo\n" + (ParsedReaderResultTestCase.formatDate(2008, 5, 4))), CALENDAR);
        ParsedReaderResultTestCase.doTestResult("BEGIN:VEVENT\r\nDTEND:20080505T\r\nEND:VEVENT", "BEGIN:VEVENT\r\nDTEND:20080505T\r\nEND:VEVENT", TEXT);
        // Yeah, it's OK that this is thought of as maybe a URI as long as it's not CALENDAR
        // Make sure illegal entries without newlines don't crash
        ParsedReaderResultTestCase.doTestResult("BEGIN:VEVENTSUMMARY:EventDTSTART:20081030T122030ZDTEND:20081030T132030ZEND:VEVENT", "BEGIN:VEVENTSUMMARY:EventDTSTART:20081030T122030ZDTEND:20081030T132030ZEND:VEVENT", URI);
    }

    @Test
    public void testSMS() {
        ParsedReaderResultTestCase.doTestResult("sms:+15551212", "+15551212", SMS);
        ParsedReaderResultTestCase.doTestResult("SMS:+15551212", "+15551212", SMS);
        ParsedReaderResultTestCase.doTestResult("sms:+15551212;via=999333", "+15551212", SMS);
        ParsedReaderResultTestCase.doTestResult("sms:+15551212?subject=foo&body=bar", "+15551212\nfoo\nbar", SMS);
        ParsedReaderResultTestCase.doTestResult("sms:+15551212,+12124440101", "+15551212\n+12124440101", SMS);
    }

    @Test
    public void testSMSTO() {
        ParsedReaderResultTestCase.doTestResult("SMSTO:+15551212", "+15551212", SMS);
        ParsedReaderResultTestCase.doTestResult("smsto:+15551212", "+15551212", SMS);
        ParsedReaderResultTestCase.doTestResult("smsto:+15551212:subject", "+15551212\nsubject", SMS);
        ParsedReaderResultTestCase.doTestResult("smsto:+15551212:My message", "+15551212\nMy message", SMS);
        // Need to handle question mark in the subject
        ParsedReaderResultTestCase.doTestResult("smsto:+15551212:What's up?", "+15551212\nWhat\'s up?", SMS);
        // Need to handle colon in the subject
        ParsedReaderResultTestCase.doTestResult("smsto:+15551212:Directions: Do this", "+15551212\nDirections: Do this", SMS);
        ParsedReaderResultTestCase.doTestResult("smsto:212-555-1212:Here's a longer message. Should be fine.", "212-555-1212\nHere\'s a longer message. Should be fine.", SMS);
    }

    @Test
    public void testMMS() {
        ParsedReaderResultTestCase.doTestResult("mms:+15551212", "+15551212", SMS);
        ParsedReaderResultTestCase.doTestResult("MMS:+15551212", "+15551212", SMS);
        ParsedReaderResultTestCase.doTestResult("mms:+15551212;via=999333", "+15551212", SMS);
        ParsedReaderResultTestCase.doTestResult("mms:+15551212?subject=foo&body=bar", "+15551212\nfoo\nbar", SMS);
        ParsedReaderResultTestCase.doTestResult("mms:+15551212,+12124440101", "+15551212\n+12124440101", SMS);
    }

    @Test
    public void testMMSTO() {
        ParsedReaderResultTestCase.doTestResult("MMSTO:+15551212", "+15551212", SMS);
        ParsedReaderResultTestCase.doTestResult("mmsto:+15551212", "+15551212", SMS);
        ParsedReaderResultTestCase.doTestResult("mmsto:+15551212:subject", "+15551212\nsubject", SMS);
        ParsedReaderResultTestCase.doTestResult("mmsto:+15551212:My message", "+15551212\nMy message", SMS);
        ParsedReaderResultTestCase.doTestResult("mmsto:+15551212:What's up?", "+15551212\nWhat\'s up?", SMS);
        ParsedReaderResultTestCase.doTestResult("mmsto:+15551212:Directions: Do this", "+15551212\nDirections: Do this", SMS);
        ParsedReaderResultTestCase.doTestResult("mmsto:212-555-1212:Here's a longer message. Should be fine.", "212-555-1212\nHere\'s a longer message. Should be fine.", SMS);
    }
}

