package com.baeldung.drools.service;


import com.baeldung.drools.model.Applicant;
import com.baeldung.drools.model.SuggestedRole;
import java.io.IOException;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;


public class ApplicantServiceIntegrationTest {
    private ApplicantService applicantService;

    @Test
    public void whenCriteriaMatching_ThenSuggestManagerRole() throws IOException {
        Applicant applicant = new Applicant("Davis", 37, 1600000.0, 11);
        SuggestedRole suggestedRole = new SuggestedRole();
        applicantService.suggestARoleForApplicant(applicant, suggestedRole);
        TestCase.assertEquals("Manager", suggestedRole.getRole());
    }

    @Test
    public void whenCriteriaMatching_ThenSuggestSeniorDeveloperRole() throws IOException {
        Applicant applicant = new Applicant("John", 37, 1200000.0, 8);
        SuggestedRole suggestedRole = new SuggestedRole();
        applicantService.suggestARoleForApplicant(applicant, suggestedRole);
        TestCase.assertEquals("Senior developer", suggestedRole.getRole());
    }

    @Test
    public void whenCriteriaMatching_ThenSuggestDeveloperRole() throws IOException {
        Applicant applicant = new Applicant("Davis", 37, 800000.0, 3);
        SuggestedRole suggestedRole = new SuggestedRole();
        applicantService.suggestARoleForApplicant(applicant, suggestedRole);
        TestCase.assertEquals("Developer", suggestedRole.getRole());
    }

    @Test
    public void whenCriteriaNotMatching_ThenNoRole() throws IOException {
        Applicant applicant = new Applicant("John", 37, 1200000.0, 5);
        SuggestedRole suggestedRole = new SuggestedRole();
        applicantService.suggestARoleForApplicant(applicant, suggestedRole);
        Assert.assertNull(suggestedRole.getRole());
    }
}

