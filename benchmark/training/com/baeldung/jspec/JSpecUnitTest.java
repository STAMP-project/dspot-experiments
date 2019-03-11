package com.baeldung.jspec;


import org.junit.Test;


public class JSpecUnitTest {
    @Test
    public void onePlusTwo_shouldEqualThree() {
        $((1 + 2)).shouldEqual(3);
        a((1 + 2)).shouldEqual(3);
        the((1 + 2)).shouldEqual(3);
        it((1 + 2)).shouldEqual(3);
    }

    @Test
    public void messageShouldContainJSpec() {
        String message = "Welcome to JSpec demo";
        // The message should not be empty
        the(message).shouldNotBe("empty");
        // The message should contain JSpec
        the(message).shouldContain("JSpec");
    }

    @Test
    public void dividingByThero_shouldThrowArithmeticException() {
        expect(new org.javalite.test.jspec.ExceptionExpectation<ArithmeticException>(ArithmeticException.class) {
            @Override
            public void exec() throws ArithmeticException {
                System.out.println((1 / 0));
            }
        });
    }
}

