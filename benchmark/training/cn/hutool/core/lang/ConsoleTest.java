package cn.hutool.core.lang;


import org.junit.Test;


/**
 * ???????
 *
 * @author Looly
 */
public class ConsoleTest {
    @Test
    public void logTest() {
        Console.log();
        String[] a = new String[]{ "abc", "bcd", "def" };
        Console.log(a);
        Console.log("This is Console log for {}.", "test");
    }

    @Test
    public void printTest() {
        String[] a = new String[]{ "abc", "bcd", "def" };
        Console.print(a);
        Console.log("This is Console print for {}.", "test");
    }

    @Test
    public void errorTest() {
        Console.error();
        String[] a = new String[]{ "abc", "bcd", "def" };
        Console.error(a);
        Console.error("This is Console error for {}.", "test");
    }
}

