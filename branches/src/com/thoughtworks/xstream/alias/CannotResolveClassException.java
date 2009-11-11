package com.thoughtworks.xstream.alias;

import com.thoughtworks.xstream.core.BaseException;

/**
 * @deprecated As of 1.2, use {@link com.thoughtworks.xstream.mapper.CannotResolveClassException} instead
 */
@Deprecated
public class CannotResolveClassException extends BaseException {
    /**
     * @deprecated As of 1.2
     */
    @Deprecated
	public CannotResolveClassException(String className) {
        super(className);
    }
}
