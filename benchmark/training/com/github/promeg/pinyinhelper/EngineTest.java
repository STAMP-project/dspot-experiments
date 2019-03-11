package com.github.promeg.pinyinhelper;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


/**
 * Created by guyacong on 2016/12/23.
 */
public class EngineTest {
    List<PinyinDict> mPinyinDicts;

    @Test
    public void toPinyin_withOneDict() throws Exception {
        mPinyinDicts.add(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                Map<String, String[]> map = new HashMap<String, String[]>();
                map.put("??", new String[]{ "CHONG", "QING" });
                map.put("??", new String[]{ "CHANG", "AN" });
                map.put("??", new String[]{ "SI", "CHUAN" });
                return map;
            }
        });
        String result = Engine.toPinyin("????????!??", Utils.dictsToTrie(mPinyinDicts), mPinyinDicts, ",", new ForwardLongestSelector());
        String expect = "CHONG,QING,HE,CHANG,AN,DOU,HEN,BANG,!,SI,CHUAN";
        MatcherAssert.assertThat(expect, Is.is(result));
    }

    @Test
    public void toPinyin_withZeroDict() throws Exception {
        mPinyinDicts.clear();
        String result = Engine.toPinyin("????????!", Utils.dictsToTrie(mPinyinDicts), mPinyinDicts, ",", new ForwardLongestSelector());
        String expect = "ZHONG,QING,HE,ZHANG,AN,DOU,HEN,BANG,!";
        MatcherAssert.assertThat(expect, Is.is(result));
    }

    @Test
    public void toPinyin_withNullDict() throws Exception {
        String result = Engine.toPinyin("????????!", null, null, ",", new ForwardLongestSelector());
        String expect = "ZHONG,QING,HE,ZHANG,AN,DOU,HEN,BANG,!";
        MatcherAssert.assertThat(expect, Is.is(result));
    }

    @Test
    public void toPinyin_withMultiDict() throws Exception {
        // First one wins???????
        mPinyinDicts.add(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                Map<String, String[]> map = new HashMap<String, String[]>();
                map.put("??", new String[]{ "CHONG", "QING" });
                return map;
            }
        });
        mPinyinDicts.add(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                Map<String, String[]> map = new HashMap<String, String[]>();
                map.put("??", new String[]{ "NOT", "MATCH" });
                map.put("??", new String[]{ "CHANG", "AN" });
                return map;
            }
        });
        String result = Engine.toPinyin("????????!", Utils.dictsToTrie(mPinyinDicts), mPinyinDicts, ",", new ForwardLongestSelector());
        String expect = "CHONG,QING,HE,CHANG,AN,DOU,HEN,BANG,!";
        MatcherAssert.assertThat(expect, Is.is(result));
    }
}

