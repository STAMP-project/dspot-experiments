/**
 * The MIT License
 * Copyright ? 2010 JmxTrans team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.googlecode.jmxtrans.model.output;


import com.google.common.collect.Maps;
import java.util.Map;
import org.junit.Test;


public class SettingsTests {
    @Test
    public void unsetBooleanSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        assertThat(Settings.getBooleanSetting(settings, "bool")).isFalse();
        assertThat(Settings.getBooleanSetting(settings, "bool", false)).isFalse();
        assertThat(Settings.getBooleanSetting(settings, "bool", true)).isTrue();
        assertThat(Settings.getBooleanSetting(settings, "bool", null)).isNull();
    }

    @Test
    public void correctBooleanSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        settings.put("trueBoolean", true);
        settings.put("falseBoolean", false);
        settings.put("TrueBoolean", Boolean.TRUE);
        settings.put("FalseBoolean", Boolean.FALSE);
        assertThat(Settings.getBooleanSetting(settings, "trueBoolean")).isTrue();
        assertThat(Settings.getBooleanSetting(settings, "falseBoolean")).isFalse();
        assertThat(Settings.getBooleanSetting(settings, "TrueBoolean")).isTrue();
        assertThat(Settings.getBooleanSetting(settings, "FalseBoolean")).isFalse();
    }

    @Test
    public void correctBooleanSettingsWithDefault() {
        Map<String, Object> settings = Maps.newHashMap();
        settings.put("trueBoolean", true);
        settings.put("falseBoolean", false);
        settings.put("TrueBoolean", Boolean.TRUE);
        settings.put("FalseBoolean", Boolean.FALSE);
        assertThat(Settings.getBooleanSetting(settings, "trueBoolean", null)).isTrue();
        assertThat(Settings.getBooleanSetting(settings, "falseBoolean", true)).isFalse();
        assertThat(Settings.getBooleanSetting(settings, "TrueBoolean", false)).isTrue();
        assertThat(Settings.getBooleanSetting(settings, "FalseBoolean", Boolean.TRUE)).isFalse();
    }

    @Test
    public void incorrectBooleanSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        settings.put("Truue", "Truue");
        settings.put("F", "F");
        settings.put("1", "1");
        assertThat(Settings.getBooleanSetting(settings, "Truue")).isFalse();
        assertThat(Settings.getBooleanSetting(settings, "F")).isFalse();
        assertThat(Settings.getBooleanSetting(settings, "1")).isFalse();
    }

    @Test
    public void wrongTypeBooleanSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        settings.put("Integer", 1);
        settings.put("Long", 0);
        assertThat(Settings.getBooleanSetting(settings, "Integer")).isFalse();
        assertThat(Settings.getBooleanSetting(settings, "Long")).isFalse();
    }

    @Test
    public void unsetIntegerSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        assertThat(Settings.getIntegerSetting(settings, "int", 1)).isEqualTo(1);
        assertThat(Settings.getIntegerSetting(settings, "int", null)).isNull();
    }

    @Test
    public void correctIntegerSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        settings.put("1", 1);
        assertThat(Settings.getIntegerSetting(settings, "1", null)).isEqualTo(1);
        assertThat(Settings.getIntegerSetting(settings, "1", 2)).isEqualTo(1);
    }

    @Test
    public void typeConvertedIntegerSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        settings.put("double", 1.1);
        assertThat(Settings.getIntegerSetting(settings, "double", null)).isEqualTo(1);
        assertThat(Settings.getIntegerSetting(settings, "double", 3)).isEqualTo(1);
    }

    @Test
    public void parsedFromStringIntegerSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        settings.put("string", "123");
        assertThat(Settings.getIntegerSetting(settings, "string", null)).isEqualTo(123);
        assertThat(Settings.getIntegerSetting(settings, "string", 3)).isEqualTo(123);
    }

    @Test
    public void parsedFromInvalidStringIntegerSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        settings.put("string", "NOT_AN_INT");
        settings.put("doubleString", "1.1");
        settings.put("emptyString", "");
        assertThat(Settings.getIntegerSetting(settings, "string", null)).isNull();
        assertThat(Settings.getIntegerSetting(settings, "string", 3)).isEqualTo(3);
        assertThat(Settings.getIntegerSetting(settings, "doubleString", null)).isNull();
        assertThat(Settings.getIntegerSetting(settings, "doubleString", 3)).isEqualTo(3);
        assertThat(Settings.getIntegerSetting(settings, "emptyString", null)).isNull();
        assertThat(Settings.getIntegerSetting(settings, "emptyString", 3)).isEqualTo(3);
    }

    @Test
    public void unsetIntSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        assertThat(Settings.getIntSetting(settings, "int", 1)).isEqualTo(1);
    }

    @Test
    public void correctIntSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        settings.put("1", 1);
        assertThat(Settings.getIntSetting(settings, "1", 2)).isEqualTo(1);
    }

    // FIXME behaviour is incoherent with the way getIntegerSettings() works
    @Test(expected = IllegalArgumentException.class)
    public void typeConvertedIntSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        settings.put("double", 1.1);
        assertThat(Settings.getIntSetting(settings, "double", 3)).isEqualTo(1);
    }

    @Test
    public void parsedFromStringIntSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        settings.put("string", "123");
        assertThat(Settings.getIntSetting(settings, "string", 3)).isEqualTo(123);
    }

    // FIXME behaviour is incoherent with the way getIntegerSettings() works
    @Test(expected = IllegalArgumentException.class)
    public void parsedFromInvalidStringIntSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        settings.put("string", "NOT_AN_INT");
        settings.put("doubleString", "1.1");
        settings.put("emptyString", "");
        assertThat(Settings.getIntSetting(settings, "string", 3)).isEqualTo(3);
        assertThat(Settings.getIntSetting(settings, "doubleString", 3)).isEqualTo(3);
        assertThat(Settings.getIntSetting(settings, "emptyString", 3)).isEqualTo(3);
    }

    @Test
    public void unsetStringSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        assertThat(Settings.getStringSetting(settings, "string", "")).isEqualTo("");
        assertThat(Settings.getStringSetting(settings, "string", "NOT_SET")).isEqualTo("NOT_SET");
        assertThat(Settings.getStringSetting(settings, "string", null)).isNull();
    }

    @Test
    public void standardStringSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        settings.put("string", "string");
        assertThat(Settings.getStringSetting(settings, "string", null)).isEqualTo("string");
        assertThat(Settings.getStringSetting(settings, "string", "other")).isEqualTo("string");
    }

    @Test
    public void numberParsedAsStringSettings() {
        Map<String, Object> settings = Maps.newHashMap();
        settings.put("integer", 1);
        settings.put("long", 2L);
        assertThat(Settings.getStringSetting(settings, "integer", null)).isEqualTo("1");
        assertThat(Settings.getStringSetting(settings, "long", null)).isEqualTo("2");
    }
}

