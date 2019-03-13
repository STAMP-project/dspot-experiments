package com.baeldung.comparable;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ComparableUnitTest {
    @Test
    public void whenUsingComparable_thenSortedList() {
        List<Player> footballTeam = new ArrayList<Player>();
        Player player1 = new Player(59, "John", 20);
        Player player2 = new Player(67, "Roger", 22);
        Player player3 = new Player(45, "Steven", 24);
        footballTeam.add(player1);
        footballTeam.add(player2);
        footballTeam.add(player3);
        Collections.sort(footballTeam);
        Assert.assertEquals(footballTeam.get(0).getName(), "Steven");
        Assert.assertEquals(footballTeam.get(2).getRanking(), 67);
    }
}

