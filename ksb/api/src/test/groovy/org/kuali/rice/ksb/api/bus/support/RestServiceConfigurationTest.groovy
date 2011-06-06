package org.kuali.rice.ksb.api.bus.support

import java.lang.reflect.Field

import javax.xml.bind.JAXBContext
import javax.xml.bind.Unmarshaller
import javax.xml.namespace.QName

import org.junit.Test
import org.kuali.rice.ksb.api.registry.ServiceRegistry
import org.kuali.rice.ksb.test.JAXBAssert

class RestServiceConfigurationTest {

	private static final QName SERVICE_NAME = new QName(APPLICATION_ID, "myRadService");
	private static final String APPLICATION_ID = "TEST";
	private static final URL ENDPOINT_URL = new URL("http://this.is.my.url");
	private static final String SERVICE_VERSION = "1.0";
	
	private static final String XML_REQUIRED_ONLY = """
<restServiceConfiguration xmlns="http://rice.kuali.org/ksb/v2_0">
	<serviceName>{TEST}myRadService</serviceName>
	<endpointUrl>http://this.is.my.url</endpointUrl>
	<applicationId>TEST</applicationId>
	<serviceVersion>1.0</serviceVersion>
	<type>REST</type>
	<queue>true</queue>
	<busSecurity>false</busSecurity>
</restServiceConfiguration>
	"""
	
	private static final String XML_WITH_RESOURCE_CLASS = """
<restServiceConfiguration xmlns="http://rice.kuali.org/ksb/v2_0">
	<serviceName>{TEST}myRadService</serviceName>
	<endpointUrl>http://this.is.my.url</endpointUrl>
	<applicationId>TEST</applicationId>
	<serviceVersion>1.0</serviceVersion>
	<type>REST</type>
	<queue>true</queue>
	<busSecurity>false</busSecurity>
	<resourceClass>org.kuali.rice.ksb.api.registry.ServiceRegistry</resourceClass>
</restServiceConfiguration>
"""
	
	private static final String XML_WITH_RESOURCE_TO_CLASSNAME_MAP = """
<restServiceConfiguration xmlns:ns2="http://rice.kuali.org/core/v2_0" xmlns="http://rice.kuali.org/ksb/v2_0">
	<serviceName>{TEST}myRadService</serviceName>
	<endpointUrl>http://this.is.my.url</endpointUrl>
	<applicationId>TEST</applicationId>
	<serviceVersion>1.0</serviceVersion>
	<type>REST</type>
	<queue>true</queue>
	<priority>5</priority>
	<retryAttempts>0</retryAttempts>
	<millisToLive>-1</millisToLive>
	<busSecurity>false</busSecurity>
	<resourceClass>org.kuali.rice.ksb.api.bus.support.Resource1</resourceClass>
	<resourceToClassNameMap>
		<ns2:entry key="Resource1">org.kuali.rice.ksb.api.bus.support.Resource1</ns2:entry>
		<ns2:entry key="/customResource2Path">org.kuali.rice.ksb.api.bus.support.Resource2</ns2:entry>
	</resourceToClassNameMap>
</restServiceConfiguration>
"""
	
	private static final String XML_WITH_FUTURE_ELEMENTS = """
<restServiceConfiguration xmlns:ns2="http://rice.kuali.org/core/v2_0" xmlns="http://rice.kuali.org/ksb/v2_0">
	<serviceName>{TEST}myRadService</serviceName>
	<endpointUrl>http://this.is.my.url</endpointUrl>
	<applicationId>TEST</applicationId>
	<serviceVersion>1.0</serviceVersion>
	<type>REST</type>
	<queue>true</queue>
	<priority>5</priority>
	<retryAttempts>0</retryAttempts>
	<millisToLive>-1</millisToLive>
	<busSecurity>false</busSecurity>
	<resourceClass>org.kuali.rice.ksb.api.bus.support.Resource1</resourceClass>
	<resourceToClassNameMap>
		<ns2:entry key="Resource1">org.kuali.rice.ksb.api.bus.support.Resource1</ns2:entry>
		<ns2:entry key="/customResource2Path">org.kuali.rice.ksb.api.bus.support.Resource2</ns2:entry>
	</resourceToClassNameMap>
	<thisIsTotallyNew brandNew="true">some content from a new version here</thisIsTotallyNew>
	<unmarshallingShouldStillEatThisJustFine/>
</restServiceConfiguration>
"""
		
	private RestServiceDefinition createBaseDefinition() {
		RestServiceDefinition definition = new RestServiceDefinition();
		definition.setServiceName(SERVICE_NAME)
		definition.setEndpointUrl(ENDPOINT_URL);
		definition.setApplicationId(APPLICATION_ID);
		definition.setServiceVersion(SERVICE_VERSION);
		return definition;
	}
	
	private RestServiceConfiguration create_requiredOnly() {
		RestServiceDefinition definition = createBaseDefinition();
		return RestServiceConfiguration.fromServiceDefinition(definition);
	}
	
	private RestServiceConfiguration create_withResourceClass() {
		RestServiceDefinition definition = createBaseDefinition();
		definition.setResourceClass(ServiceRegistry.class.getName())
		return RestServiceConfiguration.fromServiceDefinition(definition);
	}
	
	private RestServiceConfiguration create_withResourceToClassnameMap() {
		RestServiceDefinition definition = createBaseDefinition();
		List resources = new ArrayList();
		resources.add(new Resource1Impl());
		resources.add(new Resource2Impl());
		definition.setResources(resources);
		definition.validate();
		return RestServiceConfiguration.fromServiceDefinition(definition);
	}
	
	@Test
	void testXml_Marshal_Unmarshal_requiredOnly() {
		JAXBAssert.assertEqualXmlMarshalUnmarshal(create_requiredOnly(), XML_REQUIRED_ONLY, RestServiceConfiguration.class)
	}
	
	@Test
	void testXml_Marshal_Unmarshal_withResourceClass() {
		JAXBAssert.assertEqualXmlMarshalUnmarshal(create_withResourceClass(), XML_WITH_RESOURCE_CLASS, RestServiceConfiguration.class)
	}
	
	@Test
	void testXml_Marshal_Unmarshal_withResourceToClassnameMap() {
		JAXBAssert.assertEqualXmlMarshalUnmarshal(create_withResourceToClassnameMap(), XML_WITH_RESOURCE_TO_CLASSNAME_MAP, RestServiceConfiguration.class)
	}
	
	@Test
	void testXml_Unmarshal_withFutureElements() {
		JAXBContext context = JAXBContext.newInstance(RestServiceConfiguration.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		RestServiceConfiguration configuration = (RestServiceConfiguration)unmarshaller.unmarshal(new StringReader(XML_WITH_FUTURE_ELEMENTS));
		assert configuration != null;
			
		assert configuration.getServiceName() == SERVICE_NAME;
		assert configuration.getApplicationId() == APPLICATION_ID;
		assert configuration.getEndpointUrl() == ENDPOINT_URL;
		assert configuration.getServiceVersion() == SERVICE_VERSION;
		
		Field _futureElements = AbstractServiceConfiguration.class.getDeclaredField("_futureElements");
		_futureElements.setAccessible(true);
		assert !((List<?>)_futureElements.get(configuration)).isEmpty();
		
	}
	
	// TODO could use some additional tests here which test other pieces of RestServiceConfiguration
	
}
