package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.kuali.rice.core.util.MaxAgeSoftReference;
import org.kuali.rice.core.util.MaxSizeMap;
import org.kuali.rice.core.util.RiceDebugUtils;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityPrivacyPreferencesInfo;
import org.kuali.rice.kim.bo.entity.dto.KimPrincipalInfo;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.role.dto.PermissionAssigneeInfo;
import org.kuali.rice.kim.bo.role.dto.ResponsibilityActionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.AuthenticationService;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.GroupUpdateService;
import org.kuali.rice.kim.service.IdentityCacheService;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.IdentityUpdateService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.springframework.beans.factory.InitializingBean;

public class IdentityManagementServiceImpl implements IdentityManagementService, InitializingBean {
	private static final Logger LOG = Logger.getLogger( IdentityManagementServiceImpl.class );
	
	private AuthenticationService authenticationService; 
	private PermissionService permissionService; 
	private ResponsibilityService responsibilityService;  
	private IdentityService identityService;
	private IdentityCacheService identityCacheService;
	private GroupService groupService;
	private GroupUpdateService groupUpdateService;
	private IdentityUpdateService identityUpdateService;
	
	
	// Max age defined in seconds
	protected int entityPrincipalCacheMaxSize = 200;
	protected int entityPrincipalCacheMaxAgeSeconds = 30;
	protected int groupCacheMaxSize = 200;
	protected int groupCacheMaxAgeSeconds = 30;
	protected int permissionCacheMaxSize = 200;
	protected int permissionCacheMaxAgeSeconds = 30;
	protected int responsibilityCacheMaxSize = 200;
	protected int responsibilityCacheMaxAgeSeconds = 30;
	
	protected MaxSizeMap<String,MaxAgeSoftReference<KimEntityDefaultInfo>> entityDefaultInfoCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<KimEntity>> entityCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<KimPrincipalInfo>> principalByIdCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<KimPrincipalInfo>> principalByNameCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<GroupInfo>> groupByIdCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<GroupInfo>> groupByNameCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<List<String>>> groupIdsForPrincipalCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<List<? extends GroupInfo>>> groupsForPrincipalCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<Boolean>> isMemberOfGroupCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<Boolean>> isGroupMemberOfGroupCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<List<String>>> groupMemberPrincipalIdsCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<Boolean>> hasPermissionCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<Boolean>> hasPermissionByTemplateCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<Boolean>> isAuthorizedCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<Boolean>> isAuthorizedByTemplateNameCache;
    protected MaxSizeMap<String,MaxAgeSoftReference<Boolean>> isPermissionDefinedForTemplateNameCache;
	
	public void afterPropertiesSet() throws Exception {
		entityDefaultInfoCache = new MaxSizeMap<String,MaxAgeSoftReference<KimEntityDefaultInfo>>( entityPrincipalCacheMaxSize );
		entityCache = new MaxSizeMap<String,MaxAgeSoftReference<KimEntity>>( entityPrincipalCacheMaxSize );
		principalByIdCache = new MaxSizeMap<String,MaxAgeSoftReference<KimPrincipalInfo>>( entityPrincipalCacheMaxSize );
		principalByNameCache = new MaxSizeMap<String,MaxAgeSoftReference<KimPrincipalInfo>>( entityPrincipalCacheMaxSize );
		groupByIdCache = new MaxSizeMap<String,MaxAgeSoftReference<GroupInfo>>( groupCacheMaxSize );
		groupByNameCache = new MaxSizeMap<String,MaxAgeSoftReference<GroupInfo>>( groupCacheMaxSize );
		groupIdsForPrincipalCache = new MaxSizeMap<String,MaxAgeSoftReference<List<String>>>( groupCacheMaxSize );
		groupsForPrincipalCache = new MaxSizeMap<String,MaxAgeSoftReference<List<? extends GroupInfo>>>( groupCacheMaxSize );
		isMemberOfGroupCache = new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( groupCacheMaxSize );
		groupMemberPrincipalIdsCache = new MaxSizeMap<String,MaxAgeSoftReference<List<String>>>( groupCacheMaxSize );
		hasPermissionCache = new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( permissionCacheMaxSize );
		hasPermissionByTemplateCache = new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( permissionCacheMaxSize );
		isPermissionDefinedForTemplateNameCache = new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( permissionCacheMaxSize );
		isAuthorizedByTemplateNameCache = new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( permissionCacheMaxSize );
		isAuthorizedCache = new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( permissionCacheMaxSize );
	}

	public void flushAllCaches() {
		flushEntityPrincipalCaches();
		flushGroupCaches();
		flushPermissionCaches();
		flushResponsibilityCaches();
	}
	
	public void flushEntityPrincipalCaches() {
		entityDefaultInfoCache.clear();
		entityCache.clear();
		principalByIdCache.clear();
		principalByNameCache.clear();
	}
	
	public void flushGroupCaches() {
		groupByIdCache.clear();
		groupByNameCache.clear();
		groupIdsForPrincipalCache.clear();
		groupsForPrincipalCache.clear();
		isMemberOfGroupCache.clear();
		groupMemberPrincipalIdsCache.clear();
	}
	
	public void flushPermissionCaches() {
		hasPermissionCache.clear();
		hasPermissionByTemplateCache.clear();
		isPermissionDefinedForTemplateNameCache.clear();
		isAuthorizedByTemplateNameCache.clear();
		isAuthorizedCache.clear();
	}

