package com.thoughtworks.xstream.persistence;

import java.util.AbstractSet;
import java.util.Iterator;

/**
 * A persistent set implementation.
 * 
 * @author Guilherme Silveira
 */
public class XmlSet extends AbstractSet {

	private final XmlMap map;

	public XmlSet(StreamStrategy streamStrategy) {
		this.map = new XmlMap(streamStrategy);
	}

	@Override
	public Iterator iterator() {
		return map.values().iterator();
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean add(Object o) {
		if (map.containsValue(o)) {
			return false;
		} else {
			// not-synchronized!
			map.put(findEmptyKey(), o);
			return true;
		}
	}

	private String findEmptyKey() {
		long i = System.currentTimeMillis();
		while (map.containsKey("" + i)) {
			i++;
		}
		return "" + i;
	}

}
