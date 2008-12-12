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
package org.kuali.rice.kew.applicationconstants.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.kuali.rice.kew.applicationconstants.ApplicationConstant;
import org.kuali.rice.kew.applicationconstants.dao.ApplicationConstantsDAO;


/**
 * OJB implementation of the {@link ApplicationConstantsDAO}.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ApplicationConstantsDaoJpaImpl implements ApplicationConstantsDAO {

    @PersistenceContext(unitName="kew-unit")
    private EntityManager entityManager;

    public void saveConstant(ApplicationConstant applicationConstant) {
        if (entityManager.find(ApplicationConstant.class, applicationConstant.getApplicationConstantName()) == null) {
            entityManager.persist(applicationConstant);
        } else {
            entityManager.merge(applicationConstant);
        }
    }

    public void deleteConstant(ApplicationConstant applicationConstant) {
        ApplicationConstant reattatched = entityManager.merge(applicationConstant);
        entityManager.remove(reattatched);
    }

    public ApplicationConstant findByName(String applicationConstantName) {
        return (ApplicationConstant) entityManager.createNamedQuery("ApplicationConstant.FindByApplicationConstantName").setParameter("applicationConstantName", applicationConstantName).getSingleResult();
    }

    public List<ApplicationConstant> findAll() {
        return (List<ApplicationConstant>) entityManager.createNamedQuery("ApplicationConstant.FindAll").getResultList();
    }

}