package io.firebreathingduck.ducktracker.persist;

import java.util.Map;

import java.util.List;

/**
 * I'm going to defy standard Java convention and have my objects that map to database tables
 * as immutable maps.
 *
 * That's mostly because this is a toy project. But I might be tempted to do the same with a real
 * Java application.  The Clojure community has taught me on the value of immutable data structures.
 *
 */
public interface DTPersister {

    List<Map<String, Object>> getAllDucks();

    Map<String, Object> saveDuck(Map<String, Object> duckData);

    Map<String, Object> getDuck(int id);

    List<Map<String, Object>> getAllPonds();

    Map<String, Object> savePond(Map<String, Object> pondData);

    Map<String, Object> getPond(int id);

    List<Map<String, Object>> getAllDuckTravelViews();

    Map<String, Object> saveDuckTravel(Map<String, Object> duckTravelData);

    List<Map<String, Object>> getDuckTravelViewByDuck(Integer id);

    List<Map<String, Object>> getDuckTravelViewByPond(Integer id);

}
