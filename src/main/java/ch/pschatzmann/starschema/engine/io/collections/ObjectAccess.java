package ch.pschatzmann.starschema.engine.io.collections;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Uses a java bean as source for an individual row
 * 
 * @author pschatzmann
 *
 */
public class ObjectAccess implements IAccess {
	private Map<Class, Map<String, Method>> accessors = new HashMap();
	private IFieldSelectionSource reader;

	/**
	 * Constructor
	 * 
	 * @param or
	 */
	public ObjectAccess(IFieldSelectionSource or) {
		reader = or;
	}

	/**
	 * Returns the field names of the object
	 */
	public List<String> getFieldNames(Object row) {
		Map<String, Method> fieldMethodMap = getAccessors(row);

		// determine the field names
		List result;
		if (reader.isIncludedFieldsOnly()) {
			result = reader.getIncludedFields();
		} else {
			result = new ArrayList(fieldMethodMap.keySet());
		}
		return result;
	}

	private Map<String, Method> getAccessors(Object row) {
		Map<String, Method> fieldMethodMap = accessors.get(row.getClass());
		if (fieldMethodMap == null) {
			fieldMethodMap = beanProperties(row);
			accessors.put(row.getClass(), fieldMethodMap);
		}
		return fieldMethodMap;
	}

	/**
	 * Get the field value from a object using the reflection accessor method
	 */
	public Object getValue(String field, Object row) {
		Map<String, Method> methods = accessors.get(row.getClass());
		try {
			Object result = null;
			Method method = methods.get(field);
			if (method!=null) {
				result = method.invoke(row);
			}
			return result;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Determines the read methods from a bean
	 * 
	 * @param bean
	 * @return
	 */
	protected Map<String, Method> beanProperties(Object bean) {
		try {
			Map map = Arrays.asList(Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors())
					.stream().filter(pd -> Objects.nonNull(pd.getReadMethod()))
					.filter(pd -> !reader.getExcludedFields().contains(pd.getReadMethod().getName()))
					.filter(pd -> !reader.getExcludedFields().contains(getFieldName(pd.getReadMethod().getName())))
					.collect(Collectors.toMap(pd -> getFieldName(pd.getReadMethod().getName()),
							pd1 -> pd1.getReadMethod()));
			return map;
		} catch (IntrospectionException e) {
			return Collections.emptyMap();
		}
	}

	/**
	 * Remove the leading is or get from the method in order to determine the field
	 * name
	 * 
	 * @param methodName
	 * @return
	 */
	protected String getFieldName(String methodName) {
		String result = methodName;
		if (methodName.startsWith("get")) {
			result = methodName.substring(3);
		} else if (methodName.startsWith("is")) {
			result = methodName.substring(2);
		}
		return result;
	}

	@Override
	public boolean isValid(Object obj) {
		return !obj.getClass().isPrimitive();
	}

	/**
	 * Determine the return type with refelection from the method
	 */
	@Override
	public Class getReturnType(String fieldName, Object obj) {
		Map<String, Method> fieldMethodMap = getAccessors(obj);
		Method method = fieldMethodMap.get(fieldName);	
		return method == null ? null : method.getReturnType();
	}

}
