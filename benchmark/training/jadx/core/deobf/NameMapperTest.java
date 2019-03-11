package jadx.core.deobf;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class NameMapperTest {
    @Test
    public void validIdentifiers() {
        Assert.assertThat(NameMapper.isValidIdentifier("ACls"), Matchers.is(true));
    }

    @Test
    public void notValidIdentifiers() {
        Assert.assertThat(NameMapper.isValidIdentifier("1cls"), Matchers.is(false));
        Assert.assertThat(NameMapper.isValidIdentifier("-cls"), Matchers.is(false));
        Assert.assertThat(NameMapper.isValidIdentifier("A-cls"), Matchers.is(false));
    }

    @Test
    public void testRemoveInvalidCharsMiddle() {
        Assert.assertThat(NameMapper.removeInvalidCharsMiddle("1cls"), Matchers.is("1cls"));
        Assert.assertThat(NameMapper.removeInvalidCharsMiddle("-cls"), Matchers.is("cls"));
        Assert.assertThat(NameMapper.removeInvalidCharsMiddle("A-cls"), Matchers.is("Acls"));
    }

    @Test
    public void testRemoveInvalidChars() {
        Assert.assertThat(NameMapper.removeInvalidChars("1cls", "C"), Matchers.is("C1cls"));
        Assert.assertThat(NameMapper.removeInvalidChars("-cls", "C"), Matchers.is("cls"));
        Assert.assertThat(NameMapper.removeInvalidChars("A-cls", "C"), Matchers.is("Acls"));
    }
}