	public void flushResponsibilityCaches() {
		// nothing currently being cached
	}

	protected KimEntityDefaultInfo getEntityDefaultInfoFromCache( String entityId ) {
		MaxAgeSoftReference<KimEntityDefaultInfo> entityRef = entityDefaultInfoCache.get( "entityId="+entityId );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}

	protected KimEntityDefaultInfo getEntityDefaultInfoFromCacheByPrincipalId( String principalId ) {
		MaxAgeSoftReference<KimEntityDefaultInfo> entityRef = entityDefaultInfoCache.get( "principalId="+principalId );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}

	protected KimEntityDefaultInfo getEntityDefaultInfoFromCacheByPrincipalName( String principalName ) {
		MaxAgeSoftReference<KimEntityDefaultInfo> entityRef = entityDefaultInfoCache.get( "principalName="+principalName );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}
	
	protected KimEntity getEntityFromCache( String entityId ) {
		MaxAgeSoftReference<KimEntity> entityRef = entityCache.get( "entityId="+entityId );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}

	protected KimEntity getEntityFromCacheByPrincipalId( String principalId ) {
		MaxAgeSoftReference<KimEntity> entityRef = entityCache.get( "principalId="+principalId );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}
	
	protected KimEntity getEntityFromCacheByPrincipalName( String principalName ) {
		MaxAgeSoftReference<KimEntity> entityRef = entityCache.get( "principalName="+principalName );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}
	
	protected KimPrincipalInfo getPrincipalByIdCache( String principalId ) {
		MaxAgeSoftReference<KimPrincipalInfo> principalRef = principalByIdCache.get( principalId );
		if ( principalRef != null ) {
			return principalRef.get();
		}
		return null;
	}

	protected KimPrincipalInfo getPrincipalByNameCache( String principalName ) {
		MaxAgeSoftReference<KimPrincipalInfo> principalRef = principalByNameCache.get( principalName );
		if ( principalRef != null ) {
			return principalRef.get();
		}
		return null;
	}

	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getEntityPrivacyPreferences(java.lang.String)
	 */
	public KimEntityPrivacyPreferencesInfo getEntityPrivacyPreferences(String entityId) {
		return getIdentityService().getEntityPrivacyPreferences( entityId );
	}
	
	protected GroupInfo getGroupByIdCache( String groupId ) {
		MaxAgeSoftReference<GroupInfo> groupRef = groupByIdCache.get( groupId );
		if ( groupRef != null ) {
			return groupRef.get();
		}
		return null;
	}

	protected GroupInfo getGroupByNameCache( String groupName ) {
		MaxAgeSoftReference<GroupInfo> groupRef = groupByNameCache.get( groupName );
		if ( groupRef != null ) {
			return groupRef.get();
		}
		return null;
	}

	protected List<String> getGroupIdsForPrincipalCache( String principalId ) {
		MaxAgeSoftReference<List<String>> groupIdsRef = groupIdsForPrincipalCache.get( principalId );
		if ( groupIdsRef != null ) {
			return groupIdsRef.get();
		}
		return null;
	}
	
	protected List<? extends GroupInfo> getGroupsForPrincipalCache( String principalId ) {
		MaxAgeSoftReference<List<? extends GroupInfo>> groupsRef = groupsForPrincipalCache.get( principalId );
		if ( groupsRef != null ) {
			return groupsRef.get();
		}
		return null;
	}

	protected Boolean getIsMemberOfGroupCache( String principalId, String groupId ) {
		MaxAgeSoftReference<Boolean> isMemberRef = isMemberOfGroupCache.get( principalId + "-" + groupId );
		if ( isMemberRef != null ) {
			return isMemberRef.get();
		}
		return null;
	}

	protected Boolean getIsGroupMemberOfGroupCache( String potentialMemberId, String potentialParentId ) 
	{
		MaxAgeSoftReference<Boolean> isMemberRef = isGroupMemberOfGroupCache.get( potentialMemberId + "-" + potentialParentId );
		if ( isMemberRef != null ) {
			return isMemberRef.get();
		}
		return null;
	}
		protected List<String> getGroupMemberPrincipalIdsCache( String groupId ) {
		MaxAgeSoftReference<List<String>> memberIdsRef = groupMemberPrincipalIdsCache.get( groupId );
		if ( memberIdsRef != null ) {
			return memberIdsRef.get();
		}
		return null;
	}
	
	protected Boolean getHasPermissionCache( String key ) {
		MaxAgeSoftReference<Boolean> hasPermissionRef = hasPermissionCache.get( key );
		if ( hasPermissionRef != null ) {
			return hasPermissionRef.get(); 
		}
		return null;
	}
	
	protected Boolean getHasPermissionByTemplateCache( String key ) {
		MaxAgeSoftReference<Boolean> hasPermissionRef = hasPermissionByTemplateCache.get( key );
		if ( hasPermissionRef != null ) {
			return hasPermissionRef.get();
		}
		return null;
	}

	protected Boolean getIsAuthorizedByTemplateNameFromCache( String key ) {
		MaxAgeSoftReference<Boolean> cacheEntryRef = isAuthorizedByTemplateNameCache.get( key );
		if ( cacheEntryRef != null ) {
			return cacheEntryRef.get();
		}
		return null;
	}

	protected Boolean getIsAuthorizedFromCache( String key ) {
		MaxAgeSoftReference<Boolean> cacheEntryRef = isAuthorizedCache.get( key );
		if ( cacheEntryRef != null ) {
			return cacheEntryRef.get();
		}
		return null;
	}
	
