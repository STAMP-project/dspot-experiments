package com.github.promeg.tinypinyin.lexicons.java.cncity;


import com.github.promeg.pinyinhelper.Pinyin;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


/**
 * Created by guyacong on 2016/12/23.
 */
// CHECKSTYLE:OFF
public class CnCityDictTest {
    CnCityDict mDict;

    @Test
    public void words() throws Exception {
        Set<String> words = mDict.mapping().keySet();
        MatcherAssert.assertThat(words.contains(null), Is.is(false));
        MatcherAssert.assertThat(words.size(), Is.is(97));
    }

    @Test
    public void toPinyin() throws Exception {
        Set<String> words = mDict.mapping().keySet();
        for (String word : words) {
            String[] pinyins = mDict.mapping().get(word);
            MatcherAssert.assertThat(word.length(), Is.is(pinyins.length));
        }
    }

    @Test
    public void toPinyin_test_not_same_with_PinyinOrigin() throws Exception {
        Set<String> words = mDict.mapping().keySet();
        for (String word : words) {
            String[] originPinyins = new String[word.length()];
            for (int i = 0; i < (word.length()); i++) {
                originPinyins[i] = Pinyin.toPinyin(word.charAt(i));
            }
            String[] pinyins = mDict.mapping().get(word);
            boolean hasDifferent = false;
            for (int i = 0; i < (word.length()); i++) {
                if (!(originPinyins[i].equalsIgnoreCase(pinyins[i]))) {
                    hasDifferent = true;
                    break;
                }
            }
            MatcherAssert.assertThat(hasDifferent, Is.is(true));
        }
    }

    // ????????????????, fix issue 5
    @Test
    public void test_pinyin_only_contains_letters() throws Exception {
        Set<String> words = mDict.mapping().keySet();
        for (String word : words) {
            String[] pinyins = mDict.mapping().get(word);
            for (String pinyin : pinyins) {
                MatcherAssert.assertThat(pinyin.matches("[a-zA-Z]+"), Is.is(true));
            }
        }
    }
}

/**
 * CHECKSTYLE:ON
 */
