package com.baeldung.equalshashcode;


import java.util.HashMap;
import java.util.Map;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;


public class TeamUnitTest {
    @Test
    public void givenMapKeyWithHashCode_whenSearched_thenReturnsCorrectValue() {
        Map<Team, String> leaders = new HashMap<>();
        leaders.put(new Team("New York", "development"), "Anne");
        leaders.put(new Team("Boston", "development"), "Brian");
        leaders.put(new Team("Boston", "marketing"), "Charlie");
        Team myTeam = new Team("New York", "development");
        String myTeamleader = leaders.get(myTeam);
        Assert.assertEquals("Anne", myTeamleader);
    }

    @Test
    public void givenMapKeyWithoutHashCode_whenSearched_thenReturnsWrongValue() {
        Map<WrongTeam, String> leaders = new HashMap<>();
        leaders.put(new WrongTeam("New York", "development"), "Anne");
        leaders.put(new WrongTeam("Boston", "development"), "Brian");
        leaders.put(new WrongTeam("Boston", "marketing"), "Charlie");
        WrongTeam myTeam = new WrongTeam("New York", "development");
        String myTeamleader = leaders.get(myTeam);
        Assert.assertFalse("Anne".equals(myTeamleader));
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Team.class).verify();
    }
}

