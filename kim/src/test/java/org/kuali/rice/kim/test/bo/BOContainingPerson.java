package org.kuali.rice.kim.test.bo;


import java.util.LinkedHashMap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class BOContainingPerson extends PersistableBusinessObjectBase {

	@Id
	@Column(name="pk")
	protected String boPrimaryKey;
	
	@Column(name="prncpl_id")
	protected String principalId;
	
	protected Person person;
	
	public String getBoPrimaryKey() {
		return this.boPrimaryKey;
	}

	public void setBoPrimaryKey(String boPrimaryKey) {
		this.boPrimaryKey = boPrimaryKey;
	}

	public String getPrincipalId() {
		return this.principalId;
	}

	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}

	public Person getPerson() {
		person = KIMServiceLocator.getPersonService().updatePersonIfNecessary( principalId, person );
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "boPrimaryKey", boPrimaryKey );
		m.put( "principalId", principalId );
		return m;
	}

}
