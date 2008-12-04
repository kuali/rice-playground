/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.rice.kew.edl.bo;

import java.io.Serializable;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * Association between WorkflowDocument type -&gt; EDocLite definition, EDocLite style
 * Table: en_edoclt_assoc_t
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KREW_EDL_ASSCTN_T")
public class EDocLiteAssociation  extends PersistableBusinessObjectBase implements Serializable{

	private static final long serialVersionUID = 7300251507982374010L;
	/**
     * edoclt_assoc_id
     */
    @Id
	@Column(name="EDOCLT_ASSOC_ID")
	private Long edocLiteAssocId;
    /**
     * edoclt_assoc_doctype_nm
     */
    @Column(name="DOC_TYP_NM")
	private String edlName;
    /**
     * edoclt_assoc_def_nm
     */
    @Column(name="EDL_DEF_NM")
	private String definition;
    /**
     * edoclt_assoc_style_nm
     */
    @Column(name="STYLE_NM")
	private String style;
    /**
     * edoclt_assoc_actv_ind
     */
    @Column(name="ACTV_IND")
	private Boolean activeInd;

    @Transient
    private String actionsUrl;//for quickfinder

    public Long getEdocLiteAssocId() {
        return edocLiteAssocId;
    }
    public void setEdocLiteAssocId(Long edocLiteAssocId) {
        this.edocLiteAssocId = edocLiteAssocId;
    }
    public String getEdlName() {
        return edlName;
    }
    public void setEdlName(String edlName) {
        this.edlName = edlName;
    }
    public String getDefinition() {
        return definition;
    }
    public void setDefinition(String definition) {
        this.definition = definition;
    }
    public String getStyle() {
        return style;
    }
    public void setStyle(String style) {
        this.style = style;
    }
    public Boolean getActiveInd() {
        return activeInd;
    }
    public void setActiveInd(Boolean activeInd) {
        this.activeInd = activeInd;
    }

    public String toString() {
        return "[EDocLiteAssociation: edocLiteAssocId=" + edocLiteAssocId
                                 + ", edlName=" + edlName
                                 + ", definition=" + definition
                                 + ", style=" + style
                                 + ", activeInd=" + activeInd
                                 + ", versionNumber=" + versionNumber
                                 + "]";
    }
	public String getActionsUrl() {
		return actionsUrl;
	}
	public void setActionsUrl(String actionsUrl) {
		this.actionsUrl = actionsUrl;
	}
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
	    propMap.put("edocLiteAssocId", getEdocLiteAssocId());
	    propMap.put("edlName", getEdlName());
	    propMap.put("definition", getDefinition());
	    propMap.put("style", getStyle());
	    propMap.put("activeInd", getActiveInd());
	    propMap.put("versionNumber", getVersionNumber());	    
	    return propMap;
		
	}
}
