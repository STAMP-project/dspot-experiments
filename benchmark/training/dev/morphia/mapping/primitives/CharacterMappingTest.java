package dev.morphia.mapping.primitives;


import dev.morphia.TestBase;
import dev.morphia.annotations.Id;
import dev.morphia.mapping.MappingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class CharacterMappingTest extends TestBase {
    @Test
    public void emptyStringToPrimitive() {
        final CharacterMappingTest.Characters characters = testMapping("singlePrimitive", "");
        Assert.assertEquals(0, characters.singlePrimitive);
    }

    @Test
    public void emptyStringToPrimitiveArray() {
        final CharacterMappingTest.Characters characters = testMapping("primitiveArray", "");
        Assert.assertArrayEquals("".toCharArray(), characters.primitiveArray);
    }

    @Test
    public void emptyStringToWrapper() {
        final CharacterMappingTest.Characters characters = testMapping("singleWrapper", "");
        Assert.assertEquals(new Character(((char) (0))), characters.singleWrapper);
    }

    @Test
    public void emptyStringToWrapperArray() {
        final CharacterMappingTest.Characters characters = testMapping("wrapperArray", "");
        compare("", characters.wrapperArray);
    }

    @Test
    public void mapping() throws Exception {
        getMorphia().map(CharacterMappingTest.Characters.class);
        final CharacterMappingTest.Characters entity = new CharacterMappingTest.Characters();
        entity.listWrapperArray.add(new Character[]{ '1', 'g', '#' });
        entity.listPrimitiveArray.add(new char[]{ '1', 'd', 'z' });
        entity.listWrapper.addAll(Arrays.asList('*', ' ', '\u8888'));
        entity.singlePrimitive = 'a';
        entity.singleWrapper = 'b';
        entity.primitiveArray = new char[]{ 'a', 'b' };
        entity.wrapperArray = new Character[]{ 'X', 'y', 'Z' };
        entity.nestedPrimitiveArray = new char[][]{ new char[]{ '5', '-' }, new char[]{ 'a', 'b' } };
        entity.nestedWrapperArray = new Character[][]{ new Character[]{ '*', '$', '\u4824' }, new Character[]{ 'X', 'y', 'Z' } };
        getDs().save(entity);
        final CharacterMappingTest.Characters loaded = getDs().get(entity);
        Assert.assertNotNull(loaded.id);
        Assert.assertArrayEquals(entity.listWrapperArray.get(0), loaded.listWrapperArray.get(0));
        Assert.assertArrayEquals(entity.listPrimitiveArray.get(0), loaded.listPrimitiveArray.get(0));
        Assert.assertEquals(entity.listWrapper, loaded.listWrapper);
        Assert.assertEquals(entity.singlePrimitive, loaded.singlePrimitive);
        Assert.assertEquals(entity.singleWrapper, loaded.singleWrapper);
        Assert.assertArrayEquals(entity.primitiveArray, loaded.primitiveArray);
        Assert.assertArrayEquals(entity.wrapperArray, loaded.wrapperArray);
        Assert.assertArrayEquals(entity.nestedPrimitiveArray, loaded.nestedPrimitiveArray);
        Assert.assertArrayEquals(entity.nestedWrapperArray, loaded.nestedWrapperArray);
    }

    @Test
    public void singleCharToPrimitive() {
        final CharacterMappingTest.Characters characters = testMapping("singlePrimitive", "a");
        Assert.assertEquals('a', characters.singlePrimitive);
    }

    @Test
    public void singleCharToPrimitiveArray() {
        final CharacterMappingTest.Characters characters = testMapping("primitiveArray", "a");
        Assert.assertArrayEquals("a".toCharArray(), characters.primitiveArray);
        getDs().save(characters);
    }

    @Test
    public void singleCharToWrapper() {
        final CharacterMappingTest.Characters characters = testMapping("singleWrapper", "a");
        Assert.assertEquals(new Character('a'), characters.singleWrapper);
    }

    @Test
    public void singleCharToWrapperArray() {
        final CharacterMappingTest.Characters characters = testMapping("wrapperArray", "a");
        compare("a", characters.wrapperArray);
    }

    @Test(expected = MappingException.class)
    public void stringToPrimitive() {
        final CharacterMappingTest.Characters characters = testMapping("singlePrimitive", "ab");
    }

    @Test
    public void stringToPrimitiveArray() {
        final CharacterMappingTest.Characters characters = testMapping("primitiveArray", "abc");
        Assert.assertArrayEquals("abc".toCharArray(), characters.primitiveArray);
    }

    @Test(expected = MappingException.class)
    public void stringToWrapper() {
        final CharacterMappingTest.Characters characters = testMapping("singleWrapper", "ab");
    }

    @Test
    public void stringToWrapperArray() {
        final CharacterMappingTest.Characters characters = testMapping("wrapperArray", "abc");
        compare("abc", characters.wrapperArray);
    }

    public static class Characters {
        @Id
        private ObjectId id;

        private List<Character[]> listWrapperArray = new ArrayList<Character[]>();

        private List<char[]> listPrimitiveArray = new ArrayList<char[]>();

        private List<Character> listWrapper = new ArrayList<Character>();

        private char singlePrimitive;

        private Character singleWrapper;

        private char[] primitiveArray;

        private Character[] wrapperArray;

        private char[][] nestedPrimitiveArray;

        private Character[][] nestedWrapperArray;
    }
}

