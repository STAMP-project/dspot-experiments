package sagan.team;


import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class MemberProfileTests {
    @Test
    public void fullNameUsesNameIfAvailable() {
        MemberProfile nick = new MemberProfile();
        nick.setUsername("nickstreet");
        nick.setName("Nick Street");
        MatcherAssert.assertThat(nick.getFullName(), equalTo("Nick Street"));
    }

    @Test
    public void fullNameFallsBackToUsername() {
        MemberProfile nick = new MemberProfile();
        nick.setUsername("nickstreet");
        nick.setName(null);
        MatcherAssert.assertThat(nick.getFullName(), equalTo("nickstreet"));
    }

    @Test
    public void twitterLink() {
        MemberProfile nick = new MemberProfile();
        nick.setTwitterUsername("nickstreet");
        MatcherAssert.assertThat(nick.getTwitterLink().getHref(), equalTo("http://twitter.com/nickstreet"));
        MatcherAssert.assertThat(nick.getTwitterLink().getText(), equalTo("@nickstreet"));
    }

    @Test
    public void nullTwitterLink() {
        MemberProfile nick = new MemberProfile();
        MatcherAssert.assertThat(nick.getTwitterLink(), is(nullValue()));
    }

    @Test
    public void emptyTwitterLink() {
        MemberProfile nick = new MemberProfile();
        nick.setTwitterUsername("");
        MatcherAssert.assertThat(nick.getTwitterLink(), is(nullValue()));
    }

    @Test
    public void speakerdeckLink() {
        MemberProfile nick = new MemberProfile();
        nick.setSpeakerdeckUsername("nickstreet");
        MatcherAssert.assertThat(nick.getSpeakerdeckLink().getHref(), equalTo("https://speakerdeck.com/nickstreet"));
        MatcherAssert.assertThat(nick.getSpeakerdeckLink().getText(), equalTo("speakerdeck.com/nickstreet"));
    }

    @Test
    public void emptySpeakerdeckLink() {
        MemberProfile nick = new MemberProfile();
        MatcherAssert.assertThat(nick.getSpeakerdeckLink(), is(nullValue()));
    }

    @Test
    public void nullSpeakerdeckLink() {
        MemberProfile nick = new MemberProfile();
        nick.setSpeakerdeckUsername("");
        MatcherAssert.assertThat(nick.getSpeakerdeckLink(), is(nullValue()));
    }

    @Test
    public void githubLink() {
        MemberProfile nick = new MemberProfile();
        nick.setGithubUsername("nickstreet");
        MatcherAssert.assertThat(nick.getGithubLink().getHref(), equalTo("https://github.com/nickstreet"));
        MatcherAssert.assertThat(nick.getGithubLink().getText(), equalTo("github.com/nickstreet"));
    }

    @Test
    public void emptyGithubLink() {
        MemberProfile nick = new MemberProfile();
        MatcherAssert.assertThat(nick.getGithubLink(), is(nullValue()));
    }

    @Test
    public void nullGithubLink() {
        MemberProfile nick = new MemberProfile();
        nick.setGithubUsername("");
        MatcherAssert.assertThat(nick.getGithubLink(), is(nullValue()));
    }

    @Test
    public void lanyrdLink() {
        MemberProfile nick = new MemberProfile();
        nick.setLanyrdUsername("nickstreet");
        MatcherAssert.assertThat(nick.getLanyrdLink().getHref(), equalTo("https://lanyrd.com/profile/nickstreet"));
        MatcherAssert.assertThat(nick.getLanyrdLink().getText(), equalTo("lanyrd.com/profile/nickstreet"));
    }

    @Test
    public void emptyLanyrdLink() {
        MemberProfile nick = new MemberProfile();
        MatcherAssert.assertThat(nick.getLanyrdLink(), is(nullValue()));
    }

    @Test
    public void nullLanyrdLink() {
        MemberProfile nick = new MemberProfile();
        nick.setLanyrdUsername("");
        MatcherAssert.assertThat(nick.getLanyrdLink(), is(nullValue()));
    }

    @Test
    public void isNotHiddenByDefault() {
        MemberProfile nick = new MemberProfile();
        MatcherAssert.assertThat(nick.isHidden(), is(false));
    }
}

