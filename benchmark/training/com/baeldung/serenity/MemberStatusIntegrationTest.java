package com.baeldung.serenity;


import com.baeldung.serenity.membership.Commodity;
import com.baeldung.serenity.membership.MemberGrade;
import com.baeldung.serenity.membership.MemberStatusSteps;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Title;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(SerenityRunner.class)
public class MemberStatusIntegrationTest {
    @Steps
    private MemberStatusSteps memberSteps;

    @Test
    public void membersShouldStartWithBronzeStatus() {
        memberSteps.aClientJoinsTheMemberProgram();
        memberSteps.theMemberShouldHaveAStatusOf(MemberGrade.Bronze);
    }

    @Test
    @Title("Members earn Silver grade after 1000 points ($10,000)")
    public void earnsSilverAfterSpends$10000() {
        memberSteps.aClientJoinsTheMemberProgram();
        memberSteps.theMemberSpends(10000);
        memberSteps.theMemberShouldHaveAStatusOf(MemberGrade.Silver);
    }

    @Test
    @Title("Members with 2,000 points should earn Gold grade when added 3,000 points ($30,000)")
    public void memberWith2000PointsEarnsGoldAfterSpends$30000() {
        memberSteps.aMemberHasPointsOf(2000);
        memberSteps.theMemberSpends(30000);
        memberSteps.theMemberShouldHaveAStatusOf(MemberGrade.Gold);
    }

    @Test
    @Title("Members with 50,000 points can exchange a MacBook Pro")
    public void memberWith50000PointsCanExchangeAMacbookpro() {
        memberSteps.aMemberHasPointsOf(50000);
        memberSteps.aMemberExchangeA(Commodity.MacBookPro);
        memberSteps.memberShouldHavePointsLeft();
    }
}

