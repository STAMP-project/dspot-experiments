package com.github.promeg.pinyinhelper;


import HanyuPinyinCaseType.UPPERCASE;
import HanyuPinyinToneType.WITHOUT_TONE;
import HanyuPinyinVCharType.WITH_V;
import Pinyin.mPinyinDicts;
import Pinyin.mSelector;
import Pinyin.mTrieDict;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Test;


/**
 * Assert that our pinyin helper produce the same result for all Characters as Pinyin4J.
 *
 * Created by guyacong on 2015/9/28.
 */
public class PinyinTest {
    @Test
    public void init_with_null() throws Exception {
        Pinyin.init(null);
        MatcherAssert.assertThat(mTrieDict, CoreMatchers.nullValue());
        MatcherAssert.assertThat(mPinyinDicts, CoreMatchers.nullValue());
        MatcherAssert.assertThat(mSelector, CoreMatchers.nullValue());
    }

    @Test
    public void init_with_default_new_config() throws Exception {
        Pinyin.init(null);// reset

        Pinyin.init(Pinyin.newConfig());
        MatcherAssert.assertThat(mTrieDict, CoreMatchers.nullValue());
        MatcherAssert.assertThat(mPinyinDicts, CoreMatchers.nullValue());
        MatcherAssert.assertThat(mSelector, CoreMatchers.nullValue());
    }

