/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kew.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.kuali.rice.kew.exception.InvalidXmlException;
import org.kuali.rice.kew.util.XmlHelper;
import org.kuali.rice.kim.bo.entity.impl.EntityEmailImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityEntityTypeImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityNameImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.xml.sax.SAXException;

/**
 * Parses users from XML.
 * 
 * This is really meant for use only in the unit tests and was written to help ease
 * transition over to KIM.  There are numerous unit tests which took advantage of
 * the ability to import "users" from XML in KEW.  KIM does not provide XML
 * import capabilities in the initial implementation so this class provides that.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class UserXmlParser implements XmlConstants {
    
    private static final Namespace NAMESPACE = Namespace.getNamespace("", "ns:workflow/User");

    private static final String USERS_ELEMENT = "users";
    private static final String USER_ELEMENT = "user";
    private static final String WORKFLOW_ID_ELEMENT = "workflowId";
    private static final String AUTHENTICATION_ID_ELEMENT = "authenticationId";
    private static final String EMAIL_ELEMENT = "emailAddress";
    private static final String GIVEN_NAME_ELEMENT = "givenName";
    private static final String LAST_NAME_ELEMENT = "lastName";    
    
    public void parseUsers(InputStream input) throws IOException, InvalidXmlException {
        try {
            Document doc = XmlHelper.trimSAXXml(input);
            Element root = doc.getRootElement();
            parseUsers(root);
        } catch (JDOMException e) {
            throw new InvalidXmlException("Parse error.", e);
        } catch (SAXException e){
            throw new InvalidXmlException("Parse error.",e);
        } catch(ParserConfigurationException e){
            throw new InvalidXmlException("Parse error.",e);
        }
    }

    public void parseUsers(Element root) throws InvalidXmlException {
    	for (Iterator usersElementIt = root.getChildren(USERS_ELEMENT, NAMESPACE).iterator(); usersElementIt.hasNext();) {
    		Element usersElement = (Element) usersElementIt.next();
    		for (Iterator iterator = usersElement.getChildren(USER_ELEMENT, NAMESPACE).iterator(); iterator.hasNext();) {
    			Element userElement = (Element) iterator.next();
    			KimEntityImpl entity = constructEntity(userElement);
    			constructPrincipal(userElement, entity.getEntityId());
    		}
    	}
    }
    
    protected KimEntityImpl constructEntity(Element userElement) {
    	
    	String firstName = userElement.getChildTextTrim(GIVEN_NAME_ELEMENT, NAMESPACE);
        String lastName = userElement.getChildTextTrim(LAST_NAME_ELEMENT, NAMESPACE);
    	
    	Long entityId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ENTITY_ENT_TYP_ID_S");
		KimEntityImpl entity = new KimEntityImpl();
		entity.setActive(true);
		entity.setEntityId("" + entityId);
		
		Long entityTypeId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ENTITY_ENT_TYP_ID_S");
		EntityEntityTypeImpl entityType = new EntityEntityTypeImpl();
		entity.getEntityTypes().add(entityType);
		entityType.setEntityTypeCode("PERSON");
		entityType.setEntityId(entity.getEntityId());
		entityType.setEntityEntityTypeId(""+entityTypeId);
		entityType.setActive(true);
		
		Long entityNameId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ENTITY_NM_ID_S");
		EntityNameImpl name = new EntityNameImpl();
		name.setActive(true);
		name.setEntityNameId("" + entityNameId);
		name.setEntityId(entity.getEntityId());
		name.setNameTypeCode("PREFERRED");
		name.setFirstName(firstName);
		name.setMiddleName("");
		name.setLastName(lastName);
		name.setDefault(true);
		
		entity.getNames().add(name);
		
		KNSServiceLocator.getBusinessObjectService().save(entity);
		
		String emailAddress = userElement.getChildTextTrim(EMAIL_ELEMENT, NAMESPACE);
		if (!StringUtils.isBlank(emailAddress)) {
			Long emailId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ENTITY_EMAIL_ID_S");
			EntityEmailImpl email = new EntityEmailImpl();
			email.setActive(true);
			email.setEntityEmailId("" + emailId);
			email.setEntityTypeCode("PERSON");
			email.setEmailTypeCode("CAMPUS");
			email.setEmailAddress(emailAddress);
			email.setDefault(true);
			email.setEntityId(entity.getEntityId());
			KNSServiceLocator.getBusinessObjectService().save(email);
		}
		
		return entity;
    }
    
    protected KimPrincipalImpl constructPrincipal(Element userElement, String entityId) {
    	String principalId = userElement.getChildTextTrim(WORKFLOW_ID_ELEMENT, NAMESPACE);
    	String principalName = userElement.getChildTextTrim(AUTHENTICATION_ID_ELEMENT, NAMESPACE);
    	
		KimPrincipalImpl principal = new KimPrincipalImpl();
		principal.setActive(true);
		principal.setPrincipalId(principalId);
		principal.setPrincipalName(principalName);
		principal.setEntityId(entityId);
		KNSServiceLocator.getBusinessObjectService().save(principal);
		
		return principal;
    }

}
