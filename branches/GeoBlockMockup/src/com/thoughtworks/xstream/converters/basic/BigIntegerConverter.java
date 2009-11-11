package com.thoughtworks.xstream.converters.basic;

import java.math.BigInteger;

/**
 * Converts a java.math.BigInteger to a String.
 *
 * @author Joe Walnes
 */
public class BigIntegerConverter extends AbstractSingleValueConverter {

    @Override
	public boolean canConvert(Class type) {
        return type.equals(BigInteger.class);
    }

    @Override
	public Object fromString(String str) {
        return new BigInteger(str);
    }

}