	protected void addEntityToCache( KimEntity entity ) {
		if ( entity != null ) {
			entityCache.put( "entityId="+entity.getEntityId(), new MaxAgeSoftReference<KimEntity>( entityPrincipalCacheMaxAgeSeconds, entity ) );
			for ( KimPrincipal p : entity.getPrincipals() ) {
				entityCache.put( "principalId="+p.getPrincipalId(), new MaxAgeSoftReference<KimEntity>( entityPrincipalCacheMaxAgeSeconds, entity ) );
				entityCache.put( "principalName="+p.getPrincipalName(), new MaxAgeSoftReference<KimEntity>( entityPrincipalCacheMaxAgeSeconds, entity ) );
			}
		}
	}

	protected void addEntityDefaultInfoToCache( KimEntityDefaultInfo entity ) {
		if ( entity != null ) {
			entityDefaultInfoCache.put( "entityId="+entity.getEntityId(), new MaxAgeSoftReference<KimEntityDefaultInfo>( entityPrincipalCacheMaxAgeSeconds, entity ) );
			for ( KimPrincipal p : entity.getPrincipals() ) {
				entityDefaultInfoCache.put( "principalId="+p.getPrincipalId(), new MaxAgeSoftReference<KimEntityDefaultInfo>( entityPrincipalCacheMaxAgeSeconds, entity ) );
				entityDefaultInfoCache.put( "principalName="+p.getPrincipalName(), new MaxAgeSoftReference<KimEntityDefaultInfo>( entityPrincipalCacheMaxAgeSeconds, entity ) );
			}
		}
	}
	

	protected void addPrincipalToCache( KimPrincipalInfo principal ) {
		if ( principal != null ) {
			principalByNameCache.put( principal.getPrincipalName(), new MaxAgeSoftReference<KimPrincipalInfo>( entityPrincipalCacheMaxAgeSeconds, principal ) );
			principalByIdCache.put( principal.getPrincipalId(), new MaxAgeSoftReference<KimPrincipalInfo>( entityPrincipalCacheMaxAgeSeconds, principal ) );
		}
	}
	
	protected void addGroupToCache( GroupInfo group ) {
		if ( group != null ) {
			groupByNameCache.put( group.getGroupName(), new MaxAgeSoftReference<GroupInfo>( groupCacheMaxAgeSeconds, group ) );
			groupByIdCache.put( group.getGroupId(), new MaxAgeSoftReference<GroupInfo>( groupCacheMaxAgeSeconds, group ) );
		}
	}

	protected void addGroupIdsForPrincipalToCache( String principalId, List<String> ids ) {
		if ( ids != null ) {
			groupIdsForPrincipalCache.put( principalId, new MaxAgeSoftReference<List<String>>( groupCacheMaxAgeSeconds, ids ) );
		}
	}

	protected void addGroupsForPrincipalToCache( String principalId, List<? extends GroupInfo> groups ) {
		if ( groups != null ) {
			groupsForPrincipalCache.put( principalId, new MaxAgeSoftReference<List<? extends GroupInfo>>( groupCacheMaxAgeSeconds, groups ) );
			List<String> groupIds = new ArrayList<String>( groups.size() );
			for ( GroupInfo group : groups ) {
				groupIds.add( group.getGroupId() );
			}
			addGroupIdsForPrincipalToCache( principalId, groupIds );
		}
	}
	
	protected void addIsMemberOfGroupToCache( String principalId, String groupId, boolean member ) {
		isMemberOfGroupCache.put( principalId + "-" + groupId, new MaxAgeSoftReference<Boolean>( groupCacheMaxAgeSeconds, member ) );
	}
	
	protected void addIsGroupMemberOfGroupToCache( String potentialMemberId, String potentialParentId, boolean member ) 
	{
        isMemberOfGroupCache.put( potentialMemberId + "-" + potentialParentId, new MaxAgeSoftReference<Boolean>( groupCacheMaxAgeSeconds, member ) );
    }
	
	protected void addGroupMemberPrincipalIdsToCache( String groupId, List<String> ids ) {
		if ( ids != null ) {
			groupMemberPrincipalIdsCache.put( groupId, new MaxAgeSoftReference<List<String>>( groupCacheMaxAgeSeconds, ids ) );
		}
	}

	protected void addHasPermissionToCache( String key, boolean hasPerm ) {
		hasPermissionCache.put( key, new MaxAgeSoftReference<Boolean>( permissionCacheMaxAgeSeconds, hasPerm ) );
	}

	protected void addHasPermissionByTemplateToCache( String key, boolean hasPerm ) {
		hasPermissionByTemplateCache.put( key, new MaxAgeSoftReference<Boolean>( permissionCacheMaxAgeSeconds, hasPerm ) );
	}

	protected void addIsAuthorizedByTemplateNameToCache( String key, boolean authorized ) {
		isAuthorizedByTemplateNameCache.put( key, new MaxAgeSoftReference<Boolean>( permissionCacheMaxAgeSeconds, authorized ) );
	}

	protected void addIsAuthorizedToCache( String key, boolean authorized ) {
		isAuthorizedCache.put( key, new MaxAgeSoftReference<Boolean>( permissionCacheMaxAgeSeconds, authorized ) );
	}
	
	// AUTHENTICATION SERVICE
	
