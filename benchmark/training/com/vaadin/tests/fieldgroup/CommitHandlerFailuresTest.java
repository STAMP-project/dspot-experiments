package com.vaadin.tests.fieldgroup;


import org.junit.Test;


public class CommitHandlerFailuresTest extends BasicPersonFormTest {
    @Test
    public void testDefaults() {
        assertDefaults();
        assertBeanValuesUnchanged();
    }

    @Test
    public void testUpdatingWithoutCommit() {
        updateFields();
        assertBeanValuesUnchanged();
    }

    @Test
    public void testPreCommitFails() {
        updateFields();
        getPreCommitFailsCheckBox().click();
        assertCommitFails();
        assertBeanValuesUnchanged();
    }

    @Test
    public void testPostCommitFails() {
        updateFields();
        getPostCommitFailsCheckBox().click();
        assertCommitFails();
        assertBeanValuesUnchanged();
    }

    @Test
    public void testDiscard() {
        updateFields();
        assertDiscardResetsFields();
        assertBeanValuesUnchanged();
    }
}

