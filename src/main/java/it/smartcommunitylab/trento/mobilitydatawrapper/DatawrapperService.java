package it.smartcommunitylab.trento.mobilitydatawrapper;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.trento.mobilitydatawrapper.model.BY_TYPE;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.Parking;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.Position;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.SOURCE_TYPE;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.Traffic;

@Component
public class DatawrapperService {

    private static final Logger logger = LoggerFactory.getLogger(DatawrapperService.class);

    @Autowired
    GeoUtils geoUtils;

//    @Autowired
//    private DBConnector db;

    @Autowired
    private JDBCConnector jdb;

    public List<Parking> getParkings() throws Exception {
        logger.debug("getParkings");
//      List<Object[]> result = jdb.trentoParkingsProcedure();
        List<Object[]> result = jdb.getParkingsData();
        logger.debug("getParkings count: " + String.valueOf(result.size()));

        return result.stream()
                .map(x -> new Parking(x))
                .collect(Collectors.toList());

//        // filter results within a 1hour window
//        Instant i = Instant.now().minus(1, ChronoUnit.HOURS);
//
//        return result.stream()
//                .map(x -> new Parking(x))
//                .filter(x -> (x.getUpdated() > i.toEpochMilli()))
//                .collect(Collectors.toList());
//
//        //overwrite data with stale readings
//        return result.stream()
//                .map(x -> new Parking(x))
//                .map(x -> (x.getUpdated() > i.toEpochMilli() ? x : x.reset()))
//                .collect(Collectors.toList());
    }

    public List<Traffic> getTraffic(SOURCE_TYPE source, BY_TYPE by,
            long from, long to) throws Exception {
        SOURCE_TYPE dbSource = getDbSource(source);
        logger.debug("getTraffic for " + source.name() + " by " + by.name() + " reading from " + dbSource.name());

//        List<Object[]> result = jdb.trentoTrafficProcedure(dbSource, by, from, to);
        List<Object[]> result = jdb.getTrafficData(dbSource, by, from, to);
        logger.debug("getTraffic for " + source.name() + " by " + by.name() + " count: " + String.valueOf(result.size()));

        List<Traffic> traffic = new ArrayList<Traffic>();

        if (SOURCE_TYPE.NarxBikes.equals(source)) {
            // parse bikes [field 6] as value for traffic
            traffic = result.stream().map(x -> new Traffic(
                    (Timestamp) x[0],
                    (Clob) x[1],
                    (Clob) x[2],
                    (Integer) x[5])).sorted().collect(Collectors.toList());
        } else {
            // direct map first 4 objects to traffic
            traffic = result.stream().map(x -> new Traffic(x)).sorted().collect(Collectors.toList());
        }
        
        logger.debug("getTraffic for " + source.name() + " by " + by.name() + " result: " + String.valueOf(traffic.size()));
        return traffic;
    }

    public List<Position> getTrafficPositions(SOURCE_TYPE source) throws Exception {
        SOURCE_TYPE dbSource = getDbSource(source);
        logger.debug("getTrafficPositions for " + source.name() + " reading from " + dbSource.name());

//        List<Object[]> result = jdb.trentoPositionProcedure(dbSource);
        List<Object[]> result = jdb.getTrafficPositions(dbSource);
        return result.stream().map(
                x -> new Position(
                        (String) x[0],
                        geoUtils.convert(((BigDecimal) x[1]).doubleValue(), ((BigDecimal) x[2]).doubleValue())))
                .collect(Collectors.toList());
    }

    /*
     * Helpers
     */
    private SOURCE_TYPE getDbSource(SOURCE_TYPE source) {
        if (SOURCE_TYPE.NarxBikes.equals(source)) {
            return SOURCE_TYPE.Narx;
        } else if (SOURCE_TYPE.NarxPedestrians.equals(source)) {
            return SOURCE_TYPE.Narx;
        } else {
            return source;
        }
    }
}
