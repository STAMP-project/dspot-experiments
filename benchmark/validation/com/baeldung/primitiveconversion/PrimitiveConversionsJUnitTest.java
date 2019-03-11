package com.baeldung.primitiveconversion;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PrimitiveConversionsJUnitTest {
    private static final Logger LOG = LoggerFactory.getLogger(PrimitiveConversionsJUnitTest.class);

    @Test
    public void givenDataWithLessBits_whenAttributingToLargerSizeVariable_thenNoSpecialNotation() {
        int myInt = 127;
        long myLong = myInt;
        Assert.assertEquals(127L, myLong);
        float myFloat = myLong;
        Assert.assertEquals(127.0F, myFloat, 1.0E-5F);
        double myDouble = myLong;
        Assert.assertEquals(127.0, myDouble, 1.0E-5);
    }

    @Test
    public void givenDataWithMoreBits_whenAttributingToSmallerSizeVariable_thenCastOperatorNeeded() {
        long myLong = 127L;
        double myDouble = 127.0;
        float myFloat = ((float) (myDouble));
        Assert.assertEquals(127.0F, myFloat, 1.0E-5F);
        int myInt = ((int) (myLong));
        Assert.assertEquals(127, myInt);
        byte myByte = ((byte) (myInt));
        Assert.assertEquals(((byte) (127)), myByte);
    }

    @Test
    public void givenPrimitiveData_whenAssiginingToWrapper_thenAutomaticBoxingHappens() {
        int myInt = 127;
        Integer myIntegerReference = myInt;
        Assert.assertEquals(new Integer("127"), myIntegerReference);
    }

    @Test
    public void givenWrapperObjectData_whenAssiginingToPrimitive_thenAutomaticUnboxingHappens() {
        Integer myIntegerReference = new Integer("127");
        int myOtherInt = myIntegerReference;
        Assert.assertEquals(127, myOtherInt);
    }

    @Test
    public void givenByteValue_whenConvertingToChar_thenWidenAndNarrowTakesPlace() {
        byte myLargeValueByte = ((byte) (130));// 0b10000010

        PrimitiveConversionsJUnitTest.LOG.debug("{}", myLargeValueByte);// 0b10000010 -126

        Assert.assertEquals((-126), myLargeValueByte);
        int myLargeValueInt = myLargeValueByte;
        PrimitiveConversionsJUnitTest.LOG.debug("{}", myLargeValueInt);// 0b11111111 11111111 11111111 10000010 -126

        Assert.assertEquals((-126), myLargeValueInt);
        char myLargeValueChar = ((char) (myLargeValueByte));
        PrimitiveConversionsJUnitTest.LOG.debug("{}", myLargeValueChar);// 0b11111111 10000010 unsigned 0xFF82

        Assert.assertEquals(65410, myLargeValueChar);
        myLargeValueInt = myLargeValueChar;
        PrimitiveConversionsJUnitTest.LOG.debug("{}", myLargeValueInt);// 0b11111111 10000010  65410

        Assert.assertEquals(65410, myLargeValueInt);
        byte myOtherByte = ((byte) (myLargeValueInt));
        PrimitiveConversionsJUnitTest.LOG.debug("{}", myOtherByte);// 0b10000010 -126

        Assert.assertEquals((-126), myOtherByte);
        char myLargeValueChar2 = 130;// This is an int not a byte!

        PrimitiveConversionsJUnitTest.LOG.debug("{}", myLargeValueChar2);// 0b00000000 10000010 unsigned 0x0082

        Assert.assertEquals(130, myLargeValueChar2);
        int myLargeValueInt2 = myLargeValueChar2;
        PrimitiveConversionsJUnitTest.LOG.debug("{}", myLargeValueInt2);// 0b00000000 10000010  130

        Assert.assertEquals(130, myLargeValueInt2);
        byte myOtherByte2 = ((byte) (myLargeValueInt2));
        PrimitiveConversionsJUnitTest.LOG.debug("{}", myOtherByte2);// 0b10000010 -126

        Assert.assertEquals((-126), myOtherByte2);
    }

    @Test
    public void givenString_whenParsingWithWrappers_thenValuesAreReturned() {
        String myString = "127";
        byte myNewByte = Byte.parseByte(myString);
        Assert.assertEquals(((byte) (127)), myNewByte);
        short myNewShort = Short.parseShort(myString);
        Assert.assertEquals(((short) (127)), myNewShort);
        int myNewInt = Integer.parseInt(myString);
        Assert.assertEquals(127, myNewInt);
        long myNewLong = Long.parseLong(myString);
        Assert.assertEquals(127L, myNewLong);
        float myNewFloat = Float.parseFloat(myString);
        Assert.assertEquals(127.0F, myNewFloat, 1.0E-5F);
        double myNewDouble = Double.parseDouble(myString);
        Assert.assertEquals(127.0, myNewDouble, 1.0E-5F);
        boolean myNewBoolean = Boolean.parseBoolean(myString);
        Assert.assertEquals(false, myNewBoolean);// numbers are not true!

        char myNewChar = myString.charAt(0);
        Assert.assertEquals(49, myNewChar);// the value of '1'

    }
}

