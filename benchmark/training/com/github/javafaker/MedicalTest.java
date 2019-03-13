package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class MedicalTest extends AbstractFakerTest {
    @Test
    public void testMedicineName() {
        Assert.assertThat(faker.medical().medicineName(), MatchesRegularExpression.matchesRegularExpression("([\\w\']+\\.?( )?){2,5}"));
    }

    @Test
    public void testDiseaseName() {
        Assert.assertThat(faker.medical().diseaseName(), MatchesRegularExpression.matchesRegularExpression("([\\w\']+\\.?( )?){2,8}"));
    }

    @Test
    public void testHospitalName() {
        Assert.assertThat(faker.medical().hospitalName(), MatchesRegularExpression.matchesRegularExpression("[A-Z ,./&'()]+"));
    }

    @Test
    public void testSymptom() {
        Assert.assertThat(faker.medical().symptoms(), MatchesRegularExpression.matchesRegularExpression("[\\w\'\\s\\(\\)]+"));
    }
}

