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
package org.kuali.rice.kns.lookup;

import java.io.Serializable;
import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.mask.Mask;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.format.DateFormatter;

/**
 * This class holds details of html data for an action url.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public abstract class HtmlData implements Serializable {

	protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(HtmlData.class);

	public static final String ANCHOR_HTML_DATA_TYPE = AnchorHtmlData.class.getName();
	public static final String INPUT_HTML_DATA_TYPE = InputHtmlData.class.getName();
	
	protected String name = "";
	protected String title = "";
	protected String methodToCall = "";
	protected String displayText = "";
	protected String prependDisplayText = "";
	protected String appendDisplayText = "";
	protected List<HtmlData> childUrlDataList;

	/**
	 * 
	 * This method constructs the complete html tag based on the class attribute
	 * values.
	 * 
	 * @return
	 */
	public abstract String constructCompleteHtmlTag();

	/**
	 * @return the appendDisplayText
	 */
	public String getAppendDisplayText() {
		return this.appendDisplayText;
	}

	/**
	 * @param appendDisplayText the appendDisplayText to set
	 */
	public void setAppendDisplayText(String appendDisplayText) {
		this.appendDisplayText = appendDisplayText;
	}

	/**
	 * @return the childUrlDataList
	 */
	public List<HtmlData> getChildUrlDataList() {
		return this.childUrlDataList;
	}

	/**
	 * @param childUrlDataList the childUrlDataList to set
	 */
	public void setChildUrlDataList(List<HtmlData> childUrlDataList) {
		this.childUrlDataList = childUrlDataList;
	}

	/**
	 * @return the prependDisplayText
	 */
	public String getPrependDisplayText() {
		return this.prependDisplayText;
	}

	/**
	 * @param prependDisplayText the prependDisplayText to set
	 */
	public void setPrependDisplayText(String prependDisplayText) {
		this.prependDisplayText = prependDisplayText;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the displayText
	 */
	public String getDisplayText() {
		return this.displayText;
	}

	/**
	 * @param displayText the displayText to set
	 */
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	/**
	 * @return the methodToCall
	 */
	public String getMethodToCall() {
		return this.methodToCall;
	}

	/**
	 * @param methodToCall the methodToCall to set
	 */
	public void setMethodToCall(String methodToCall) {
		this.methodToCall = methodToCall;
	}

	public String getTitle(String prependText, Class bo, List keys) {
		return KNSConstants.EMPTY_STRING;
	}

	/**
	 * KFSMI-658 This method gets the title text for a link/control
	 * 
	 * @param prependText
	 * @param bo
	 * @param fieldConversions
	 * @param returnKeys
	 * @return title text
	 */
	public static String getTitleText(String prependText, BusinessObject bo, List keys) {
		if (bo == null)
			return KNSConstants.EMPTY_STRING;

		Map<String, String> m = new HashMap<String, String>();
		Iterator keysIt = keys.iterator();
		while (keysIt.hasNext()) {
			String fieldNm = (String) keysIt.next();
			Object fieldVal = ObjectUtils.getPropertyValue(bo, fieldNm);
			// need to format date in url
			if (fieldVal == null) {
				fieldVal = KNSConstants.EMPTY_STRING;
			} else if (fieldVal instanceof Date) {
				DateFormatter dateFormatter = new DateFormatter();
				fieldVal = dateFormatter.format(fieldVal);
			}
			m.put(fieldNm, fieldVal.toString());
		}
		return getTitleText(prependText, bo.getClass(), m);
	}

	public static String getTitleText(String prependText, Class element, Map<String, String> keyValueList) {
		StringBuffer titleText = new StringBuffer(prependText);
		for (String key : keyValueList.keySet()) {
			String fieldVal = keyValueList.get(key).toString();
			// Mask value if it is a secure field
			boolean viewAuthorized = KNSServiceLocator
					.getAuthorizationService()
					.isAuthorizedToViewAttribute(
							GlobalVariables.getUserSession()
									.getUniversalUser(),
							element.getName(), key);
			if (!viewAuthorized) {
				Mask displayMask = KNSServiceLocator
						.getDataDictionaryService()
						.getAttributeDisplayMask(
								element.getName(), key);
				fieldVal = displayMask.maskValue(fieldVal);
			}
			titleText.append(KNSServiceLocator.getDataDictionaryService()
					.getAttributeLabel(element, key)
					+ "=" + fieldVal.toString() + " ");
		}
		return titleText.toString();
	}

	/**
	 * 
	 * This class is an extension of HtmlData. It represents an anchor tag.
	 * 
	 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
	 * 
	 */
	public static class AnchorHtmlData extends HtmlData {
		protected String href = "";
		protected String target = "";

		/**
		 * Needed by inquiry framework
		 */
		public AnchorHtmlData() {
		}

		public AnchorHtmlData(String href, String title) {
			this.href = href;
			this.title = title;
		}

		public AnchorHtmlData(String href, String methodToCall,
				String displayText) {
			this.href = href;
			this.methodToCall = methodToCall;
			this.displayText = displayText;
		}

		/**
		 * @param href the href to set
		 */
		public void setHref(String href) {
			this.href = href;
		}

		/**
		 * 
		 * This method generates anchor tag.
		 * 
		 * @see org.kuali.rice.kns.lookup.HtmlData#constructCompleteHtmlTag()
		 */
		public String constructCompleteHtmlTag() {
			String completeHtmlTag;
			if (StringUtils.isEmpty(getHref()))
				completeHtmlTag = getDisplayText();
			else
				completeHtmlTag = getPrependDisplayText()
						+ "<a title=\""
						+ title
						+ "\""
						+ " href=\""
						+ getHref()
						+ "\""
						+ (StringUtils.isEmpty(getTarget()) ? "" : " target=\""
								+ getTarget() + "\" ") + ">" + getDisplayText()
						+ "</a>" + getAppendDisplayText();
			return completeHtmlTag;
		}

		/**
		 * @return the target
		 */
		public String getTarget() {
			return this.target;
		}

		/**
		 * @param target
		 *            the target to set
		 */
		public void setTarget(String target) {
			this.target = target;
		}

		/**
		 * @return the href
		 */
		public String getHref() {
			return this.href;
		}

		/**
		 * @return the methodToCall
		 */
		public String getMethodToCall() {
			return this.methodToCall;
		}

	}

	/**
	 * 
	 * This class is an extension of HtmlData. It represents an input tag.
	 * 
	 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
	 * 
	 */
	public static class InputHtmlData extends HtmlData {
		public static final String CHECKBOX_INPUT_TYPE = "checkbox";
		public static final String CHECKBOX_CHECKED_VALUE = "checked";

		protected String inputType = "";
		protected String src = "";
		protected String styleClass = "";
		protected String border = "0";
		protected String checked = "";
		protected String value = "";

		public InputHtmlData(String name, String inputType) {
			this.name = name;
			this.inputType = inputType;
		}

		public InputHtmlData(String name, String inputType, String src) {
			this.name = name;
			this.inputType = inputType;
			this.src = src;
		}

		/***********************************************************************
		 * 
		 * This method contructs an input tag.
		 * 
		 * @see org.kuali.rice.kns.lookup.HtmlData#constructCompleteHtmlTag()
		 */
		public String constructCompleteHtmlTag() {
			return getPrependDisplayText()
					+ "<input title=\""
					+ title
					+ "\""
					+ " name=\""
					+ getName()
					+ "\""
					+ (StringUtils.isEmpty(src) ? ""
							: " src=\"" + src + "\" ")
					+ " type=\""
					+ getInputType()
					+ "\""
					+ (StringUtils.isEmpty(value) ? ""
							: " value=\"" + value + "\" ")
					+ (StringUtils.isEmpty(checked) ? ""
							: " checked=\"" + checked + "\" ")
					+ (StringUtils.isEmpty(getStyleClass()) ? ""
							: " styleClass=\"" + getStyleClass() + "\" ")
					+ " border=\"" + getBorder() + "\"" + " value=\""
					+ getDisplayText() + "\"" + "/>" + getAppendDisplayText();
		}

		/**
		 * @return the inputType
		 */
		public String getInputType() {
			return this.inputType;
		}

		/**
		 * @return the src
		 */
		public String getSrc() {
			return this.src;
		}

		/**
		 * @return the border
		 */
		public String getBorder() {
			return this.border;
		}

		/**
		 * @param border
		 *            the border to set
		 */
		public void setBorder(String border) {
			this.border = border;
		}

		/**
		 * @return the styleClass
		 */
		public String getStyleClass() {
			return this.styleClass;
		}

		/**
		 * @param styleClass
		 *            the styleClass to set
		 */
		public void setStyleClass(String styleClass) {
			this.styleClass = styleClass;
		}

		/**
		 * @param checked the checked to set
		 */
		public void setChecked(String checked) {
			this.checked = checked;
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}

	}

}