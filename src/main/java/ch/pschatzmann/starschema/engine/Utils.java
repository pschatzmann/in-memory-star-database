package ch.pschatzmann.starschema.engine;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Compare methods for different data types which also support null and empty
 * values.
 * 
 * @author philschatzmann
 * 
 */
public class Utils {
	private static String emptyString = "";
	private static int nullFirstFactor = 1;
	private static DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}

	public static int compare(Object obj, Object obj1) {
		if (isEmpty(obj, obj1))
			return 0;
		if (isEmpty(obj))
			return -1 * nullFirstFactor;
		if (isEmpty(obj1))
			return 1 * nullFirstFactor;
		if (obj instanceof String && obj1 instanceof String) {
			return ((String) obj).compareTo((String) obj1);
		}
		if (obj instanceof Long) {
			return ((Long) obj).compareTo((Long) obj1);
		}
		if (obj instanceof Integer) {
			return ((Integer) obj).compareTo((Integer) obj1);
		}
		if (obj instanceof Double) {
			return ((Double) obj).compareTo((Double) obj1);
		}
		// in the last resort we convert the object to the string and
		// compare the strings
		return String.valueOf(obj).compareTo(String.valueOf(obj1));
	}

	public static int compare(String obj, String obj1) {
		return obj.compareTo(obj1);
	}

	public static int compare(Integer obj, Integer obj1) {
		return obj.compareTo(obj1);
	}

	public static int compare(Double obj, Double obj1) {
		return obj.compareTo(obj1);
	}

	public static boolean isEmpty(Object obj, Object obj1) {
		return (isEmpty(obj1) && isEmpty(obj));
	}

	public static boolean isEmpty(Object obj) {
		return (obj == null || obj.equals(emptyString));
	}

	public static String getEmptyString() {
		return emptyString;
	}

	public static void setEmptyString(String emptyString) {
		emptyString = emptyString;
	}

	public static void setNullFirst(boolean isNullFirst) {
		nullFirstFactor = isNullFirst ? 1 : -1;
	}
	
	public static Double toDouble(Object obj) {
		if (isEmpty(obj)) {
			return 0.0;
		}
		if (obj instanceof Double) {
			return (Double) obj; 
		}
		if (obj instanceof Number) {
			return ((Number)obj).doubleValue();
		}
		return Double.valueOf(obj.toString());
	}

	
	public static double todouble(Object obj) {
		Double value = toDouble(obj);
		return value == null ? 0.0 : value;
	}
	
	public static String toString(Object value) {
		String result = "";
		if (value!=null) {
			if (value instanceof Date) {
				Date date = (Date)value;
				if (date.getHours() == 0 && date.getMinutes()== 0 && date.getSeconds()==0) {
					result = dateFormat.format((Date)value);
				} else {
					result = timeFormat.format((Date)value);
				}
			} else {
				result = value.toString();
			}
		}
		return  result;
	}
	
	public static String nullValue(Object value, String defaultValue) {
		return isEmpty(value) ? defaultValue : value.toString();
	}

	public static List<String> csvToList(String values, String delim) {
		List<String> result = new ArrayList();
		for (String value : values.split(delim) ){
			result.add(value.trim());
		}
		return result;
	}

	public static String toCSV(Collection<String> collection, String delim) {
		StringBuffer sb = new StringBuffer();
		for (String name : collection) {
			if (sb.length()>0){
				sb.append(delim);
			}
			sb.append(name);
		}
		return sb.toString();
	}

	
}
