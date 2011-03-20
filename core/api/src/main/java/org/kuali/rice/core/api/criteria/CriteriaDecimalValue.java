/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.core.api.criteria;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.mutable.MutableDouble;
import org.apache.commons.lang.mutable.MutableFloat;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = CriteriaDecimalValue.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = CriteriaDecimalValue.Constants.TYPE_NAME)
public final class CriteriaDecimalValue implements CriteriaValue<BigDecimal> {

    @XmlValue
    private final BigDecimal value;
    
    CriteriaDecimalValue() {
        this.value = null;
    }
    
    CriteriaDecimalValue(BigDecimal value) {
    	validateValue(value);
        this.value = value;
    }
    
    CriteriaDecimalValue(Float value) {
    	validateValue(value);
    	this.value = BigDecimal.valueOf(value.doubleValue());
    }
    
    CriteriaDecimalValue(MutableFloat value) {
    	validateValue(value);
    	this.value = BigDecimal.valueOf(value.doubleValue());
    }
    
    CriteriaDecimalValue(Double value) {
    	validateValue(value);
    	this.value = BigDecimal.valueOf(value.doubleValue());
    }
    
    CriteriaDecimalValue(MutableDouble value) {
    	validateValue(value);
    	this.value = BigDecimal.valueOf(value.doubleValue());
    }
    
    private static void validateValue(Object value) {
    	if (value == null) {
    		throw new IllegalArgumentException("Value cannot be null.");
    	}
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "decimalValue";
        final static String TYPE_NAME = "CriteriaDecimalValueType";
    }
    
}
