package io.dropwizard.jdbi.args;


import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skife.jdbi.v2.StatementContext;


public class InstantMapperTest {
    private final ResultSet resultSet = Mockito.mock(ResultSet.class);

    private final StatementContext ctx = Mockito.mock(StatementContext.class);

    @Test
    public void mapColumnByName() throws Exception {
        ZonedDateTime expected = ZonedDateTime.parse("2012-12-21T00:00:00.000Z");
        ZonedDateTime stored = expected.withZoneSameInstant(ZoneId.systemDefault());
        Mockito.when(resultSet.getTimestamp("instant")).thenReturn(Timestamp.from(stored.toInstant()));
        Instant actual = new InstantMapper().mapColumn(resultSet, "instant", ctx);
        assertThat(actual).isEqualTo(expected.toInstant());
    }

    @Test
    public void mapColumnByName_TimestampIsNull() throws Exception {
        Mockito.when(resultSet.getTimestamp("instant")).thenReturn(null);
        Instant actual = new InstantMapper().mapColumn(resultSet, "instant", ctx);
        assertThat(actual).isNull();
    }

    @Test
    public void mapColumnByIndex() throws Exception {
        ZonedDateTime expected = ZonedDateTime.parse("2012-12-21T00:00:00.000Z");
        ZonedDateTime stored = expected.withZoneSameInstant(ZoneId.systemDefault());
        Mockito.when(resultSet.getTimestamp(1)).thenReturn(Timestamp.from(stored.toInstant()));
        Instant actual = new InstantMapper().mapColumn(resultSet, 1, ctx);
        assertThat(actual).isEqualTo(expected.toInstant());
    }

    @Test
    public void mapColumnByIndex_TimestampIsNull() throws Exception {
        Mockito.when(resultSet.getTimestamp(1)).thenReturn(null);
        Instant actual = new InstantMapper().mapColumn(resultSet, 1, ctx);
        assertThat(actual).isNull();
    }
}

