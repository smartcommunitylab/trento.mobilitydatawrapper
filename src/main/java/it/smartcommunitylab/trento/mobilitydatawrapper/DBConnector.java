package it.smartcommunitylab.trento.mobilitydatawrapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.geotools.referencing.CRS;
import org.hibernate.procedure.ProcedureOutputs;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import it.smartcommunitylab.trento.mobilitydatawrapper.model.BY_TYPE;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.DATA_TYPE;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.Parking;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.Position;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.SOURCE_TYPE;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.Traffic;

@Component
public class DBConnector {

	@Autowired
	@Qualifier("parkingsEntityManager")
	private EntityManager parkingsEntityManager;

	@Autowired
	@Qualifier("trafficEntityManager")
	private EntityManager trafficEntityManager;

	private static transient final Logger logger = LoggerFactory.getLogger(DBConnector.class);

	private LoadingCache<DATA_TYPE, List<Parking>> parkings;

	@PostConstruct
	public void init() throws Exception {
		GeoUtils.init();
		
		parkings = CacheBuilder.newBuilder().refreshAfterWrite(1, TimeUnit.MINUTES).build(new CacheLoader<DATA_TYPE, List<Parking>>() {

			@Override
			public List<Parking> load(DATA_TYPE key) throws Exception {
				switch (key) {
				case PARKINGS:
					return trentoParkingsProcedure();
				}
				return null;
			}
		});
		
	
	}

	public List<Parking> getParkings() throws ExecutionException {
		return parkings.get(DATA_TYPE.PARKINGS);
	}

	public List<Parking> trentoParkingsProcedure() {
		logger.info("Reading data from TRENTO DB sp_SelectParkingData");
		
		String q = "{CALL dbo.sp_SelectParkingData()}";
		Query query = parkingsEntityManager.createNativeQuery(q);
		List<Object[]> result = query.getResultList();
		List<Parking> parkings = result.stream().map(x -> new Parking(x)).collect(Collectors.toList());
		return parkings;
	}

	public List<Object[]> trentoTrafficProcedure(SOURCE_TYPE source, BY_TYPE by, long from, long to) {
	    logger.info("Reading data from TRENTO DB sp_Select" + source + "Data" + by);

		StoredProcedureQuery storedProcedure = trafficEntityManager.createStoredProcedureQuery("dbo.sp_Select" + source + "Data" + by);

		storedProcedure.registerStoredProcedureParameter(0, Timestamp.class, ParameterMode.IN);
		storedProcedure.registerStoredProcedureParameter(1, Timestamp.class, ParameterMode.IN);

		storedProcedure.setParameter(0, new Timestamp(from));
		storedProcedure.setParameter(1, new Timestamp(to));
		
        try {
            //explicitly fetch to a list to flush cursor results
            List<Object[]> result = storedProcedure.getResultList();
            return result;
        } finally {
            //properly close underlying JDBC CallableStatement to avoid leaking 
            storedProcedure.unwrap(ProcedureOutputs.class).release();
        }
	}

	public List<Object[]> trentoPositionProcedure(SOURCE_TYPE source) {
        logger.info("Reading data from TRENTO DB sp_Select" + source + "PositionData");

        StoredProcedureQuery storedProcedure = trafficEntityManager.createStoredProcedureQuery("dbo.sp_Select" + source + "PositionData");
        try {
            //explicitly fetch to a list to flush cursor results            
            List<Object[]> result = storedProcedure.getResultList();
            return result;
        } finally {
            // properly close underlying JDBC CallableStatement to avoid leaking
            storedProcedure.unwrap(ProcedureOutputs.class).release();
        }
	}

	// public List<Position> trentoPositionProcedure(SOURCE_TYPE source) {
//  String q = "{CALL dbo.sp_Select" + source + "PositionData()}";
//  Query query = trafficEntityManager.createNativeQuery(q);
//  List<Object[]> result = query.getResultList();
//  List<Position> positions = result.stream().map(x -> new Position(x)).collect(Collectors.toList());
//  return positions;
//}
	
}
