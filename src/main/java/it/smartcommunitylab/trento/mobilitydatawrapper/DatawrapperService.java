package it.smartcommunitylab.trento.mobilitydatawrapper;

import java.sql.Clob;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.trento.mobilitydatawrapper.model.BY_TYPE;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.Parking;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.Position;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.SOURCE_TYPE;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.Traffic;

@Component
public class DatawrapperService {

    @Autowired
    private DBConnector db;

    @Autowired
    private JDBCConnector jdb;

    public List<Parking> getParkings() throws Exception {
        return db.getParkings();
    }

    public List<Traffic> getTraffic(SOURCE_TYPE source, BY_TYPE by,
            long from, long to) throws Exception {
        SOURCE_TYPE dbSource = getDbSource(source);
//        List<Object[]> result = jdb.trentoTrafficProcedure(dbSource, by, from, to);
        List<Object[]> result = jdb.getTrafficData(dbSource, by, from, to);

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

        return traffic;
    }

    public List<Position> getTrafficPositions(SOURCE_TYPE source) throws Exception {
        SOURCE_TYPE dbSource = getDbSource(source);
//        List<Object[]> result = jdb.trentoPositionProcedure(dbSource);
        List<Object[]> result = jdb.getTrafficPositions(dbSource);
        return result.stream().map(x -> new Position(x)).collect(Collectors.toList());
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
