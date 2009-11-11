package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

public class ReferenceByIdUnmarshaller extends AbstractReferenceUnmarshaller {

    public ReferenceByIdUnmarshaller(Object root, HierarchicalStreamReader reader,
                                     ConverterLookup converterLookup, Mapper mapper) {
        super(root, reader, converterLookup, mapper);
    }

    /**
     * @deprecated As of 1.2, use {@link #ReferenceByIdUnmarshaller(Object, HierarchicalStreamReader, ConverterLookup, Mapper)}
     */
    @Deprecated
	public ReferenceByIdUnmarshaller(Object root, HierarchicalStreamReader reader,
                                     ConverterLookup converterLookup, ClassMapper classMapper) {
        this(root, reader, converterLookup, (Mapper)classMapper);
    }

    @Override
	protected Object getReferenceKey(String reference) {
        return reference;
    }

    @Override
	protected Object getCurrentReferenceKey() {
        return reader.getAttribute(getMapper().aliasForAttribute("id"));
    }
}
