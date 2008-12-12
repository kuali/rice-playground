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
package org.kuali.rice.kew.engine.node;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * The state of a {@link RouteNodeInstance} represented as a key-value pair of Strings.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KREW_RTE_NODE_INSTN_ST_T")
@AttributeOverride(name="stateId", column=@Column(name="RTE_NODE_INSTN_ST_ID"))
@NamedQueries({
	@NamedQuery(name="NodeState.FindNodeState", query="select n from NodeState as n where n.nodeInstance.routeNodeInstanceId = :routeNodeInstanceId and n.key = :key"),
	@NamedQuery(name="NodeState.FindNodeStateById", query="select n from NodeState as n where n.stateId = :nodeStateId")
})
public class NodeState extends State {

    private static final long serialVersionUID = -4382379569851955918L;

    @ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="RTE_NODE_INSTN_ID")
	private RouteNodeInstance nodeInstance;
    @Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;
    
    public NodeState() {}
    
    public NodeState(String key, String value) {
        super(key, value);
    }
    
    public RouteNodeInstance getNodeInstance() {
        return nodeInstance;
    }
    public void setNodeInstance(RouteNodeInstance nodeInstance) {
        this.nodeInstance = nodeInstance;
    }

    public Long getNodeStateId() {
        return getStateId();
    }

    public void setNodeStateId(Long nodeStateId) {
        setStateId(nodeStateId);
    }

    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }
}
