package io.firebreathingduck.ducktracker.persist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import io.firebreathingduck.ducktracker.domain.FieldNames;

/**
 * I haven't used Mockito before, colleagues have used it.  I thought I would
 * take it for a spin here.
 * 
 * If this was a production app there would be a lot more tests, and I would use
 * an in-memory database for the testing.  I might still use Mockito in some cases.
 */

@SpringBootTest
public class PostgresDTPersisterTest {

    @Mock
    JdbcTemplate jdbcTemplate;

    @Test
    public void checkLoadDucks() {
        Mockito.when(jdbcTemplate.queryForList("select * from duck order by id "))
            .thenReturn(List.of(
                Map.of(FieldNames.DUCK_ID, 1, FieldNames.DUCK_NAME, "Daffy", FieldNames.DUCK_TAGGED, new Date()),
                Map.of(FieldNames.DUCK_ID, 2, FieldNames.DUCK_NAME, "Donald", FieldNames.DUCK_TAGGED,
                    new Date(new Date().getTime() - 60 * 1000L))));

        DTPersister dtPersister = new PostgresDTPersister(jdbcTemplate);
        List<Map<String, Object>> ducks = dtPersister.getAllDucks();
        assertEquals(2, ducks.size());
        assertEquals(1, ducks.get(0).get(FieldNames.DUCK_ID));
        assertEquals("Daffy", ducks.get(0).get(FieldNames.DUCK_NAME));
        assertEquals(2, ducks.get(1).get(FieldNames.DUCK_ID));
        assertEquals("Donald", ducks.get(1).get(FieldNames.DUCK_NAME));
        assertTrue(ducks.get(0).get(FieldNames.DUCK_TAGGED) instanceof Date &&
            ducks.get(1).get(FieldNames.DUCK_TAGGED) instanceof Date);
    }

    @Test
    public void checkAddDuck() {
        try {
            DataSource dataSource = Mockito.mock(DataSource.class);
            Mockito.when(jdbcTemplate.getDataSource())
                .thenReturn(dataSource);
            Connection conn = Mockito.mock(Connection.class);
            Mockito.when(dataSource.getConnection())
                .thenReturn(conn);
            PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
            Mockito.when(conn.prepareStatement("insert into duck (id, pond_name, tagged) values (nextval('duck_id_sequence'), ?, ?) "))
                .thenReturn(preparedStatement);
            Statement statement = Mockito.mock(Statement.class);
            Mockito.when(conn.createStatement())
                .thenReturn(statement);
            ResultSet rs = Mockito.mock(ResultSet.class);
            Mockito.when(statement.executeQuery("select currval('duck_id_sequence') "))
                .thenReturn(rs);
            Mockito.when(rs.getInt(1))
                .thenReturn(27);
            DTPersister dtPersister = new PostgresDTPersister(jdbcTemplate);
            Date now = new Date();
            Map<String, Object> insertedDuck = dtPersister.saveDuck(
                Map.of(FieldNames.DUCK_NAME, "Feathers", FieldNames.DUCK_TAGGED, now));
            assertNotNull(insertedDuck);
            assertEquals(3, insertedDuck.size());
            assertEquals(27, insertedDuck.get(FieldNames.DUCK_ID));
            assertEquals("Feathers", insertedDuck.get(FieldNames.DUCK_NAME));
            assertEquals(now, insertedDuck.get(FieldNames.DUCK_TAGGED));
        } catch (SQLException sqle) {
            throw new RuntimeException("SQLException during test.", sqle);   
        }
    }

}
