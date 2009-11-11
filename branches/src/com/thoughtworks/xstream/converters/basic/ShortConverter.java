package com.thoughtworks.xstream.converters.basic;

/**
 * Converts a short primitive or java.lang.Short wrapper to
 * a String.
 *
 * @author Joe Walnes
 */
public class ShortConverter extends AbstractSingleValueConverter {

    @Override
	public boolean canConvert(Class type) {
        return type.equals(short.class) || type.equals(Short.class);
    }

    @Override
	public Object fromString(String str) {
    	int value = Integer.decode(str).intValue();
    	if(value < Short.MIN_VALUE || value > 0xFFFF) {
    		throw new NumberFormatException("For input string: \"" + str + '"');
    	}
        return new Short((short)value);
    }

}
