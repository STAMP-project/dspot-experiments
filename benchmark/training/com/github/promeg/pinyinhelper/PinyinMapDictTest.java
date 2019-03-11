package com.github.promeg.pinyinhelper;


import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 * Created by guyacong on 2017/1/1.
 */
public class PinyinMapDictTest {
    @Test
    public void words_null_map_return_null() throws Exception {
        PinyinMapDict dict = new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                return null;
            }
        };
        MatcherAssert.assertThat(dict.words(), CoreMatchers.nullValue());
    }

    @Test
    public void words_nonnull_map_return_keyset() throws Exception {
        final Map<String, String[]> map = new HashMap<String, String[]>();
        map.put("1", null);
        PinyinMapDict dict = new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                return map;
            }
        };
        MatcherAssert.assertThat(dict.words(), CoreMatchers.is(map.keySet()));
    }

    @Test
    public void toPinyin_null_map_return_null() throws Exception {
        PinyinMapDict dict = new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                return null;
            }
        };
        MatcherAssert.assertThat(dict.toPinyin("1"), CoreMatchers.nullValue());
    }

    @Test
    public void toPinyin_nonnull_map_return_value() throws Exception {
        final Map<String, String[]> map = new HashMap<String, String[]>();
        map.put("1", new String[]{ "ONE" });
        PinyinMapDict dict = new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                return map;
            }
        };
        MatcherAssert.assertThat(dict.toPinyin("1"), CoreMatchers.is(map.get("1")));
    }

    @Test
    public void toPinyin_nonnull_map_nokey_return_null() throws Exception {
        final Map<String, String[]> map = new HashMap<String, String[]>();
        map.put("1", new String[]{ "ONE" });
        PinyinMapDict dict = new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                return map;
            }
        };
        MatcherAssert.assertThat(dict.toPinyin("2"), CoreMatchers.nullValue());
    }
}

