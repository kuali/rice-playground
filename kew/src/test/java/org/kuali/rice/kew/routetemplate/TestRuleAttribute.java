/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
package org.kuali.rice.kew.routetemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.rule.ResolvedQualifiedRole;
import org.kuali.rice.kew.rule.Role;
import org.kuali.rice.kew.rule.RoleAttribute;
import org.kuali.rice.kew.rule.WorkflowAttribute;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kew.rule.WorkflowAttributeXmlValidator;


/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class TestRuleAttribute implements WorkflowAttribute, RoleAttribute, WorkflowAttributeXmlValidator {

	public static boolean VALID_CLIENT_ROUTING_DATA_CALLED = false;
	
	private static Map roles = new HashMap();
	private boolean required;
		
    public boolean isMatch(DocumentContent documentContent, List ruleExtensions) {
        return true;
    }
    
    public List getRoleNames() {
        List roleNames = new ArrayList();
        for (Iterator iterator = roles.keySet().iterator(); iterator.hasNext();) {
            String roleName = (String) iterator.next();
            roleNames.add(new Role(getClass(), roleName, roleName));
        }
    	return roleNames;
    }

    public List getRuleRows() {
    	return new ArrayList();
    }

    public List getRoutingDataRows() {
    	return new ArrayList();
    }

    public String getDocContent() {
    	return "<testRuleAttributeContent/>";
    }

    public List getRuleExtensionValues() {
    	return new ArrayList();
    }

    public List validateRoutingData(Map paramMap) {
    	return new ArrayList();
    }
    
    public String getAttributeLabel(){
        return "";
    }

    public List validateRuleData(Map paramMap) {
    	return new ArrayList();
    }

    public void setRequired(boolean required) {
    	this.required = required;
    }

    public boolean isRequired() {
        return required;
    }

	public List getQualifiedRoleNames(String roleName, DocumentContent documentContent) throws KEWUserNotFoundException {
		ArrayList qualifiedRoleNames = new ArrayList();
		Map qualifiedRoles = (Map)roles.get(roleName);
		if (qualifiedRoles != null) {
			qualifiedRoleNames.addAll(qualifiedRoles.keySet());
		} else {
			throw new IllegalArgumentException("TestRuleAttribute does not support the given role " + roleName);
		}
		return qualifiedRoleNames;
	}

	public ResolvedQualifiedRole resolveQualifiedRole(RouteContext routeContext, String roleName, String qualifiedRole) throws KEWUserNotFoundException {
		ResolvedQualifiedRole resolved = new ResolvedQualifiedRole();
		Map qualifiedRoles = (Map)roles.get(roleName);
		if (qualifiedRoles != null) {
			List recipients = (List)qualifiedRoles.get(qualifiedRole);
			if (recipients != null) {
				resolved.setQualifiedRoleLabel(qualifiedRole);
				resolved.setRecipients(recipients);
			} else {
				throw new IllegalArgumentException("TestRuleAttribute does not support the qualified role " + qualifiedRole);
			}
		} else {
			throw new IllegalArgumentException("TestRuleAttribute does not support the given role " + roleName);
		}
		return resolved;
	}
	
	public static void addRole(String roleName) {
		roles.put(roleName, new HashMap());
	}
	
	public static void removeRole(String roleName) {
		roles.remove(roleName);
	}
	
	public static Map getRole(String roleName) {
		return (Map)roles.get(roleName);
	}
	
	public static void addQualifiedRole(String roleName, String qualifiedRoleName) {
		getRole(roleName).put(qualifiedRoleName, new ArrayList());
	}
	
	public static void removeQualifiedRole(String roleName, String qualifiedRoleName) {
		getRole(roleName).remove(qualifiedRoleName);
	}
	
	/**
	 * All you need to call now.  Simplies the previous 3 step process of adding role, qual role then recipients
	 * 
	 * @param roleName
	 * @param qualifiedRoleName
	 * @param recipients
	 */
	public static void setRecipients(String roleName, String qualifiedRoleName, List recipients) {
		Map qualifiedRoles = getRole(roleName);
		if (qualifiedRoles == null) {
		    addRole(roleName);
		}
		if (qualifiedRoles == null) {
		    addRole(roleName);
		    addQualifiedRole(roleName, qualifiedRoleName);
		    qualifiedRoles = getRole(roleName);
		}
		qualifiedRoles.put(qualifiedRoleName, recipients);
	}
	
	public static List getRecipients(String roleName, String qualifiedRoleName) {
		Map qualifiedRoles = getRole(roleName);
		return (List)qualifiedRoles.get(qualifiedRoleName);
	}

	public List<WorkflowAttributeValidationError> validateClientRoutingData() {
		return new ArrayList<WorkflowAttributeValidationError>();
	}

}
