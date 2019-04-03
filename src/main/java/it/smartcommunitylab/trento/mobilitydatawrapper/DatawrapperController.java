package it.smartcommunitylab.trento.mobilitydatawrapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatawrapperController {

	@Autowired
	private DataCache cache;

	@GetMapping("/parkings")
	public @ResponseBody List<Parking> getParkings() throws Exception {
		return cache.getParkings();
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "An error occurred")
	@ExceptionHandler(DataIntegrityViolationException.class)
	public void error() {
	}
}
