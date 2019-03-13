package com.fishercoder;


import _409.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _409Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(7, _409Test.solution1.longestPalindrome("abccccdd"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(7, _409Test.solution1.longestPalindrome("abccAccdd"));
    }

    @Test
    public void test3() {
        Assert.assertEquals(983, _409Test.solution1.longestPalindrome("civilwartestingwhetherthatnaptionoranynartionsoconceivedandsodedicatedcanlongendureWeareqmetonagreatbattlefiemldoftzhatwarWehavecometodedicpateaportionofthatfieldasafinalrestingplaceforthosewhoheregavetheirlivesthatthatnationmightliveItisaltogetherfangandproperthatweshoulddothisButinalargersensewecannotdedicatewecannotconsecratewecannothallowthisgroundThebravelmenlivinganddeadwhostruggledherehaveconsecrateditfaraboveourpoorponwertoaddordetractTgheworldadswfilllittlenotlenorlongrememberwhatwesayherebutitcanneverforgetwhattheydidhereItisforusthelivingrathertobededicatedheretotheulnfinishedworkwhichtheywhofoughtherehavethusfarsonoblyadvancedItisratherforustobeherededicatedtothegreattdafskremainingbeforeusthatfromthesehonoreddeadwetakeincreaseddevotiontothatcauseforwhichtheygavethelastpfullmeasureofdevotionthatweherehighlyresolvethatthesedeadshallnothavediedinvainthatthisnationunsderGodshallhaveanewbirthoffreedomandthatgovernmentofthepeoplebythepeopleforthepeopleshallnotperishfromtheearth"));
    }

    @Test
    public void test4() {
        Assert.assertEquals(3, _409Test.solution1.longestPalindrome("ccc"));
    }
}

