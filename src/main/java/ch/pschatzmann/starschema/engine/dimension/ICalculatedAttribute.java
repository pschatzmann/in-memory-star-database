package ch.pschatzmann.starschema.engine.dimension;

import java.util.Map;

public interface ICalculatedAttribute {
	String getName();
	String getValue(Map<String, Object> recordMap);
}
