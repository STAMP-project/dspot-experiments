/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.reading.time.internal.calculator;


import ContentTypes.APPLICATION_PDF;
import ContentTypes.TEXT;
import ContentTypes.TEXT_HTML;
import ContentTypes.TEXT_HTML_UTF8;
import ContentTypes.TEXT_PLAIN;
import ContentTypes.TEXT_PLAIN_UTF8;
import StringPool.BLANK;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.reading.time.calculator.ReadingTimeCalculator;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Alejandro Tard?n
 */
public class ReadingTimeCalculatorImplTest {
    @Test
    public void testAcceptsHTMLAndText() {
        ReadingTimeCalculator readingTimeCalculator = new ReadingTimeCalculatorImpl();
        for (String contentType : ReadingTimeCalculatorImplTest._supportedContentTypes) {
            Optional<Duration> readingTimeOptional = readingTimeCalculator.calculate(StringUtil.randomString(), contentType, Locale.getDefault());
            Assert.assertTrue(readingTimeOptional.isPresent());
        }
    }

    @Test
    public void testAdds3SecondsPerImageInHTML() {
        StringBundler sb = new StringBundler((265 + 3));
        for (int i = 0; i < 265; i++) {
            sb.append("<b>word</b>&nbsp;");
        }
        sb.append("<img src=\"img1\"/>");
        sb.append("<img src=\"img2\"/>");
        sb.append("<img src=\"img3\"/>");
        Duration readingTimeDuration = _calculateReadingTime(sb.toString(), "text/html", Locale.getDefault());
        Assert.assertEquals((60 + (3 * 3)), readingTimeDuration.getSeconds());
    }

    @Test
    public void testCounts0ForEmptyContent() {
        Duration readingTimeDuration = _calculateReadingTime(BLANK, "text/html", Locale.getDefault());
        Assert.assertEquals(0, readingTimeDuration.getSeconds());
    }

    @Test
    public void testCounts0ForNullContent() {
        Duration readingTimeDuration = _calculateReadingTime(null, "text/html", Locale.getDefault());
        Assert.assertEquals(0, readingTimeDuration.getSeconds());
    }

    @Test
    public void testCounts265WordsPerMinuteInHTML() {
        StringBundler sb = new StringBundler(265);
        for (int i = 0; i < 265; i++) {
            sb.append("<span class=\"random\">word</span>&nbsp;");
        }
        Duration readingTimeDuration = _calculateReadingTime(sb.toString(), "text/html", Locale.getDefault());
        Assert.assertEquals(60, readingTimeDuration.getSeconds());
    }

    @Test
    public void testCounts265WordsPerMinuteInText() {
        StringBundler sb = new StringBundler(265);
        for (int i = 0; i < 265; i++) {
            sb.append("word ");
        }
        Duration readingTimeDuration = _calculateReadingTime(sb.toString(), "text/plain", Locale.getDefault());
        Assert.assertEquals(60, readingTimeDuration.getSeconds());
    }

    @Test
    public void testDoesNotAcceptPDFs() {
        ReadingTimeCalculator readingTimeCalculator = new ReadingTimeCalculatorImpl();
        Optional<Duration> readingTimeOptional = readingTimeCalculator.calculate(StringUtil.randomString(), APPLICATION_PDF, Locale.getDefault());
        Assert.assertFalse(readingTimeOptional.isPresent());
    }

    private static final List<String> _supportedContentTypes = Arrays.asList(TEXT_HTML, TEXT_HTML_UTF8, TEXT, TEXT_PLAIN, TEXT_PLAIN_UTF8);
}

