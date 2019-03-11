

import com.seriouscompany.business.java.fizzbuzz.packagenamingpackage.interfaces.FizzBuzz;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.Test;

import static TestConstants.INT_1;
import static TestConstants.INT_10;
import static TestConstants.INT_11;
import static TestConstants.INT_12;
import static TestConstants.INT_13;
import static TestConstants.INT_14;
import static TestConstants.INT_15;
import static TestConstants.INT_16;
import static TestConstants.INT_2;
import static TestConstants.INT_3;
import static TestConstants.INT_4;
import static TestConstants.INT_5;
import static TestConstants.INT_6;
import static TestConstants.INT_7;
import static TestConstants.INT_8;
import static TestConstants.INT_9;
import static TestConstants._1_;
import static TestConstants._1_2_;
import static TestConstants._1_2_FIZZ;
import static TestConstants._1_2_FIZZ_4;
import static TestConstants._1_2_FIZZ_4_BUZZ;
import static TestConstants._1_2_FIZZ_4_BUZZ_FIZZ;
import static TestConstants._1_2_FIZZ_4_BUZZ_FIZZ_7;
import static TestConstants._1_2_FIZZ_4_BUZZ_FIZZ_7_8;
import static TestConstants._1_2_FIZZ_4_BUZZ_FIZZ_7_8_FIZZ;
import static TestConstants._1_2_FIZZ_4_BUZZ_FIZZ_7_8_FIZZ_BUZZ;
import static TestConstants._1_2_FIZZ_4_BUZZ_FIZZ_7_8_FIZZ_BUZZ_11;
import static TestConstants._1_2_FIZZ_4_BUZZ_FIZZ_7_8_FIZZ_BUZZ_11_FIZZ;
import static TestConstants._1_2_FIZZ_4_BUZZ_FIZZ_7_8_FIZZ_BUZZ_11_FIZZ_13;
import static TestConstants._1_2_FIZZ_4_BUZZ_FIZZ_7_8_FIZZ_BUZZ_11_FIZZ_13_14;
import static TestConstants._1_2_FIZZ_4_BUZZ_FIZZ_7_8_FIZZ_BUZZ_11_FIZZ_13_14_FIZZ_BUZZ;
import static TestConstants._1_2_FIZZ_4_BUZZ_FIZZ_7_8_FIZZ_BUZZ_11_FIZZ_13_14_FIZZ_BUZZ_16;


public class FizzBuzzTest {
    private PrintStream out;

    private FizzBuzz fb;

    @Test
    public void testFizzBuzz() throws IOException {
        this.doFizzBuzz(INT_1, _1_);
        this.doFizzBuzz(INT_2, _1_2_);
        this.doFizzBuzz(INT_3, _1_2_FIZZ);
        this.doFizzBuzz(INT_4, _1_2_FIZZ_4);
        this.doFizzBuzz(INT_5, _1_2_FIZZ_4_BUZZ);
        this.doFizzBuzz(INT_6, _1_2_FIZZ_4_BUZZ_FIZZ);
        this.doFizzBuzz(INT_7, _1_2_FIZZ_4_BUZZ_FIZZ_7);
        this.doFizzBuzz(INT_8, _1_2_FIZZ_4_BUZZ_FIZZ_7_8);
        this.doFizzBuzz(INT_9, _1_2_FIZZ_4_BUZZ_FIZZ_7_8_FIZZ);
        this.doFizzBuzz(INT_10, _1_2_FIZZ_4_BUZZ_FIZZ_7_8_FIZZ_BUZZ);
        this.doFizzBuzz(INT_11, _1_2_FIZZ_4_BUZZ_FIZZ_7_8_FIZZ_BUZZ_11);
        this.doFizzBuzz(INT_12, _1_2_FIZZ_4_BUZZ_FIZZ_7_8_FIZZ_BUZZ_11_FIZZ);
        this.doFizzBuzz(INT_13, _1_2_FIZZ_4_BUZZ_FIZZ_7_8_FIZZ_BUZZ_11_FIZZ_13);
        this.doFizzBuzz(INT_14, _1_2_FIZZ_4_BUZZ_FIZZ_7_8_FIZZ_BUZZ_11_FIZZ_13_14);
        this.doFizzBuzz(INT_15, _1_2_FIZZ_4_BUZZ_FIZZ_7_8_FIZZ_BUZZ_11_FIZZ_13_14_FIZZ_BUZZ);
        this.doFizzBuzz(INT_16, _1_2_FIZZ_4_BUZZ_FIZZ_7_8_FIZZ_BUZZ_11_FIZZ_13_14_FIZZ_BUZZ_16);
    }
}

