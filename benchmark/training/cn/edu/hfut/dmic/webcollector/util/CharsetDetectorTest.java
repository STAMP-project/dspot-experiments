/**
 * Copyright (C) 2014 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cn.edu.hfut.dmic.webcollector.util;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Vlad Medvedev on 21.01.2016.
 * vladislav.medvedev@devfactory.com
 */
public class CharsetDetectorTest {
    public static final String DEFAULT_ENCODING = "UTF-8";

    @Test
    public void testGuessEncodingByMozilla() throws Exception {
        Assert.assertThat(CharsetDetector.guessEncodingByMozilla(encode("KOI8-R", "??????")), Is.is("KOI8-R"));
        Assert.assertThat(CharsetDetector.guessEncodingByMozilla(encode("Windows-1251", "??????")), Is.is("WINDOWS-1251"));
        Assert.assertThat(CharsetDetector.guessEncodingByMozilla(encode("ISO-8859-7", "????' ????? ??????, ? ????.")), Is.is("ISO-8859-7"));
        Assert.assertThat(CharsetDetector.guessEncodingByMozilla(encode("Windows-1252", "hello")), Is.is(CharsetDetectorTest.DEFAULT_ENCODING));
    }

    @Test
    public void testGuessEncoding() throws Exception {
        Assert.assertThat(CharsetDetector.guessEncoding(encode("KOI8-R", "??????")), Is.is("KOI8-R"));
        Assert.assertThat(CharsetDetector.guessEncoding(encode("Windows-1251", "??????")), Is.is("WINDOWS-1251"));
        Assert.assertThat(CharsetDetector.guessEncoding(encode("ISO-8859-7", "????' ????? ??????, ? ????.")), Is.is("ISO-8859-7"));
        Assert.assertThat(CharsetDetector.guessEncoding(encode("Windows-1252", "hello")), Is.is(CharsetDetectorTest.DEFAULT_ENCODING));
    }
}

