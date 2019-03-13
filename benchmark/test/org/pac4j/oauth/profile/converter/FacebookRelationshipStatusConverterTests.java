package org.pac4j.oauth.profile.converter;


import FacebookRelationshipStatus.DIVORCED;
import FacebookRelationshipStatus.ENGAGED;
import FacebookRelationshipStatus.IN_AN_OPEN_RELATIONSHIP;
import FacebookRelationshipStatus.IN_A_CIVIL_UNION;
import FacebookRelationshipStatus.IN_A_DOMESTIC_PARTNERSHIP;
import FacebookRelationshipStatus.IN_A_RELATIONSHIP;
import FacebookRelationshipStatus.ITS_COMPLICATED;
import FacebookRelationshipStatus.MARRIED;
import FacebookRelationshipStatus.SEPARATED;
import FacebookRelationshipStatus.SINGLE;
import FacebookRelationshipStatus.WIDOWED;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.oauth.profile.facebook.converter.FacebookRelationshipStatusConverter;


/**
 * This class test the {@link org.pac4j.oauth.profile.facebook.converter.FacebookRelationshipStatusConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class FacebookRelationshipStatusConverterTests {
    private final FacebookRelationshipStatusConverter converter = new FacebookRelationshipStatusConverter();

    @Test
    public void testNull() {
        Assert.assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAString() {
        Assert.assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testSingle() {
        Assert.assertEquals(SINGLE, this.converter.convert("Single"));
    }

    @Test
    public void testInARelationship() {
        Assert.assertEquals(IN_A_RELATIONSHIP, this.converter.convert("In a relationship"));
    }

    @Test
    public void testEngaged() {
        Assert.assertEquals(ENGAGED, this.converter.convert("Engaged"));
    }

    @Test
    public void testMarried() {
        Assert.assertEquals(MARRIED, this.converter.convert("Married"));
    }

    @Test
    public void testItsComplicated() {
        Assert.assertEquals(ITS_COMPLICATED, this.converter.convert("It's complicated"));
    }

    @Test
    public void testInAnOpenRelationship() {
        Assert.assertEquals(IN_AN_OPEN_RELATIONSHIP, this.converter.convert("In an open relationship"));
    }

    @Test
    public void testWidowed() {
        Assert.assertEquals(WIDOWED, this.converter.convert("Widowed"));
    }

    @Test
    public void testSeparated() {
        Assert.assertEquals(SEPARATED, this.converter.convert("Separated"));
    }

    @Test
    public void testDivorced() {
        Assert.assertEquals(DIVORCED, this.converter.convert("Divorced"));
    }

    @Test
    public void testInACivilUnion() {
        Assert.assertEquals(IN_A_CIVIL_UNION, this.converter.convert("In a civil union"));
    }

    @Test
    public void testInADomesticPartnership() {
        Assert.assertEquals(IN_A_DOMESTIC_PARTNERSHIP, this.converter.convert("In a domestic partnership"));
    }

    @Test
    public void testSingleEnum() {
        Assert.assertEquals(SINGLE, this.converter.convert(SINGLE.toString()));
    }

    @Test
    public void testInARelationshipEnum() {
        Assert.assertEquals(IN_A_RELATIONSHIP, this.converter.convert(IN_A_RELATIONSHIP.toString()));
    }

    @Test
    public void testEngagedEnum() {
        Assert.assertEquals(ENGAGED, this.converter.convert(ENGAGED.toString()));
    }

    @Test
    public void testMarriedEnum() {
        Assert.assertEquals(MARRIED, this.converter.convert(MARRIED.toString()));
    }

    @Test
    public void testItsComplicatedEnum() {
        Assert.assertEquals(ITS_COMPLICATED, this.converter.convert(ITS_COMPLICATED.toString()));
    }

    @Test
    public void testInAnOpenRelationshipEnum() {
        Assert.assertEquals(IN_AN_OPEN_RELATIONSHIP, this.converter.convert(IN_AN_OPEN_RELATIONSHIP.toString()));
    }

    @Test
    public void testWidowedEnum() {
        Assert.assertEquals(WIDOWED, this.converter.convert(WIDOWED.toString()));
    }

    @Test
    public void testSeparatedEnum() {
        Assert.assertEquals(SEPARATED, this.converter.convert(SEPARATED.toString()));
    }

    @Test
    public void testDivorcedEnum() {
        Assert.assertEquals(DIVORCED, this.converter.convert(DIVORCED.toString()));
    }

    @Test
    public void testInACivilUnionEnum() {
        Assert.assertEquals(IN_A_CIVIL_UNION, this.converter.convert(IN_A_CIVIL_UNION.toString()));
    }

    @Test
    public void testInADomesticPartnershipEnum() {
        Assert.assertEquals(IN_A_DOMESTIC_PARTNERSHIP, this.converter.convert(IN_A_DOMESTIC_PARTNERSHIP.toString()));
    }
}

