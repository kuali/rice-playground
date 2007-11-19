/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.workflow.attribute;

import org.w3c.dom.Element;

import edu.iu.uis.eden.routetemplate.xmlrouting.StandardGenericXMLRuleAttribute;

/**
 * This class extends the workflow xml rule attribute implementation to use the information in the data dictionary to generate
 * labels.
 */
public class KualiXmlRuleAttributeImpl extends StandardGenericXMLRuleAttribute implements KualiXmlAttribute {
	private static final long serialVersionUID = -3453451186396963835L;

    /**
     * Constructs a KualiXmlRuleAttributeImpl.java.
     */
    public KualiXmlRuleAttributeImpl() {
        super();
    }

    /**
     * This method overrides the super class and modifies the XML that it operates on to put the name and the title in the place
     * where the super class expects to see them, even though they may no longer exist in the original XML.
     * 
     * @see edu.iu.uis.eden.routetemplate.xmlrouting.StandardGenericXMLRuleAttribute#getConfigXML()
     */
    public Element getConfigXML() {
        Element root = getAttributeConfigXML();
        KualiXmlAttributeHelper attributeHelper = new KualiXmlAttributeHelper();
        // this adds the name and title to the xml based on the data dictionary
        return attributeHelper.processConfigXML(root);
    }

    public Element getAttributeConfigXML() {
        return super.getConfigXML();
    }

}
