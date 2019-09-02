package it.smartcommunitylab.trento.mobilitydatawrapper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import it.smartcommunitylab.trento.mobilitydatawrapper.model.BY_TYPE;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.SOURCE_TYPE;

@Component
public class JDBCConnector {
    private static final Logger logger = LoggerFactory.getLogger(DBConnector.class);

//    private JdbcTemplate jdbcTemplate;
//    private SimpleJdbcCall trafficPositionProcedure_Narx;
//    private SimpleJdbcCall trafficPositionProcedure_Spot;
//    private SimpleJdbcCall trafficPositionProcedure_RDT;

    @Autowired
    @Qualifier("trafficDataSource")
    private DataSource trafficDataSource;

    private LoadingCache<SOURCE_TYPE, List<Object[]>> positions;

    @PostConstruct
    public void init() throws Exception {
//        logger.info("Define jdbcCalls for stored procedures");
//        trafficPositionProcedure_Narx = new SimpleJdbcCall(trafficDataSource)
//                .withProcedureName("dbo.sp_SelectNarxPositionData");
//        trafficPositionProcedure_Spot = new SimpleJdbcCall(trafficDataSource)
//                .withProcedureName("dbo.sp_SelectSpotPositionData");
//        trafficPositionProcedure_RDT = new SimpleJdbcCall(trafficDataSource)
//                .withProcedureName("dbo.sp_SelectRDTPositionData");

        positions = CacheBuilder.newBuilder().refreshAfterWrite(1, TimeUnit.MINUTES).build(
                new CacheLoader<SOURCE_TYPE, List<Object[]>>() {

                    @Override
                    public List<Object[]> load(SOURCE_TYPE key) throws Exception {
                        logger.info("Loading cache data for TRENTO DB sp_Select" + key.name() + "PositionData");
                        return trentoPositionProcedure(key);
                    }
                });

    }

    public List<Object[]> getTrafficPositions(SOURCE_TYPE source) throws ExecutionException {
        return positions.get(source);
    }

    public List<Object[]> trentoPositionProcedure(SOURCE_TYPE source) {
        logger.info("Reading data for TRENTO DB sp_Select" + source + "PositionData via CS");

        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;
        List<Object[]> list = new ArrayList<>();

        try {
            // Get Connection instance from dataSource
            connection = trafficDataSource.getConnection();
            statement = connection.prepareCall(
                    "{CALL sp_Select" + source + "PositionData()}",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            boolean results = statement.execute();
            int rowsAffected = 0;

            // Protects against lack of SET NOCOUNT in stored procedure
            while (results || rowsAffected != -1) {
                if (results) {
                    rs = statement.getResultSet();
                    break;
                } else {
                    rowsAffected = statement.getUpdateCount();
                }
                results = statement.getMoreResults();
            }

            // read
            while (rs.next()) {
                // explicit map
                Object[] obj = new Object[3];
                obj[0] = rs.getObject(1);
                obj[1] = rs.getObject(2);
                obj[2] = rs.getObject(3);
                list.add(obj);
            }
        } catch (Exception ex) {
            logger.error("Error reading data for TRENTO DB sp_Select" + source + "PositionData: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    logger.error(
                            "Error closing rs for TRENTO DB sp_Select" + source + "PositionData: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    logger.error(
                            "Error closing statement for TRENTO DB sp_Select" + source + "PositionData: "
                                    + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    logger.error(
                            "Error closing connection for TRENTO DB sp_Select" + source + "PositionData: "
                                    + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }

        return list;
    }

    public List<Object[]> trentoTrafficProcedure(SOURCE_TYPE source, BY_TYPE by, long from, long to) {
        logger.info("Reading data for TRENTO DB sp_Select" + source + "Data via CS");

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        List<Object[]> list = new ArrayList<>();

        try {
            // Get Connection instance from dataSource
            connection = trafficDataSource.getConnection();
            statement = connection.prepareStatement(
                    "{CALL sp_Select" + source + "Data" + by + "(?, ?)}",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            
            statement.setTimestamp(1, new Timestamp(from));
            statement.setTimestamp(2, new Timestamp(to));

            boolean results = true;
            int rowsAffected = 0;

            // Protects against lack of SET NOCOUNT in stored procedure
            while (results || rowsAffected != -1) {
                if (results) {
                    rs = statement.getResultSet();
                    break;
                } else {
                    rowsAffected = statement.getUpdateCount();
                }
                results = statement.getMoreResults();
            }

            // read
            while (rs.next()) {
                // explicit map
                Object[] obj = new Object[3];
                obj[0] = rs.getObject(1);
                obj[1] = rs.getObject(2);
                obj[2] = rs.getObject(3);
                obj[3] = rs.getObject(4);
                obj[4] = rs.getObject(5);

                if (SOURCE_TYPE.Narx.equals(source)) {
                    obj[5] = rs.getObject(6);
                    obj[6] = rs.getObject(7);
                }
                list.add(obj);
            }
        } catch (Exception ex) {
            logger.error("Error reading data for TRENTO DB sp_Select" + source + "Data: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    logger.error(
                            "Error closing rs for TRENTO DB sp_Select" + source + "Data: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    logger.error(
                            "Error closing statement for TRENTO DB sp_Select" + source + "Data: "
                                    + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    logger.error(
                            "Error closing connection for TRENTO DB sp_Select" + source + "Data: "
                                    + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }

        return list;
    }
}
