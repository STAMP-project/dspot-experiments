package com.kickstarter.viewmodels;


import com.kickstarter.KSRobolectricTestCase;
import junit.framework.TestCase;
import org.junit.Test;


public class SignupDataTest extends KSRobolectricTestCase {
    @Test
    public void testSignupData_isValid() {
        TestCase.assertTrue(new SignupViewModel.ViewModel.SignupData("brando", "b@kickstarter.com", "danisawesome", true).isValid());
        TestCase.assertFalse(new SignupViewModel.ViewModel.SignupData("", "b@kickstarter.com", "danisawesome", true).isValid());
        TestCase.assertFalse(new SignupViewModel.ViewModel.SignupData("brando", "b@kickstarter", "danisawesome", true).isValid());
        TestCase.assertFalse(new SignupViewModel.ViewModel.SignupData("brando", "b@kickstarter.com", "dan", true).isValid());
        TestCase.assertTrue(new SignupViewModel.ViewModel.SignupData("brando", "b@kickstarter.com", "danisawesome", false).isValid());
        TestCase.assertFalse(new SignupViewModel.ViewModel.SignupData("", "b@kickstarter.com", "danisawesome", false).isValid());
        TestCase.assertFalse(new SignupViewModel.ViewModel.SignupData("brando", "b@kickstarter", "danisawesome", false).isValid());
        TestCase.assertFalse(new SignupViewModel.ViewModel.SignupData("brando", "b@kickstarter.com", "dan", false).isValid());
    }
}

