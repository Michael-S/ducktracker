package io.firebreathingduck.ducktracker.persist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.firebreathingduck.ducktracker.domain.FieldNames;


import org.springframework.jdbc.core.JdbcTemplate;

public class PostgresDTPersister implements DTPersister {

    private final JdbcTemplate jdbcTemplate;

    public PostgresDTPersister(JdbcTemplate jdbcTemplate) {
        if (jdbcTemplate == null) {
            throw new IllegalArgumentException("JdbcTemplate input is required.");
        }
        this.jdbcTemplate = jdbcTemplate;
    }

    protected List<Map<String, Object>> query(String query, Object... params) {
        if (params != null && params.length > 0) {
            return jdbcTemplate.queryForList(query, params);
        } else {
            return jdbcTemplate.queryForList(query);
        }
    }

    protected Map<String, Object> queryGetFirstResult(String query, Object... params) {
        List<Map<String, Object>> allResults = query(query, params);
        if (allResults != null && !allResults.isEmpty()) {
            return allResults.get(0);
        } else {
            return null;
        }
    }


    @Override
    public List<Map<String, Object>> getAllDucks() {
        return query("select * from duck order by id ");
    }

    @Override
    public Map<String, Object> saveDuck(Map<String, Object> duckData) {
        // can add validation later
        if (duckData.get(FieldNames.DUCK_ID) != null) {
            jdbcTemplate.update("update duck set duck_name = ?, tagged = ? where id = ?",
                duckData.get(FieldNames.DUCK_NAME), duckData.get(FieldNames.DUCK_TAGGED), duckData.get(FieldNames.DUCK_ID));
            // unmodified
            return duckData;
        }

        final String duckInsert = "insert into duck (id, pond_name, tagged) values (nextval('duck_id_sequence'), ?, ?) ";
        // Quick way to set up a transaction without going into battle with Spring.
        try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement insertPreparedStatement = conn.prepareStatement(duckInsert)) {
                insertPreparedStatement.setString(1, duckData.get(FieldNames.DUCK_NAME).toString());
                insertPreparedStatement.setDate(2, new java.sql.Date(((Date)duckData.get(FieldNames.DUCK_TAGGED)).getTime()));
                insertPreparedStatement.execute();
            }
            int insertedId = -1;
            try (Statement stat = conn.createStatement();
                ResultSet rs = stat.executeQuery("select currval('duck_id_sequence') ")) {
                rs.next();
                insertedId = rs.getInt(1);
            }
            conn.commit();
            return Map.of(FieldNames.DUCK_ID, insertedId,
                FieldNames.DUCK_NAME, duckData.get(FieldNames.DUCK_NAME),
                FieldNames.DUCK_TAGGED, duckData.get(FieldNames.DUCK_TAGGED));
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }        
    }

    @Override
    public Map<String, Object> getDuck(int id) {
        return queryGetFirstResult("select * from duck where id = ? order by id", id);
    }

    @Override
    public List<Map<String, Object>> getAllPonds() {
        return query("select * from pond order by id ");
    }

    @Override
    public Map<String, Object> savePond(Map<String, Object> pondData) {
        // can add validation later
        if (pondData.get(FieldNames.POND_ID) != null) {
            jdbcTemplate.update("update pond set pond_name = ?, pond_location = ? where id = ?",
                pondData.get(FieldNames.POND_NAME), pondData.get(FieldNames.POND_LOCATION), pondData.get(FieldNames.POND_ID));
            // unmodified
            return pondData;
        }

        final String pondInsert = "insert into pond (id, pond_name, pond_description) values (nextval('pond_id_sequence'), ?, ?) ";
        // Quick way to set up a transaction without going into battle with Spring.
        try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement insertPreparedStatement = conn.prepareStatement(pondInsert)) {
                insertPreparedStatement.setString(1, (String)pondData.get(FieldNames.POND_NAME));
                insertPreparedStatement.setString(2, (String)pondData.get(FieldNames.POND_LOCATION));
                insertPreparedStatement.execute();
            }
            int insertedId = -1;
            try (Statement stat = conn.createStatement();
                ResultSet rs = stat.executeQuery("select currval('pond_id_sequence') ")) {
                rs.next();
                insertedId = rs.getInt(1);
            }
            conn.commit();
            return Map.of(FieldNames.POND_ID, insertedId,
                FieldNames.POND_NAME, pondData.get(FieldNames.POND_NAME),
                FieldNames.POND_LOCATION, pondData.get(FieldNames.POND_LOCATION));
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

    @Override
    public Map<String, Object> getPond(int id) {
        return queryGetFirstResult("select * from pond where id = ? order by id", id);
    }

    @Override
    public List<Map<String, Object>> getAllDuckTravel() {
        return query("select * from duck_travel order by id ");
    }

    @Override
    public Map<String, Object> saveDuckTravel(Map<String, Object> duckTravelData) {
        // can add validation later
        if (duckTravelData.get(FieldNames.DUCK_TRAVEL_ID) != null) {
            jdbcTemplate.update("update duck_travel set duck_id = ?, pond_id = ?, arrival = ?, departure = ? where id = ?",
                duckTravelData.get(FieldNames.DUCK_TRAVEL_DUCK_ID), duckTravelData.get(FieldNames.DUCK_TRAVEL_POND_ID),
                duckTravelData.get(FieldNames.DUCK_TRAVEL_ARRIVAL), duckTravelData.get(FieldNames.DUCK_TRAVEL_DEPARTURE),
                duckTravelData.get(FieldNames.DUCK_TRAVEL_ID));
            // unmodified
            return duckTravelData;
        }

        final String duckTravelInsert = "insert into duck_travel (id, duck_id, pond_id, arrival, departure) values "
            + "(nextval('duck_travel_id_sequence'), ?, ?, ?, ?) ";
        // Quick way to set up a transaction without going into battle with Spring.
        try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement insertPreparedStatement = conn.prepareStatement(duckTravelInsert)) {
                insertPreparedStatement.setInt(1, (Integer)duckTravelData.get(FieldNames.DUCK_TRAVEL_DUCK_ID));
                insertPreparedStatement.setInt(2, (Integer)duckTravelData.get(FieldNames.DUCK_TRAVEL_POND_ID));
                insertPreparedStatement.setDate(3, new java.sql.Date(((Date)duckTravelData.get(FieldNames.DUCK_TRAVEL_ARRIVAL)).getTime()));
                Date departure = (Date)duckTravelData.get(FieldNames.DUCK_TRAVEL_DEPARTURE);
                if (departure != null) {
                    insertPreparedStatement.setDate(4, new java.sql.Date(departure.getTime()));
                } else {
                    insertPreparedStatement.setNull(4, Types.DATE);
                }
                insertPreparedStatement.execute();
            }
            int insertedId = -1;
            try (Statement stat = conn.createStatement();
                ResultSet rs = stat.executeQuery("select currval('duck_travel_id_sequence') ")) {
                rs.next();
                insertedId = rs.getInt(1);
            }
            conn.commit();
            return Map.of(FieldNames.DUCK_TRAVEL_ID, insertedId,
                FieldNames.DUCK_TRAVEL_DUCK_ID, duckTravelData.get(FieldNames.DUCK_TRAVEL_DUCK_ID),
                FieldNames.DUCK_TRAVEL_POND_ID, duckTravelData.get(FieldNames.DUCK_TRAVEL_POND_ID),
                FieldNames.DUCK_TRAVEL_ARRIVAL, duckTravelData.get(FieldNames.DUCK_TRAVEL_ARRIVAL),
                FieldNames.DUCK_TRAVEL_DEPARTURE, duckTravelData.get(FieldNames.DUCK_TRAVEL_DEPARTURE)
            );
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

    @Override
    public List<Map<String, Object>> getDuckTravelByDuck(int duckId) {
        return query("select * from duck_travel where duck_id = ? order by arrival ", duckId);
    }

    @Override
    public List<Map<String, Object>> getDuckTravelByPond(int pondId) {
        return query("select * from duck_travel where pond_id = ? order by arrival ", pondId);
    }


}