    @Test
    public void init_with_new_config() throws Exception {
        Pinyin.init(null);// reset

        Pinyin.init(Pinyin.newConfig().with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                HashMap<String, String[]> map = new HashMap<String, String[]>();
                map.put("1", null);
                return map;
            }
        }));
        MatcherAssert.assertThat(mTrieDict, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(mPinyinDicts.size(), Is.is(1));
        MatcherAssert.assertThat(mSelector, CoreMatchers.instanceOf(ForwardLongestSelector.class));
    }

    @Test
    public void add_null_to_null() throws Exception {
        Pinyin.init(null);// reset

        Pinyin.add(null);
        MatcherAssert.assertThat(mTrieDict, CoreMatchers.nullValue());
        MatcherAssert.assertThat(mPinyinDicts, CoreMatchers.nullValue());
        MatcherAssert.assertThat(mSelector, CoreMatchers.nullValue());
    }

    @Test
    public void add_null_to_nonnull() throws Exception {
        Pinyin.init(null);// reset

        Pinyin.init(Pinyin.newConfig().with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                HashMap<String, String[]> map = new HashMap<String, String[]>();
                map.put("1", null);
                return map;
            }
        }));
        Pinyin.add(null);
        MatcherAssert.assertThat(mTrieDict.containsMatch("1"), Is.is(true));
        MatcherAssert.assertThat(mTrieDict, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(mPinyinDicts.size(), Is.is(1));
        MatcherAssert.assertThat(mSelector, CoreMatchers.instanceOf(ForwardLongestSelector.class));
    }

    @Test
    public void add_duplicate() throws Exception {
        Pinyin.init(null);// reset

        PinyinMapDict dict = new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                HashMap<String, String[]> map = new HashMap<String, String[]>();
                map.put("1", null);
                return map;
            }
        };
        Pinyin.init(Pinyin.newConfig().with(dict));
        Pinyin.add(dict);
        MatcherAssert.assertThat(mTrieDict, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(mTrieDict.containsMatch("1"), Is.is(true));
        MatcherAssert.assertThat(mPinyinDicts.size(), Is.is(1));
        MatcherAssert.assertThat(mSelector, CoreMatchers.instanceOf(ForwardLongestSelector.class));
    }

    @Test
    public void add_new() throws Exception {
        Pinyin.init(null);// reset

        PinyinMapDict dict = new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                HashMap<String, String[]> map = new HashMap<String, String[]>();
                map.put("1", null);
                return map;
            }
        };
        Pinyin.init(Pinyin.newConfig().with(dict));
        Pinyin.add(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                HashMap<String, String[]> map = new HashMap<String, String[]>();
                map.put("2", null);
                return map;
            }
        });
        MatcherAssert.assertThat(mTrieDict, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(mTrieDict.containsMatch("1"), Is.is(true));
        MatcherAssert.assertThat(mTrieDict.containsMatch("2"), Is.is(true));
        MatcherAssert.assertThat(mPinyinDicts.size(), Is.is(2));
        MatcherAssert.assertThat(mSelector, CoreMatchers.instanceOf(ForwardLongestSelector.class));
    }

    @Test
    public void testIsChinese() throws BadHanyuPinyinOutputFormatCombination {
        char[] allChars = PinyinTest.allChars();
        final int allCharsLength = allChars.length;
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setToneType(WITHOUT_TONE);
        format.setCaseType(UPPERCASE);
        format.setVCharType(WITH_V);
        for (int i = 0; i < allCharsLength; i++) {
            char targetChar = allChars[i];
            String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(targetChar, format);
            if ((pinyins != null) && ((pinyins.length) > 0)) {
                // is chinese
                MatcherAssert.assertThat(Pinyin.isChinese(targetChar), Is.is(true));
            } else {
                // not chinese
                MatcherAssert.assertThat(Pinyin.isChinese(targetChar), Is.is(false));
            }
        }
    }

    @Test
    public void testToPinyin_char() throws BadHanyuPinyinOutputFormatCombination {
        char[] allChars = PinyinTest.allChars();
        final int allCharsLength = allChars.length;
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setToneType(WITHOUT_TONE);
        format.setCaseType(UPPERCASE);
        format.setVCharType(WITH_V);
        int chineseCount = 0;
        for (int i = 0; i < allCharsLength; i++) {
            char targetChar = allChars[i];
            String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(targetChar, format);
            if ((pinyins != null) && ((pinyins.length) > 0)) {
                // is chinese
                chineseCount++;
                MatcherAssert.assertThat(Pinyin.toPinyin(targetChar), IsEqual.equalTo(pinyins[0]));
            } else {
                // not chinese
                MatcherAssert.assertThat(Pinyin.toPinyin(targetChar), IsEqual.equalTo(String.valueOf(targetChar)));
            }
        }
        // CHECKSTYLE:OFF
        int expectedChineseCount = 20378;
        // CHECKSTYLE:ON
        MatcherAssert.assertThat(chineseCount, Is.is(expectedChineseCount));
    }

    @Test
    public void testInit_no_dict() {
        MatcherAssert.assertThat(mTrieDict, CoreMatchers.nullValue());
    }

    @Test
    public void testInit_with_null_dict() {
        Pinyin.init(Pinyin.newConfig().with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                return null;
            }
        }).with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                return null;
            }
        }));
        MatcherAssert.assertThat(mTrieDict, CoreMatchers.nullValue());
    }

    @Test
    public void testInit_with_nonnull_nokey_dict() {
        Pinyin.init(Pinyin.newConfig().with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                return new HashMap<String, String[]>();
            }
        }));
        MatcherAssert.assertThat(mTrieDict, CoreMatchers.nullValue());
    }

    @Test
    public void testInit_with_nonnull_haskey_dict() {
        Pinyin.init(Pinyin.newConfig().with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                Map<String, String[]> map = new HashMap<String, String[]>();
                map.put("1", new String[]{  });
                return map;
            }
        }));
        MatcherAssert.assertThat(mTrieDict.containsMatch("1"), Is.is(true));
    }

    @Test
    public void testInit_with_multi_haskey_dict() {
        Pinyin.init(Pinyin.newConfig().with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                Map<String, String[]> map = new HashMap<String, String[]>();
                map.put("1", new String[]{  });
                return map;
            }
        }).with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                Map<String, String[]> map = new HashMap<String, String[]>();
                map.put("2", new String[]{  });
                return map;
            }
        }));
        MatcherAssert.assertThat(mTrieDict.containsMatch("1"), Is.is(true));
        MatcherAssert.assertThat(mTrieDict.containsMatch("2"), Is.is(true));
    }

    @Test
    public void testInit_with_multi_hasduplicatekey_dict() {
        Pinyin.init(Pinyin.newConfig().with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                Map<String, String[]> map = new HashMap<String, String[]>();
                map.put("1", new String[]{ "Hello" });
                return map;
            }
        }).with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                Map<String, String[]> map = new HashMap<String, String[]>();
                map.put("1", new String[]{ "world" });
                return map;
            }
        }));
        MatcherAssert.assertThat(mTrieDict.containsMatch("1"), Is.is(true));// first one in wins

    }

    @Test
    public void testToPinyin_Str_null_str() {
        String str = null;
        MatcherAssert.assertThat(Pinyin.toPinyin(str, ","), CoreMatchers.nullValue());
    }

    @Test
    public void testToPinyin_Str_empty_str() {
        String str = "";
        MatcherAssert.assertThat(Pinyin.toPinyin(str, ","), Is.is(""));
    }

    @Test
    public void testToPinyin_Str_no_chinese_str() {
        String str = "hello";
        String expected = "h,e,l,l,o";
        MatcherAssert.assertThat(Pinyin.toPinyin(str, ","), Is.is(expected));
    }

    @Test
    public void testToPinyin_Str_no_dict() {
        String str = "??????test,??????;?>?";
        String expected = "YI GE CE SHI ZHONG QING t e s t , ZHONG YING WEN FU HAO ? ; ? > ?";
        MatcherAssert.assertThat(Pinyin.toPinyin(str, " "), Is.is(expected));
    }

    @Test
    public void testToPinyin_Str_empty_dict() {
        Pinyin.init(Pinyin.newConfig().with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                return null;
            }
        }));
        String str = "??????test,??????;?>?";
        String expected = "YI GE CE SHI ZHONG QING t e s t , ZHONG YING WEN FU HAO ? ; ? > ?";
        MatcherAssert.assertThat(Pinyin.toPinyin(str, " "), Is.is(expected));
    }

    @Test
    public void testToPinyin_Str_one_dict() {
        Pinyin.init(Pinyin.newConfig().with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                Map<String, String[]> map = new HashMap<String, String[]>();
                map.put("??", new String[]{ "CHONG", "QING" });
                return map;
            }
        }));
        String str = "??????test,??????;?>?";
        String expected = "YI GE CE SHI CHONG QING t e s t , ZHONG YING WEN FU HAO ? ; ? > ?";
        MatcherAssert.assertThat(Pinyin.toPinyin(str, " "), Is.is(expected));
    }

    @Test
    public void testToPinyin_Str_multi_dict() {
        Pinyin.init(Pinyin.newConfig().with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                Map<String, String[]> map = new HashMap<String, String[]>();
                map.put("??", new String[]{ "CHONG", "QING" });
                return map;
            }
        }).with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                Map<String, String[]> map = new HashMap<String, String[]>();
                map.put("??", new String[]{ "NOT", "MATCH" });
                map.put("??", new String[]{ "CHANG", "AN" });
                return map;
            }
        }));
        String str = "?????????test,??????;?>?";
        String expected = "YI GE CE SHI CHONG QING HE CHANG AN t e s t , ZHONG YING WEN FU HAO ? ; ? > ?";
        MatcherAssert.assertThat(Pinyin.toPinyin(str, " "), Is.is(expected));
    }

    @Test
    public void testToPinyin_Str_overlap() {
        Pinyin.init(Pinyin.newConfig().with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                Map<String, String[]> map = new HashMap<String, String[]>();
                map.put("???", new String[]{ "SHOULD", "NOT", "MATCH" });
                map.put("????", new String[]{ "ZHONG", "GUO", "REN", "MIN" });
                return map;
            }
        }));
        String str = "??????????";
        String expected = "YI GE CE SHI ZHONG GUO REN MIN HEN ZAN";
        MatcherAssert.assertThat(Pinyin.toPinyin(str, " "), Is.is(expected));
    }

    @Test
    public void testToPinyin_traditional_chars() throws BadHanyuPinyinOutputFormatCombination {
        for (char c : TraditionalCharSet.All) {
            MatcherAssert.assertThat(Pinyin.isChinese(c), Is.is(true));
        }
    }
}

