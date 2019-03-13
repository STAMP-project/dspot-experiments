package com.vip.saturn.job.exception;


import SaturnJobException.ILLEGAL_ARGUMENT;
import org.junit.Test;

import static SaturnJobException.ILLEGAL_ARGUMENT;


/**
 *
 *
 * @author xiaopeng.he
 */
public class SaturnJobExceptionTest {
    @Test
    public void testConstant() {
        assertThat(ILLEGAL_ARGUMENT).isEqualTo(0);
        assertThat(SaturnJobException.JOB_NOT_FOUND).isEqualTo(1);
        assertThat(SaturnJobException.OUT_OF_ZK_LIMIT_MEMORY).isEqualTo(3);
        assertThat(SaturnJobException.JOB_NAME_INVALID).isEqualTo(4);
    }

    @Test
    public void testGet() {
        SaturnJobException saturnJobException = new SaturnJobException(ILLEGAL_ARGUMENT, "cron valid");
        assertThat(saturnJobException.getType()).isEqualTo(ILLEGAL_ARGUMENT);
        assertThat(saturnJobException.getMessage()).isEqualTo("cron valid");
    }
}

