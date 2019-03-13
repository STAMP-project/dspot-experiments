package com.fishercoder;


import _721.Solution1;
import _721.Solution2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


public class _721Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static List<List<String>> accounts;

    private static List<List<String>> expected;

    @Test
    public void test1() throws Exception {
        _721Test.accounts = new ArrayList<>();
        List<String> account1 = new ArrayList<>(Arrays.asList("John", "johnsmith@mail.com", "john00@mail.com"));
        List<String> account2 = new ArrayList<>(Arrays.asList("John", "johnnybravo@mail.com"));
        List<String> account3 = new ArrayList<>(Arrays.asList("John", "johnsmith@mail.com", "john_newyork@mail.com"));
        List<String> account4 = new ArrayList<>(Arrays.asList("Mary", "mary@mail.com"));
        _721Test.accounts.add(account1);
        _721Test.accounts.add(account2);
        _721Test.accounts.add(account3);
        _721Test.accounts.add(account4);
        _721Test.expected = new ArrayList<>();
        List<String> expected1 = new ArrayList<>(Arrays.asList("Mary", "mary@mail.com"));
        List<String> expected2 = new ArrayList<>(Arrays.asList("John", "john00@mail.com", "john_newyork@mail.com", "johnsmith@mail.com"));
        List<String> expected3 = new ArrayList<>(Arrays.asList("John", "johnnybravo@mail.com"));
        _721Test.expected.add(expected1);
        _721Test.expected.add(expected2);
        _721Test.expected.add(expected3);
        assertEqualsIgnoreOrdering(_721Test.expected, _721Test.solution1.accountsMerge(_721Test.accounts));
        assertEqualsIgnoreOrdering(_721Test.expected, _721Test.solution2.accountsMerge(_721Test.accounts));
    }

    @Test
    public void test2() throws Exception {
        _721Test.accounts = new ArrayList<>();
        List<String> account1 = new ArrayList<>(Arrays.asList("Alex", "Alex5@m.co", "Alex4@m.co", "Alex0@m.co"));
        List<String> account2 = new ArrayList<>(Arrays.asList("Ethan", "Ethan3@m.co", "Ethan3@m.co", "Ethan0@m.co"));
        List<String> account3 = new ArrayList<>(Arrays.asList("Kevin", "Kevin4@m.co", "Kevin2@m.co", "Kevin2@m.co"));
        List<String> account4 = new ArrayList<>(Arrays.asList("Gabe", "Gabe0@m.co", "Gabe3@m.co", "Gabe2@m.co"));
        List<String> account5 = new ArrayList<>(Arrays.asList("Gabe", "Gabe3@m.co", "Gabe4@m.co", "Gabe2@m.co"));
        _721Test.accounts.add(account1);
        _721Test.accounts.add(account2);
        _721Test.accounts.add(account3);
        _721Test.accounts.add(account4);
        _721Test.accounts.add(account5);
        _721Test.expected = new ArrayList<>();
        List<String> expected1 = new ArrayList<>(Arrays.asList("Alex", "Alex0@m.co", "Alex4@m.co", "Alex5@m.co"));
        List<String> expected2 = new ArrayList<>(Arrays.asList("Kevin", "Kevin2@m.co", "Kevin4@m.co"));
        List<String> expected3 = new ArrayList<>(Arrays.asList("Ethan", "Ethan0@m.co", "Ethan3@m.co"));
        List<String> expected4 = new ArrayList<>(Arrays.asList("Gabe", "Gabe0@m.co", "Gabe2@m.co", "Gabe3@m.co", "Gabe4@m.co"));
        _721Test.expected.add(expected1);
        _721Test.expected.add(expected2);
        _721Test.expected.add(expected3);
        _721Test.expected.add(expected4);
        assertEqualsIgnoreOrdering(_721Test.expected, _721Test.solution1.accountsMerge(_721Test.accounts));
        assertEqualsIgnoreOrdering(_721Test.expected, _721Test.solution2.accountsMerge(_721Test.accounts));
    }

    @Test
    public void test3() throws Exception {
        _721Test.accounts = new ArrayList<>();
        List<String> account1 = new ArrayList<>(Arrays.asList("David", "David0@m.co", "David1@m.co"));
        List<String> account2 = new ArrayList<>(Arrays.asList("David", "David3@m.co", "David4@m.co"));
        List<String> account3 = new ArrayList<>(Arrays.asList("David", "David4@m.co", "David5@m.co"));
        List<String> account4 = new ArrayList<>(Arrays.asList("David", "David2@m.co", "David3@m.co"));
        List<String> account5 = new ArrayList<>(Arrays.asList("David", "David1@m.co", "David2@m.co"));
        _721Test.accounts.add(account1);
        _721Test.accounts.add(account2);
        _721Test.accounts.add(account3);
        _721Test.accounts.add(account4);
        _721Test.accounts.add(account5);
        _721Test.expected = new ArrayList<>();
        List<String> expected1 = new ArrayList<>(Arrays.asList("David", "David0@m.co", "David1@m.co", "David2@m.co", "David3@m.co", "David4@m.co", "David5@m.co"));
        _721Test.expected.add(expected1);
        assertEqualsIgnoreOrdering(_721Test.expected, _721Test.solution1.accountsMerge(_721Test.accounts));
        assertEqualsIgnoreOrdering(_721Test.expected, _721Test.solution2.accountsMerge(_721Test.accounts));
    }
}

