/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.mail.javamail;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Brian Hanafee
 * @author Sam Brannen
 * @since 09.07.2005
 */
public class InternetAddressEditorTests {
    private static final String EMPTY = "";

    private static final String SIMPLE = "nobody@nowhere.com";

    private static final String BAD = "(";

    private final InternetAddressEditor editor = new InternetAddressEditor();

    @Test
    public void uninitialized() {
        Assert.assertEquals("Uninitialized editor did not return empty value string", InternetAddressEditorTests.EMPTY, editor.getAsText());
    }

    @Test
    public void setNull() {
        editor.setAsText(null);
        Assert.assertEquals("Setting null did not result in empty value string", InternetAddressEditorTests.EMPTY, editor.getAsText());
    }

    @Test
    public void setEmpty() {
        editor.setAsText(InternetAddressEditorTests.EMPTY);
        Assert.assertEquals("Setting empty string did not result in empty value string", InternetAddressEditorTests.EMPTY, editor.getAsText());
    }

    @Test
    public void allWhitespace() {
        editor.setAsText(" ");
        Assert.assertEquals("All whitespace was not recognized", InternetAddressEditorTests.EMPTY, editor.getAsText());
    }

    @Test
    public void simpleGoodAddress() {
        editor.setAsText(InternetAddressEditorTests.SIMPLE);
        Assert.assertEquals("Simple email address failed", InternetAddressEditorTests.SIMPLE, editor.getAsText());
    }

    @Test
    public void excessWhitespace() {
        editor.setAsText(((" " + (InternetAddressEditorTests.SIMPLE)) + " "));
        Assert.assertEquals("Whitespace was not stripped", InternetAddressEditorTests.SIMPLE, editor.getAsText());
    }

    @Test(expected = IllegalArgumentException.class)
    public void simpleBadAddress() {
        editor.setAsText(InternetAddressEditorTests.BAD);
    }
}

