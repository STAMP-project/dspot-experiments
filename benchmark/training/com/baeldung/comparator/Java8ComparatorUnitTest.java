package com.baeldung.comparator;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class Java8ComparatorUnitTest {
    List<Player> footballTeam;

    @Test
    public void whenComparing_UsingLambda_thenSorted() {
        System.out.println("************** Java 8 Comaparator **************");
        Comparator<Player> byRanking = (Player player1,Player player2) -> (player1.getRanking()) - (player2.getRanking());
        System.out.println(("Before Sorting : " + (footballTeam)));
        Collections.sort(footballTeam, byRanking);
        System.out.println(("After Sorting : " + (footballTeam)));
        Assert.assertEquals(footballTeam.get(0).getName(), "Steven");
        Assert.assertEquals(footballTeam.get(2).getRanking(), 67);
    }

    @Test
    public void whenComparing_UsingComparatorComparing_thenSorted() {
        System.out.println("********* Comaparator.comparing method *********");
        System.out.println("********* byRanking *********");
        Comparator<Player> byRanking = Comparator.comparing(Player::getRanking);
        System.out.println(("Before Sorting : " + (footballTeam)));
        Collections.sort(footballTeam, byRanking);
        System.out.println(("After Sorting : " + (footballTeam)));
        Assert.assertEquals(footballTeam.get(0).getName(), "Steven");
        Assert.assertEquals(footballTeam.get(2).getRanking(), 67);
        System.out.println("********* byAge *********");
        Comparator<Player> byAge = Comparator.comparing(Player::getAge);
        System.out.println(("Before Sorting : " + (footballTeam)));
        Collections.sort(footballTeam, byAge);
        System.out.println(("After Sorting : " + (footballTeam)));
        Assert.assertEquals(footballTeam.get(0).getName(), "Roger");
        Assert.assertEquals(footballTeam.get(2).getRanking(), 45);
    }
}

