package io.dropwizard.jdbi.args;


import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skife.jdbi.v2.StatementContext;


public class OffsetDateTimeMapperTest {
    private final ResultSet resultSet = Mockito.mock(ResultSet.class);

    private final StatementContext ctx = Mockito.mock(StatementContext.class);

    @Test
    public void mapColumnByName() throws Exception {
        final Instant now = OffsetDateTime.now().toInstant();
        Mockito.when(resultSet.getTimestamp("name")).thenReturn(Timestamp.from(now));
        OffsetDateTime actual = new OffsetDateTimeMapper().mapColumn(resultSet, "name", ctx);
        assertThat(actual).isEqualTo(OffsetDateTime.ofInstant(now, ZoneId.systemDefault()));
    }

    @Test
    public void mapColumnByName_TimestampIsNull() throws Exception {
        Mockito.when(resultSet.getTimestamp("name")).thenReturn(null);
        OffsetDateTime actual = new OffsetDateTimeMapper().mapColumn(resultSet, "name", ctx);
        assertThat(actual).isNull();
    }

    @Test
    public void mapColumnByIndex() throws Exception {
        final Instant now = OffsetDateTime.now().toInstant();
        Mockito.when(resultSet.getTimestamp(1)).thenReturn(Timestamp.from(now));
        OffsetDateTime actual = new OffsetDateTimeMapper().mapColumn(resultSet, 1, ctx);
        assertThat(actual).isEqualTo(OffsetDateTime.ofInstant(now, ZoneId.systemDefault()));
    }

    @Test
    public void mapColumnByIndex_TimestampIsNull() throws Exception {
        Mockito.when(resultSet.getTimestamp(1)).thenReturn(null);
        OffsetDateTime actual = new OffsetDateTimeMapper().mapColumn(resultSet, 1, ctx);
        assertThat(actual).isNull();
    }
}

