package it.smartcommunitylab.trento.mobilitydatawrapper;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

public class GeoUtils {
	
	private static MathTransform transform;
	private static GeometryFactory geometryFactory;

	public static void init() throws Exception {
		CoordinateReferenceSystem sourceCRS = CRS.decode("epsg:3064");
		CoordinateReferenceSystem targetCRS = CRS.decode("epsg:4326");
		transform = CRS.findMathTransform(sourceCRS, targetCRS, false);
		geometryFactory = new GeometryFactory(new PrecisionModel());	
	}
	
	public static Point convert(double x, double y) {
		Point point = geometryFactory.createPoint(new Coordinate(x,y));
		try {
			Point targetPoint = (Point) JTS.transform(point, transform);
			return targetPoint;
		} catch (Exception e) {
			return null;
		}
	}
	
}
