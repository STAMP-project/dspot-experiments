package com.alibaba.json.test;


import java.util.Random;
import junit.framework.TestCase;


/**
 * Created by wenshao on 05/01/2017.
 */
public class FNVHashTest extends TestCase {
    char[] digLetters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_".toCharArray();

    Random r = new Random();

    public void test_fnv_hash() throws Exception {
        int COUNT = (1000 * 1000) * 1000;
        int collision_cnt = 0;
        // Map<Long, char[]> map = new HashMap<Long, char[]>(COUNT);
        long id_hash = FNVHashTest.fnv_hash("id".toCharArray());
        System.out.printf(("id : " + id_hash));
        System.out.println();
        for (int i = 0; i < (digLetters.length); ++i) {
            System.out.print(digLetters[i]);
            System.out.print(",");
        }
        // for (int i = 0; i < COUNT; ++i) {
        // char[] chars = gen();
        // int hash = fnv_hash32(chars);
        // if (hash == id_hash) {
        // System.out.println(new String(chars));
        // break;
        // }
        // }
        // for (int i = 0; i < COUNT; ++i) {
        // char[] chars = gen();
        // Long hash = bkdr_hash(chars);
        // 
        // char[] chars_2 = map.get(hash);
        // if (chars_2 != null) {
        // if (!Arrays.equals(chars, chars_2)) {
        // System.out.println("collision (" + collision_cnt++ + ") : " + new String(chars) + " -> " + new String(chars_2));
        // }
        // } else {
        // map.put(hash, chars);
        // }
        // }
    }
}

