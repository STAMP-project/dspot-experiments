package org.baeldung.convertarraytostring;


import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class ArrayToStringUnitTest {
    // convert with Java
    @Test
    public void givenAStringArray_whenConvertBeforeJava8_thenReturnString() {
        String[] strArray = new String[]{ "Convert", "Array", "With", "Java" };
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < (strArray.length); i++) {
            stringBuilder.append(strArray[i]);
        }
        String joinedString = stringBuilder.toString();
        MatcherAssert.assertThat(joinedString, CoreMatchers.instanceOf(String.class));
        Assert.assertEquals("ConvertArrayWithJava", joinedString);
    }

    @Test
    public void givenAString_whenConvertBeforeJava8_thenReturnStringArray() {
        String input = "lorem ipsum dolor sit amet";
        String[] strArray = input.split(" ");
        MatcherAssert.assertThat(strArray, CoreMatchers.instanceOf(String[].class));
        Assert.assertEquals(5, strArray.length);
        input = "loremipsum";
        strArray = input.split("");
        MatcherAssert.assertThat(strArray, CoreMatchers.instanceOf(String[].class));
        Assert.assertEquals(10, strArray.length);
    }

    @Test
    public void givenAnIntArray_whenConvertBeforeJava8_thenReturnString() {
        int[] strArray = new int[]{ 1, 2, 3, 4, 5 };
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < (strArray.length); i++) {
            stringBuilder.append(Integer.valueOf(strArray[i]));
        }
        String joinedString = stringBuilder.toString();
        MatcherAssert.assertThat(joinedString, CoreMatchers.instanceOf(String.class));
        Assert.assertEquals("12345", joinedString);
    }

    // convert with Java Stream API
    @Test
    public void givenAStringArray_whenConvertWithJavaStream_thenReturnString() {
        String[] strArray = new String[]{ "Convert", "With", "Java", "Streams" };
        String joinedString = Arrays.stream(strArray).collect(Collectors.joining());
        MatcherAssert.assertThat(joinedString, CoreMatchers.instanceOf(String.class));
        Assert.assertEquals("ConvertWithJavaStreams", joinedString);
        joinedString = Arrays.stream(strArray).collect(Collectors.joining(","));
        MatcherAssert.assertThat(joinedString, CoreMatchers.instanceOf(String.class));
        Assert.assertEquals("Convert,With,Java,Streams", joinedString);
    }

    // convert with Apache Commons
    @Test
    public void givenAStringArray_whenConvertWithApacheCommons_thenReturnString() {
        String[] strArray = new String[]{ "Convert", "With", "Apache", "Commons" };
        String joinedString = StringUtils.join(strArray);
        MatcherAssert.assertThat(joinedString, CoreMatchers.instanceOf(String.class));
        Assert.assertEquals("ConvertWithApacheCommons", joinedString);
    }

    @Test
    public void givenAString_whenConvertWithApacheCommons_thenReturnStringArray() {
        String input = "lorem ipsum dolor sit amet";
        String[] strArray = StringUtils.split(input, " ");
        MatcherAssert.assertThat(strArray, CoreMatchers.instanceOf(String[].class));
        Assert.assertEquals(5, strArray.length);
    }

    // convert with Guava
    @Test
    public void givenAStringArray_whenConvertWithGuava_thenReturnString() {
        String[] strArray = new String[]{ "Convert", "With", "Guava", null };
        String joinedString = Joiner.on("").skipNulls().join(strArray);
        MatcherAssert.assertThat(joinedString, CoreMatchers.instanceOf(String.class));
        Assert.assertEquals("ConvertWithGuava", joinedString);
    }

    @Test
    public void givenAString_whenConvertWithGuava_thenReturnStringArray() {
        String input = "lorem ipsum dolor sit amet";
        List<String> resultList = Splitter.on(' ').trimResults().omitEmptyStrings().splitToList(input);
        String[] strArray = resultList.toArray(new String[0]);
        MatcherAssert.assertThat(strArray, CoreMatchers.instanceOf(String[].class));
        Assert.assertEquals(5, strArray.length);
    }
}

