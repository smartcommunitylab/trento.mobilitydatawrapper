package it.smartcommunitylab.trento.mobilitydatawrapper;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Component
public class DataCache {

	@Autowired
	@Qualifier("parkingsEntityManager")
	private EntityManager parkingsEntityManager;

	private static transient final Logger logger = LoggerFactory.getLogger(DataCache.class);
	
	private LoadingCache<DATA_TYPE, List<Parking>> parkings;

	
	@PostConstruct
	public void init() {
		parkings = CacheBuilder.newBuilder().refreshAfterWrite(1, TimeUnit.MINUTES).build(new CacheLoader<DATA_TYPE, List<Parking>>() {

			@Override
			public List<Parking> load(DATA_TYPE key) throws Exception {
				switch (key) {
				case PARKINGS:
					return trentoParkings();
				}
				return null;
			}
		});
	}
	
	public List<Parking> getParkings() throws ExecutionException {
		return parkings.get(DATA_TYPE.PARKINGS);
	}
	
	public List<Parking> trentoParkings() throws Exception {
		logger.info("Reading data from TRENTO DB");
		String q = "{CALL dbo.sp_SelectParkingData()}";
		Query query = parkingsEntityManager.createNativeQuery(q);
		List<Object[]> result = query.getResultList();
		List<Parking> parkings = result.stream().map(x -> new Parking(x)).collect(Collectors.toList());
		return parkings;

	}

}
