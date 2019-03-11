package com.github.javafaker.idnumbers;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class SwedishIdNumberTest {
    @Test
    public void valid() {
        SvSEIdNumber idNumber = new SvSEIdNumber();
        MatcherAssert.assertThat(idNumber.validSwedishSsn("670919-9530"), Is.is(true));
        MatcherAssert.assertThat(idNumber.validSwedishSsn("811228-9874"), Is.is(true));
    }

    @Test
    public void invalid() {
        SvSEIdNumber idNumber = new SvSEIdNumber();
        MatcherAssert.assertThat(idNumber.validSwedishSsn("8112289873"), Is.is(false));
        MatcherAssert.assertThat(idNumber.validSwedishSsn("foo228-9873"), Is.is(false));
        MatcherAssert.assertThat(idNumber.validSwedishSsn("811228-9873"), Is.is(false));
        MatcherAssert.assertThat(idNumber.validSwedishSsn("811228-9875"), Is.is(false));
        MatcherAssert.assertThat(idNumber.validSwedishSsn("811200-9874"), Is.is(false));
        MatcherAssert.assertThat(idNumber.validSwedishSsn("810028-9874"), Is.is(false));
    }
}

