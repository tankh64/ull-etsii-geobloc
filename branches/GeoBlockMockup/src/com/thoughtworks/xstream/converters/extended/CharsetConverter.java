package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import java.nio.charset.Charset;

/**
 * Converts a java.nio.charset.Carset to a string.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class CharsetConverter extends AbstractSingleValueConverter {

    @Override
	public boolean canConvert(Class type) {
        return Charset.class.isAssignableFrom(type);
    }

    @Override
	public String toString(Object obj) {
        return obj == null ? null : ((Charset)obj).name();
    }


    @Override
	public Object fromString(String str) {
        return Charset.forName(str);
    }
}