package com.baeldung.comparator;


import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ComparatorUnitTest {
    List<Player> footballTeam;

    @Test
    public void whenUsingRankingComparator_thenSortedList() {
        PlayerRankingComparator playerComparator = new PlayerRankingComparator();
        Collections.sort(footballTeam, playerComparator);
        Assert.assertEquals(footballTeam.get(0).getName(), "Steven");
        Assert.assertEquals(footballTeam.get(2).getRanking(), 67);
    }

    @Test
    public void whenUsingAgeComparator_thenSortedList() {
        PlayerAgeComparator playerComparator = new PlayerAgeComparator();
        Collections.sort(footballTeam, playerComparator);
        Assert.assertEquals(footballTeam.get(0).getName(), "John");
        Assert.assertEquals(footballTeam.get(2).getRanking(), 45);
    }
}

