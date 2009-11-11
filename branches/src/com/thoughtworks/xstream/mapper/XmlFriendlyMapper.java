package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;

/**
 * Mapper that ensures that all names in the serialization stream are XML friendly.
 * The replacement chars and strings are:
 * <ul>
 * <li><b>$</b> (dollar) chars appearing in class names are replaced with <b>_</b> (underscore) chars.<br></li>
 * <li><b>$</b> (dollar) chars appearing in field names are replaced with <b>_DOLLAR_</b> string.<br></li>
 * <li><b>_</b> (underscore) chars appearing in field names are replaced with <b>__</b> (double underscore) string.<br></li>
 * <li><b>default</b> as the prefix for class names with no package.</li>
 * </ul>
 * 
 * @author Joe Walnes
 * @author Mauro Talevi
 */
public class XmlFriendlyMapper extends AbstractXmlFriendlyMapper {

    public XmlFriendlyMapper(Mapper wrapped) {
        super(wrapped);
    }
    
    /**
     * @deprecated As of 1.2, use {@link #XmlFriendlyMapper(Mapper)}
     */
    @Deprecated
	public XmlFriendlyMapper(ClassMapper wrapped) {
        this((Mapper)wrapped);
    }

    @Override
	public String serializedClass(Class type) {
        return escapeClassName(super.serializedClass(type));
    }

    @Override
	public Class realClass(String elementName) {
        return super.realClass(unescapeClassName(elementName));
    }

    @Override
	public String serializedMember(Class type, String memberName) {
        return escapeFieldName(super.serializedMember(type, memberName));
    }

    @Override
	public String realMember(Class type, String serialized) {
        return unescapeFieldName(super.realMember(type, serialized));
    }

    public String mapNameToXML(String javaName) {
        return escapeFieldName(javaName);
    }

    public String mapNameFromXML(String xmlName) {
        return unescapeFieldName(xmlName);
    }

}
