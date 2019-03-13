package com.baeldung.java.countingChars;


import com.google.common.base.CharMatcher;
import java.util.function.IntPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * *
 * Example of counting chars in a String.
 */
public class CountCharsExampleUnitTest {
    @Test
    public void givenString_whenUsingLoop_thenCountChars() {
        String someString = "elephant";
        char someChar = 'e';
        int count = 0;
        for (int i = 0; i < (someString.length()); i++) {
            if ((someString.charAt(i)) == someChar) {
                count++;
            }
        }
        Assert.assertEquals(2, count);
    }

    @Test
    public void givenString_whenUsingReplace_thenCountChars() {
        String someString = "elephant";
        int count = (someString.length()) - (someString.replace("e", "").length());
        Assert.assertEquals(2, count);
    }

    @Test
    public void givenString_whenUsingSplit_thenCountChars() {
        String someString = "elephant";
        int count = (someString.split("e", (-1)).length) - 1;
        Assert.assertEquals(2, count);
    }

    @Test
    public void givenString_whenUsingReqExp_thenCountChars() {
        Pattern pattern = Pattern.compile("[^e]*e");
        Matcher matcher = pattern.matcher("elephant");
        int count = 0;
        while (matcher.find()) {
            count++;
        } 
        Assert.assertEquals(2, count);
    }

    @Test
    public void givenString_whenUsingRecursion_thenCountChars() {
        int count = useRecursion("elephant", 'e', 0);
        Assert.assertEquals(2, count);
    }

    @Test
    public void givenString_whenUsingStringUtils_thenCountChars() throws InterruptedException {
        int count = StringUtils.countMatches("elephant", "e");
        Assert.assertEquals(2, count);
    }

    @Test
    public void givenString_whenUsingJava8Features_thenCountChars() {
        String someString = "elephant";
        long count = someString.chars().filter(( ch) -> ch == 'e').count();
        Assert.assertEquals(2, count);
        long count2 = someString.codePoints().filter(( ch) -> ch == 'e').count();
        Assert.assertEquals(2, count2);
    }

    @Test
    public void givenString_whenUsingGuavaCharMatcher_thenCountChars() {
        int count = CharMatcher.is('e').countIn("elephant");
        Assert.assertEquals(2, count);
    }
}