	public String getAuthenticatedPrincipalName(HttpServletRequest request) {
		return getAuthenticationService().getPrincipalName(request);
	}

    public boolean authenticationServiceValidatesPassword() {
    	return getAuthenticationService().validatePassword();
    }
    
    // AUTHORIZATION SERVICE
    
    public boolean hasPermission(String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails) {
    	if ( LOG.isDebugEnabled() ) {
    		logHasPermissionCheck("Permission", principalId, namespaceCode, permissionName, permissionDetails);
    	}
    	StringBuffer cacheKey = new StringBuffer();
    	cacheKey.append( principalId ).append(  '/' );
    	cacheKey.append( namespaceCode ).append( '-' ).append( permissionName ).append( '/' );
    	addAttributeSetToKey( permissionDetails, cacheKey );
    	String key = cacheKey.toString();
    	Boolean hasPerm = getHasPermissionCache(key);
		if (hasPerm == null) {
			hasPerm = getPermissionService().hasPermission( principalId, namespaceCode, permissionName, permissionDetails );
	    	addHasPermissionToCache(key, hasPerm);
    		if ( LOG.isDebugEnabled() ) {
    			LOG.debug( "Result: " + hasPerm );
    		}
		} else {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug( "Result Found in cache using key: " + key + "\nResult: " + hasPerm );
			}
		}
    	return hasPerm;        	
    }
    
    public boolean isAuthorized(String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	if ( qualification == null ) {
    		return hasPermission( principalId, namespaceCode, permissionName, permissionDetails );
    	}
    	if ( LOG.isDebugEnabled() ) {
    		logAuthorizationCheck("Permission", principalId, namespaceCode, permissionName, permissionDetails, qualification);
    	}
    	StringBuffer cacheKey = new StringBuffer();
    	cacheKey.append( principalId ).append(  '/' );
    	cacheKey.append( namespaceCode ).append( '-' ).append( permissionName ).append( '/' );
    	addAttributeSetToKey( permissionDetails, cacheKey );
    	cacheKey.append( '/' );
    	addAttributeSetToKey( qualification, cacheKey );
    	String key = cacheKey.toString();
    	Boolean isAuthorized = getIsAuthorizedFromCache( key );
    	if ( isAuthorized == null ) {
    		isAuthorized = getPermissionService().isAuthorized( principalId, namespaceCode, permissionName, permissionDetails, qualification );
    		addIsAuthorizedToCache( key, isAuthorized );
    		if ( LOG.isDebugEnabled() ) {
    			LOG.debug( "Result: " + isAuthorized );
    		}
		} else {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug( "Result Found in cache using key: " + key + "\nResult: " + isAuthorized );
			}
    	}
    	return isAuthorized;
    }

    public boolean hasPermissionByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails) {
    	if ( LOG.isDebugEnabled() ) {
    		logHasPermissionCheck("Perm Templ", principalId, namespaceCode, permissionTemplateName, permissionDetails);
    	}
    	StringBuffer cacheKey = new StringBuffer();
    	cacheKey.append( principalId ).append(  '/' );
    	cacheKey.append( namespaceCode ).append( '-' ).append( permissionTemplateName ).append( '/' );
    	addAttributeSetToKey( permissionDetails, cacheKey );
    	String key = cacheKey.toString();
    	Boolean hasPerm = getHasPermissionByTemplateCache(key);
		if (hasPerm == null) {
			hasPerm = getPermissionService().hasPermissionByTemplateName( principalId, namespaceCode, permissionTemplateName, permissionDetails );
	    	addHasPermissionByTemplateToCache(key, hasPerm);
    		if ( LOG.isDebugEnabled() ) {
    			LOG.debug( "Result: " + hasPerm );
    		}
		} else {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug( "Result Found in cache using key: " + key + "\nResult: " + hasPerm );
			}
		}
    	return hasPerm;        	
    }
    
    public boolean isAuthorizedByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	if ( qualification == null ) {
    		return hasPermissionByTemplateName( principalId, namespaceCode, permissionTemplateName, permissionDetails );
    	}
    	if ( LOG.isDebugEnabled() ) {
    		logAuthorizationCheck("Perm Templ", principalId, namespaceCode, permissionTemplateName, permissionDetails, qualification);
    	}
    	StringBuffer cacheKey = new StringBuffer();
    	cacheKey.append( principalId ).append(  '/' );
    	cacheKey.append( namespaceCode ).append( '-' ).append( permissionTemplateName ).append( '/' );
    	addAttributeSetToKey( permissionDetails, cacheKey );
    	cacheKey.append( '/' );
    	addAttributeSetToKey( qualification, cacheKey );
    	String key = cacheKey.toString();
    	Boolean isAuthorized = getIsAuthorizedByTemplateNameFromCache( key );
    	if ( isAuthorized == null ) {
    		isAuthorized = getPermissionService().isAuthorizedByTemplateName( principalId, namespaceCode, permissionTemplateName, permissionDetails, qualification );
    		addIsAuthorizedByTemplateNameToCache( key, isAuthorized );
    		if ( LOG.isDebugEnabled() ) {
    			LOG.debug( "Result: " + isAuthorized );
    		}
		} else {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug( "Result Found in cache using key: " + key + "\nResult: " + isAuthorized );
			}
    	}
    	return isAuthorized;
    }

	private void addAttributeSetToKey(AttributeSet attributes, StringBuffer key) {
		if ( attributes != null ) {
			for ( Map.Entry<String, String> entry : attributes.entrySet() ) {
				key.append( entry.getKey() ).append( '=' ).append( entry.getValue() ).append('|');
	    	}
		} else {
			key.append( "[null]" );
		}
	}
	
    /**
     * @see org.kuali.rice.kim.service.IdentityManagementService#getAuthorizedPermissions(java.lang.String, String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public List<? extends KimPermissionInfo> getAuthorizedPermissions(String principalId,
    		String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification) {
    	return getPermissionService().getAuthorizedPermissions( principalId, namespaceCode, permissionName, permissionDetails, qualification );
    }

    public List<? extends KimPermissionInfo> getAuthorizedPermissionsByTemplateName(String principalId,
    		String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification) {
    	return getPermissionService().getAuthorizedPermissionsByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, qualification);
    }
    
    public boolean isPermissionDefinedForTemplateName(String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails) {
    	StringBuffer key = new StringBuffer();
    	key.append( namespaceCode ).append( '-' ).append( permissionTemplateName ).append( '/' );
        addAttributeSetToKey(permissionDetails, key);
        MaxAgeSoftReference<Boolean> resultEntry = isPermissionDefinedForTemplateNameCache.get(key.toString());
        if ( resultEntry != null ) {
            Boolean result = resultEntry.get();
            if ( result != null ) {
                return result;
            }
        }
        boolean result = getPermissionService().isPermissionDefinedForTemplateName(namespaceCode, permissionTemplateName, permissionDetails);
        isPermissionDefinedForTemplateNameCache.put(key.toString(),new MaxAgeSoftReference<Boolean>( permissionCacheMaxAgeSeconds, result ));
        return result; 
    }
    
    
	public List<PermissionAssigneeInfo> getPermissionAssignees(String namespaceCode,
			String permissionName, AttributeSet permissionDetails, AttributeSet qualification) {
		return this.permissionService.getPermissionAssignees( namespaceCode, permissionName,
				permissionDetails, qualification );
	}

	public List<PermissionAssigneeInfo> getPermissionAssigneesForTemplateName(String namespaceCode,
			String permissionTemplateName, AttributeSet permissionDetails,
			AttributeSet qualification) {
		return this.permissionService.getPermissionAssigneesForTemplateName( namespaceCode,
				permissionTemplateName, permissionDetails, qualification );
	}    
    
    // GROUP SERVICE

	public boolean isMemberOfGroup(String principalId, String groupId) {
    	Boolean isMember = getIsMemberOfGroupCache(principalId, groupId);
		if (isMember != null) {
			return isMember;
		}
		isMember = getGroupService().isMemberOfGroup(principalId, groupId);
    	addIsMemberOfGroupToCache(principalId, groupId, isMember);
    	return isMember;    	
	}

	public boolean isMemberOfGroup(String principalId, String namespaceCode, String groupName) {
		GroupInfo group = getGroupByName(namespaceCode, groupName);
		if ( group == null ) {
			return false;
		}
		return isMemberOfGroup(principalId, group.getGroupId());
    }

	public boolean isGroupMemberOfGroup(String potentialMemberId, String potentialParentId)
	{
	    Boolean isMember = getIsGroupMemberOfGroupCache(potentialMemberId, potentialParentId);
	    if(isMember != null)
	    {
	        return isMember;
	    }
	    else
        {
	        isMember = getGroupService()
	                .isGroupMemberOfGroup(potentialMemberId, potentialParentId);
        }
	    addIsGroupMemberOfGroupToCache(potentialMemberId, potentialParentId, isMember);
	    return isMember;
	}
	public List<String> getGroupMemberPrincipalIds(String groupId) {
    	List<String> ids = getGroupMemberPrincipalIdsCache(groupId);
		if (ids != null) {
			return ids;
		}
		ids = getGroupService().getMemberPrincipalIds(groupId);
    	addGroupMemberPrincipalIdsToCache(groupId, ids);
    	return ids;    		
	}

	public List<String> getDirectGroupMemberPrincipalIds(String groupId) {
		return getGroupService().getDirectMemberPrincipalIds(groupId);
	}

    public List<String> getGroupIdsForPrincipal(String principalId) {
    	List<String> ids = getGroupIdsForPrincipalCache(principalId);
		if (ids != null) {
			return ids;
		}
		ids = getGroupService().getGroupIdsForPrincipal(principalId);
    	addGroupIdsForPrincipalToCache(principalId, ids);
    	return ids;    	
	}

    public List<String> getGroupIdsForPrincipal(String principalId, String namespaceCode ) {
		return getGroupService().getGroupIdsForPrincipalByNamespace(principalId, namespaceCode );
	}

    public List<? extends GroupInfo> getGroupsForPrincipal(String principalId) {
    	List<? extends GroupInfo> groups = getGroupsForPrincipalCache(principalId);
		if (groups != null) {
			return groups;
		}
		groups = getGroupService().getGroupsForPrincipal(principalId);
    	addGroupsForPrincipalToCache(principalId, groups);
    	return groups;    	
	}

    public List<? extends GroupInfo> getGroupsForPrincipal(String principalId, String namespaceCode ) {
    	List<? extends GroupInfo> groups = getGroupsForPrincipalCache(principalId + "-" + namespaceCode);
		if (groups != null) {
			return groups;
		}
		groups = getGroupService().getGroupsForPrincipalByNamespace(principalId, namespaceCode );
    	addGroupsForPrincipalToCache(principalId, groups);
    	return groups;    	
	}
    
    public List<String> getMemberGroupIds(String groupId) {
		return getGroupService().getMemberGroupIds(groupId);
	}

    public List<String> getDirectMemberGroupIds(String groupId) {
		return getGroupService().getDirectMemberGroupIds(groupId);
	}

    public GroupInfo getGroup(String groupId) {
    	GroupInfo group = getGroupByIdCache(groupId);
		if (group != null) {
			return group;
		}
		group = getGroupService().getGroupInfo(groupId);
    	addGroupToCache(group);
    	return group;
	}
    
    public GroupInfo getGroupByName(String namespaceCode, String groupName) {
    	GroupInfo group = getGroupByNameCache(namespaceCode + "-" + groupName);
		if (group != null) {
			return group;
		}
		group = getGroupService().getGroupInfoByName( namespaceCode, groupName );
    	addGroupToCache(group);
    	return group;    	
    }
    
    public List<String> getParentGroupIds(String groupId) {
		return getGroupService().getParentGroupIds( groupId );
	}

    public List<String> getDirectParentGroupIds(String groupId) {
		return getGroupService().getDirectParentGroupIds( groupId );
	}
    
    protected void clearGroupCachesForPrincipalAndGroup( String principalId, String groupId ) {
    	if ( principalId != null ) {
	    	groupIdsForPrincipalCache.remove(principalId);
	    	groupsForPrincipalCache.remove(principalId);
	    	isMemberOfGroupCache.remove(principalId + "-" + groupId);
    	} else {
    		// added or removed a group - perform a more extensive purge
    		Iterator<String> keys = isMemberOfGroupCache.keySet().iterator();
    		while ( keys.hasNext() ) {
    			String key = keys.next();
    			if ( key.endsWith("-"+groupId) ) {
    				keys.remove();
    			}
    		}
    		// NOTE: There's no good way to selectively purge the other two group caches or the permission caches which could be
    		// affected - is this necessary or do we just wait for the cache items to expire    		
    	}
    	groupMemberPrincipalIdsCache.remove(groupId);
    }
    
    
    public boolean addGroupToGroup(String childId, String parentId) {
    	clearGroupCachesForPrincipalAndGroup(null, parentId);
        return getGroupUpdateService().addGroupToGroup(childId, parentId);
    }

    public boolean addPrincipalToGroup(String principalId, String groupId) {
    	clearGroupCachesForPrincipalAndGroup(principalId, groupId);
        return getGroupUpdateService().addPrincipalToGroup(principalId, groupId);
    }

    public boolean removeGroupFromGroup(String childId, String parentId) {
    	clearGroupCachesForPrincipalAndGroup(null, parentId);
        return getGroupUpdateService().removeGroupFromGroup(childId, parentId);
    }

    public boolean removePrincipalFromGroup(String principalId, String groupId) {
    	clearGroupCachesForPrincipalAndGroup(principalId, groupId);
        return getGroupUpdateService().removePrincipalFromGroup(principalId, groupId);
    }
    
    /**
	 * This delegate method ...
	 * 
	 * @param groupInfo
	 * @return
	 * @see org.kuali.rice.kim.service.GroupUpdateService#createGroup(org.kuali.rice.kim.bo.group.dto.GroupInfo)
	 */
	public GroupInfo createGroup(GroupInfo groupInfo) {
    	clearGroupCachesForPrincipalAndGroup(null,groupInfo.getGroupId());
		return getGroupUpdateService().createGroup(groupInfo);
	}

	/**
	 * This delegate method ...
	 * 
	 * @param groupId
	 * @see org.kuali.rice.kim.service.GroupUpdateService#removeAllGroupMembers(java.lang.String)
	 */
	public void removeAllGroupMembers(String groupId) {
    	clearGroupCachesForPrincipalAndGroup(null, groupId);
		getGroupUpdateService().removeAllGroupMembers(groupId);
	}

	/**
	 * This delegate method ...
	 * 
	 * @param groupId
	 * @param groupInfo
	 * @return
	 * @see org.kuali.rice.kim.service.GroupUpdateService#updateGroup(java.lang.String, org.kuali.rice.kim.bo.group.dto.GroupInfo)
	 */
	public GroupInfo updateGroup(String groupId, GroupInfo groupInfo) {
    	clearGroupCachesForPrincipalAndGroup(null, groupId);
		return getGroupUpdateService().updateGroup(groupId, groupInfo);
	}

    
    // IDENTITY SERVICE

	public KimPrincipalInfo getPrincipal(String principalId) {
    	KimPrincipalInfo principal = getPrincipalByIdCache(principalId);
		if (principal != null) {
			return principal;
		}
		principal = getIdentityService().getPrincipal(principalId);
    	addPrincipalToCache(principal);
    	return principal;
	}
    
    public KimPrincipalInfo getPrincipalByPrincipalName(String principalName) {
    	KimPrincipalInfo principal = getPrincipalByNameCache(principalName);
		if (principal != null) {
			return principal;
		}
		principal = getIdentityService().getPrincipalByPrincipalName(principalName);
    	addPrincipalToCache(principal);
    	return principal;
    }
    
    /**
     * @see org.kuali.rice.kim.service.IdentityManagementService#getPrincipalByPrincipalNameAndPassword(java.lang.String, java.lang.String)
     */
    public KimPrincipalInfo getPrincipalByPrincipalNameAndPassword(String principalName, String password) {
    	// TODO: cache this?
    	return getIdentityService().getPrincipalByPrincipalNameAndPassword( principalName, password );
    }
	   
    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.IdentityManagementService#getEntityDefaultInfo(java.lang.String)
     */
    public KimEntityDefaultInfo getEntityDefaultInfo(String entityId) {
    	KimEntityDefaultInfo entity = getEntityDefaultInfoFromCache( entityId );
    	if ( entity == null ) {
    		entity = getIdentityService().getEntityDefaultInfo(entityId);
	    	if ( entity == null ) {
	    		entity = getIdentityCacheService().getEntityDefaultInfoFromPersistentCache( entityId );
	    	} else {
				getIdentityCacheService().saveDefaultInfoToCache(entity);
	    	}
    		addEntityDefaultInfoToCache( entity );
    	}
    	return entity;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.IdentityManagementService#getEntityDefaultInfoByPrincipalId(java.lang.String)
     */
    public KimEntityDefaultInfo getEntityDefaultInfoByPrincipalId(
    		String principalId) {
    	KimEntityDefaultInfo entity = getEntityDefaultInfoFromCacheByPrincipalId( principalId );
    	if ( entity == null ) {
	    	entity = getIdentityService().getEntityDefaultInfoByPrincipalId(principalId);
	    	if ( entity == null ) {
	    		entity = getIdentityCacheService().getEntityDefaultInfoFromPersistentCacheByPrincipalId( principalId );
	    	} else {
				getIdentityCacheService().saveDefaultInfoToCache(entity);
	    	}
			addEntityDefaultInfoToCache( entity );
    	}
    	return entity;
    }
    
    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.IdentityManagementService#getEntityDefaultInfoByPrincipalName(java.lang.String)
     */
    public KimEntityDefaultInfo getEntityDefaultInfoByPrincipalName(
    		String principalName) {
    	KimEntityDefaultInfo entity = getEntityDefaultInfoFromCacheByPrincipalName( principalName );
    	if ( entity == null ) {
	    	entity = getIdentityService().getEntityDefaultInfoByPrincipalName(principalName);
	    	if ( entity == null ) {
	    		entity = getIdentityCacheService().getEntityDefaultInfoFromPersistentCacheByPrincipalName( principalName );
	    	} else {
				getIdentityCacheService().saveDefaultInfoToCache(entity);
	    	}
			addEntityDefaultInfoToCache( entity );
    	}
    	return entity;
    }
    
    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.IdentityManagementService#lookupEntityDefaultInfo(Map, boolean)
     */
    public List<? extends KimEntityDefaultInfo> lookupEntityDefaultInfo(
    		Map<String, String> searchCriteria, boolean unbounded) {
    	return getIdentityService().lookupEntityDefaultInfo(searchCriteria, unbounded);
    }
    
    /**
     * @see org.kuali.rice.kim.service.IdentityManagementService#getMatchingEntityCount(java.util.Map)
     */
    public int getMatchingEntityCount(Map<String,String> searchCriteria) {
    	return getIdentityService().getMatchingEntityCount( searchCriteria );
    }
    
	// OTHER METHODS
	
	
	
	public AuthenticationService getAuthenticationService() {
		if ( authenticationService == null ) {
			authenticationService = KIMServiceLocator.getAuthenticationService();
		}
		return authenticationService;
	}

	public IdentityService getIdentityService() {
		if ( identityService == null ) {
			identityService = KIMServiceLocator.getIdentityService();
		}
		return identityService;
	}

	public IdentityCacheService getIdentityCacheService() {
		if ( identityCacheService == null ) {
			identityCacheService = KIMServiceLocator.getIdentityCacheService();
		}
		return identityCacheService;
	}
	
	public GroupService getGroupService() {
		if ( groupService == null ) {
			groupService = KIMServiceLocator.getGroupService();
		}
		return groupService;
	}

	public PermissionService getPermissionService() {
		if ( permissionService == null ) {
			permissionService = KIMServiceLocator.getPermissionService();
		}
		return permissionService;
	}

	public ResponsibilityService getResponsibilityService() {
		if ( responsibilityService == null ) {
			responsibilityService = KIMServiceLocator.getResponsibilityService();
		}
		return responsibilityService;
	}
	
    // ----------------------
    // Responsibility Methods
    // ----------------------

	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getResponsibility(java.lang.String)
	 */
	public KimResponsibilityInfo getResponsibility(String responsibilityId) {
		return getResponsibilityService().getResponsibility( responsibilityId );
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#hasResponsibility(java.lang.String, String, java.lang.String, AttributeSet, AttributeSet)
	 */
	public boolean hasResponsibility(String principalId, String namespaceCode,
			String responsibilityName, AttributeSet qualification,
			AttributeSet responsibilityDetails) {
		return getResponsibilityService().hasResponsibility( principalId, namespaceCode, responsibilityName, qualification, responsibilityDetails );
	}

	public List<? extends KimResponsibilityInfo> getResponsibilitiesByName( String namespaceCode, String responsibilityName) {
		return getResponsibilityService().getResponsibilitiesByName( namespaceCode, responsibilityName );
	}
	
	public List<ResponsibilityActionInfo> getResponsibilityActions( String namespaceCode, String responsibilityName,
    		AttributeSet qualification, AttributeSet responsibilityDetails) {
		return getResponsibilityService().getResponsibilityActions( namespaceCode, responsibilityName, qualification, responsibilityDetails );
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getResponsibilityActionsByTemplateName(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public List<ResponsibilityActionInfo> getResponsibilityActionsByTemplateName(
			String namespaceCode, String responsibilityTemplateName,
			AttributeSet qualification, AttributeSet responsibilityDetails) {
		return getResponsibilityService().getResponsibilityActionsByTemplateName(namespaceCode, responsibilityTemplateName, qualification, responsibilityDetails);
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.IdentityManagementService#hasResponsibilityByTemplateName(java.lang.String, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public boolean hasResponsibilityByTemplateName(String principalId,
			String namespaceCode, String responsibilityTemplateName,
			AttributeSet qualification, AttributeSet responsibilityDetails) {
		return getResponsibilityService().hasResponsibilityByTemplateName(principalId, namespaceCode, responsibilityTemplateName, qualification, responsibilityDetails);
	}

	public void setEntityPrincipalCacheMaxSize(int entityPrincipalCacheMaxSize) {
		this.entityPrincipalCacheMaxSize = entityPrincipalCacheMaxSize;
	}

	public void setEntityPrincipalCacheMaxAgeSeconds(int entityPrincipalCacheMaxAge) {
		this.entityPrincipalCacheMaxAgeSeconds = entityPrincipalCacheMaxAge;
	}

	public void setGroupCacheMaxSize(int groupCacheMaxSize) {
		this.groupCacheMaxSize = groupCacheMaxSize;
	}

	public void setGroupCacheMaxAgeSeconds(int groupCacheMaxAge) {
		this.groupCacheMaxAgeSeconds = groupCacheMaxAge;
	}

	public void setPermissionCacheMaxSize(int permissionCacheMaxSize) {
		this.permissionCacheMaxSize = permissionCacheMaxSize;
	}

	public void setPermissionCacheMaxAgeSeconds(int permissionCacheMaxAge) {
		this.permissionCacheMaxAgeSeconds = permissionCacheMaxAge;
	}

	public void setResponsibilityCacheMaxSize(int responsibilityCacheMaxSize) {
		this.responsibilityCacheMaxSize = responsibilityCacheMaxSize;
	}

	public void setResponsibilityCacheMaxAgeSeconds(int responsibilityCacheMaxAge) {
		this.responsibilityCacheMaxAgeSeconds = responsibilityCacheMaxAge;
	}
	
    protected void logAuthorizationCheck(String checkType, String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
		StringBuffer sb = new StringBuffer();
		sb.append(  '\n' );
		sb.append( "Is AuthZ for " ).append( checkType ).append( ": " ).append( namespaceCode ).append( "/" ).append( permissionName ).append( '\n' );
		sb.append( "             Principal:  " ).append( principalId );
		if ( principalId != null ) {
			KimPrincipalInfo principal = getPrincipal( principalId );
			if ( principal != null ) {
				sb.append( " (" ).append( principal.getPrincipalName() ).append( ')' );
			}
		}
		sb.append( '\n' );
		sb.append( "             Details:\n" );
		if ( permissionDetails != null ) {
			sb.append( permissionDetails.formattedDump( 25 ) );
		} else {
			sb.append( "                         [null]\n" );
		}
		sb.append( "             Qualifiers:\n" );
		if ( qualification != null ) {
			sb.append( qualification.formattedDump( 25 ) );
		} else {
			sb.append( "                         [null]\n" );
		}
		LOG.debug( sb.append( RiceDebugUtils.getTruncatedStackTrace(true) ).toString() );
    }

    protected void logHasPermissionCheck(String checkType, String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails ) {
		StringBuffer sb = new StringBuffer();
		sb.append(  '\n' );
		sb.append( "Has Perm for " ).append( checkType ).append( ": " ).append( namespaceCode ).append( "/" ).append( permissionName ).append( '\n' );
		sb.append( "             Principal:  " ).append( principalId );
		if ( principalId != null ) {
			KimPrincipalInfo principal = getPrincipal( principalId );
			if ( principal != null ) {
				sb.append( " (" ).append( principal.getPrincipalName() ).append( ')' );
			}
		}
		sb.append(  '\n' );
		sb.append( "             Details:\n" );
		if ( permissionDetails != null ) {
			sb.append( permissionDetails.formattedDump( 25 ) );
		} else {
			sb.append( "                         [null]\n" );
		}
		LOG.debug( sb.append( RiceDebugUtils.getTruncatedStackTrace(true) ).toString() );
    }

	public GroupUpdateService getGroupUpdateService() {
		try {
			if ( groupUpdateService == null ) {
				groupUpdateService = KIMServiceLocator.getGroupUpdateService();
				if ( groupUpdateService == null ) {
					throw new UnsupportedOperationException( "null returned for GroupUpdateService, unable to update group data");
				}
			}
		} catch ( Exception ex ) {
			throw new UnsupportedOperationException( "unable to obtain a GroupUpdateService, unable to update group data", ex);
		}
		return groupUpdateService;
	}

	public IdentityUpdateService getIdentityUpdateService() {
		try {
			if ( identityUpdateService == null ) {
				identityUpdateService = KIMServiceLocator.getIdentityUpdateService();
				if ( identityUpdateService == null ) {
					throw new UnsupportedOperationException( "null returned for IdentityUpdateService, unable to update identity data");
				}
			}
		} catch ( Exception ex ) {
			throw new UnsupportedOperationException( "unable to obtain an IdentityUpdateService, unable to update identity data", ex);
		}
		return identityUpdateService;
	}
 	
}
