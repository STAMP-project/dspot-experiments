/**
 *
 */
package com.baeldung.string;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author swpraman
 */
public class AppendCharAtPositionXUnitTest {
    private AppendCharAtPositionX appendCharAtPosition = new AppendCharAtPositionX();

    private String word = "Titanc";

    private char letter = 'i';

    @Test
    public void whenUsingCharacterArrayAndCharacterAddedAtBeginning_shouldAddCharacter() {
        Assert.assertEquals("iTitanc", appendCharAtPosition.addCharUsingCharArray(word, letter, 0));
    }

    @Test
    public void whenUsingSubstringAndCharacterAddedAtBeginning_shouldAddCharacter() {
        Assert.assertEquals("iTitanc", appendCharAtPosition.addCharUsingSubstring(word, letter, 0));
    }

    @Test
    public void whenUsingStringBuilderAndCharacterAddedAtBeginning_shouldAddCharacter() {
        Assert.assertEquals("iTitanc", appendCharAtPosition.addCharUsingStringBuilder(word, letter, 0));
    }

    @Test
    public void whenUsingCharacterArrayAndCharacterAddedAtMiddle_shouldAddCharacter() {
        Assert.assertEquals("Titianc", appendCharAtPosition.addCharUsingCharArray(word, letter, 3));
    }

    @Test
    public void whenUsingSubstringAndCharacterAddedAtMiddle_shouldAddCharacter() {
        Assert.assertEquals("Titianc", appendCharAtPosition.addCharUsingSubstring(word, letter, 3));
    }

    @Test
    public void whenUsingStringBuilderAndCharacterAddedAtMiddle_shouldAddCharacter() {
        Assert.assertEquals("Titianc", appendCharAtPosition.addCharUsingStringBuilder(word, letter, 3));
    }

    @Test
    public void whenUsingCharacterArrayAndCharacterAddedAtEnd_shouldAddCharacter() {
        Assert.assertEquals("Titanci", appendCharAtPosition.addCharUsingCharArray(word, letter, word.length()));
    }

    @Test
    public void whenUsingSubstringAndCharacterAddedAtEnd_shouldAddCharacter() {
        Assert.assertEquals("Titanci", appendCharAtPosition.addCharUsingSubstring(word, letter, word.length()));
    }

    @Test
    public void whenUsingStringBuilderAndCharacterAddedAtEnd_shouldAddCharacter() {
        Assert.assertEquals("Titanci", appendCharAtPosition.addCharUsingStringBuilder(word, letter, word.length()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenUsingCharacterArrayAndCharacterAddedAtNegativePosition_shouldThrowException() {
        appendCharAtPosition.addCharUsingStringBuilder(word, letter, (-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenUsingSubstringAndCharacterAddedAtNegativePosition_shouldThrowException() {
        appendCharAtPosition.addCharUsingStringBuilder(word, letter, (-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenUsingStringBuilderAndCharacterAddedAtNegativePosition_shouldThrowException() {
        appendCharAtPosition.addCharUsingStringBuilder(word, letter, (-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenUsingCharacterArrayAndCharacterAddedAtInvalidPosition_shouldThrowException() {
        appendCharAtPosition.addCharUsingStringBuilder(word, letter, ((word.length()) + 2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenUsingSubstringAndCharacterAddedAtInvalidPosition_shouldThrowException() {
        appendCharAtPosition.addCharUsingStringBuilder(word, letter, ((word.length()) + 2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenUsingStringBuilderAndCharacterAddedAtInvalidPosition_shouldThrowException() {
        appendCharAtPosition.addCharUsingStringBuilder(word, letter, ((word.length()) + 2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenUsingCharacterArrayAndCharacterAddedAtPositionXAndStringIsNull_shouldThrowException() {
        appendCharAtPosition.addCharUsingStringBuilder(null, letter, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenUsingSubstringAndCharacterAddedAtPositionXAndStringIsNull_shouldThrowException() {
        appendCharAtPosition.addCharUsingStringBuilder(null, letter, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenUsingStringBuilderAndCharacterAddedAtPositionXAndStringIsNull_shouldThrowException() {
        appendCharAtPosition.addCharUsingStringBuilder(null, letter, 3);
    }
}

