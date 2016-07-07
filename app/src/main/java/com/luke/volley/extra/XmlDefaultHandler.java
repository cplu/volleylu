package com.luke.volley.extra;

import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by cplu on 2015/10/30.
 */
public abstract class XmlDefaultHandler<Type> extends DefaultHandler {
	protected Type m_obj;

	public Type getResult(){
		return m_obj;
	}
}
