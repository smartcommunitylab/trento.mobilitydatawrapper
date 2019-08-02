package it.smartcommunitylab.trento.mobilitydatawrapper;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.trento.mobilitydatawrapper.model.BY_TYPE;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.Parking;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.Position;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.SOURCE_TYPE;
import it.smartcommunitylab.trento.mobilitydatawrapper.model.Traffic;

@RestController
public class DatawrapperController {

	public static final long MAX_INT = 1000*60*60*24*31L;
	
	@Autowired
	private DBConnector cache;

	@GetMapping("/parkings")
	public @ResponseBody List<Parking> getParkings() throws Exception {
		return cache.getParkings();
	}

	@GetMapping("/traffic/{source}/{by}/{from}/{to}")
	public @ResponseBody List<Traffic> getTraffic(@PathVariable SOURCE_TYPE source, @PathVariable BY_TYPE by, @PathVariable long from, @PathVariable long to) throws Exception {
//		long f = 1543618800000L; // December 1st, 2018 0:00:00
//		long t = 1544309999000L; // December 8th, 2018 23:59:59
		long diff = to - from;
		if ((BY_TYPE.By5m.equals(by) || BY_TYPE.ByHour.equals(by)) && diff > MAX_INT) throw new IllegalArgumentException();
		return cache.trentoTrafficProcedure(source, by, from, to);
	}	
	
	@GetMapping("/positions/{source}")
	public @ResponseBody List<Position> getPositions(@PathVariable SOURCE_TYPE source) throws Exception {
		return cache.trentoPositionProcedure(source);
	}		

	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "An error occurred")
	@ExceptionHandler(IllegalArgumentException.class)
	public void error(HttpServletResponse response) {
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "An error occurred")
	@ExceptionHandler(DataIntegrityViolationException.class)
	public void error() {
	}
}
