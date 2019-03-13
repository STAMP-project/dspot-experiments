package org.jabref.logic.bst;


import org.junit.jupiter.api.Test;


public class TextPrefixFunctionTest {
    @Test
    public void testPrefix() {
        TextPrefixFunctionTest.assertPrefix("i", "i");
        TextPrefixFunctionTest.assertPrefix("0I~ ", "0I~ ");
        TextPrefixFunctionTest.assertPrefix("Hi Hi", "Hi Hi ");
        TextPrefixFunctionTest.assertPrefix("{\\oe}", "{\\oe}");
        TextPrefixFunctionTest.assertPrefix("Hi {\\oe   }H", "Hi {\\oe   }Hi ");
        TextPrefixFunctionTest.assertPrefix("Jonat", "Jonathan Meyer and Charles Louis Xavier Joseph de la Vall{\\\'e}e Poussin");
        TextPrefixFunctionTest.assertPrefix("{\\\'e}", "{\\\'e}");
        TextPrefixFunctionTest.assertPrefix("{\\\'{E}}doua", "{\\\'{E}}douard Masterly");
        TextPrefixFunctionTest.assertPrefix("Ulric", "Ulrich {\\\"{U}}nderwood and Ned {\\~N}et and Paul {\\={P}}ot");
    }
}

