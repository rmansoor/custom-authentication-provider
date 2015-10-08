/*
 * This program is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software 
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this 
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html 
 * or from the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright 2007 - 2009 Pentaho Corporation.  All rights reserved.
 *
*/
package org.pentaho.custom.authentication.provider.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.pentaho.platform.api.engine.IUserRoleListService;
import org.pentaho.platform.api.engine.security.IAuthenticationRoleMapper;
import org.pentaho.platform.api.mt.ITenant;
import org.pentaho.platform.api.mt.ITenantedPrincipleNameResolver;
import org.pentaho.platform.authentication.hibernate.CustomRole;
import org.pentaho.platform.authentication.hibernate.IRole;
import org.pentaho.platform.authentication.hibernate.IUser;
import org.pentaho.platform.authentication.hibernate.IUserRoleDao;

/**
 * An {@link IUserRoleListService} that delegates to an {@link IUserRoleDao}.
 * 
 */
public class CustomUserRoleListService implements IUserRoleListService {

  // ~ Static fields/initializers ====================================================================================== 

  // ~ Instance fields =================================================================================================

  private IUserRoleDao userRoleDao;

  private String defaultRole;
  
  private ITenantedPrincipleNameResolver userNameUtils;

  private ITenantedPrincipleNameResolver roleNameUtils;
  
  private IAuthenticationRoleMapper roleMapper;

  // ~ Constructors ====================================================================================================

  public CustomUserRoleListService() {
    super();
  }

  // ~ Methods =========================================================================================================


  public List<String> getAllRoles() {
    return getAllRoles(null);
  }

  public List<String> getAllUsers() {
    return getAllUsers(null);
  }

  @Override
  public List<String> getSystemRoles() {
    return null;
  }

  @Override
  public List<String> getAllRoles( ITenant tenant ) {
    List<IRole> roles = userRoleDao.getRoles();

    List<String> auths = new ArrayList<String>(roles.size());

    for ( IRole role : roles ) {
      // If the roleMapper exists than convert all custom role to Pentaho specific roles
      if ( roleMapper != null ) {
        auths.add( roleMapper.toPentahoRole( role.getName() ) );
      } else {
        auths.add( role.getName() );
      }
    }
    return auths;
  }

  @Override
  public List<String> getAllUsers( ITenant tenant ) {
    List<IUser> users = userRoleDao.getUsers();

    List<String> usernames = new ArrayList<String>();

    for (IUser user : users) {
      usernames.add(user.getUsername());
    }

    return usernames;
  }

  @Override
  public List<String> getUsersInRole( ITenant tenant, String roleName ) {

	// Parse the role name from the tenanted role
    String updateRole = roleNameUtils.getPrincipleName( roleName );

    // Check if the role exist in the custom authentication provider
    IRole role = userRoleDao.getRole(updateRole);
    if (role == null) {
      return Collections.emptyList();
    }

    List<String> usernames = new ArrayList<String>();

    // If role exists than get all the users
    for (IUser user : role.getUsers()) {
      usernames.add(user.getUsername());
    }

    return usernames;

  }

  @Override
  public List<String> getRolesForUser( ITenant tenant, String username ) {
	IUser user = userRoleDao.getUser(username);
	
	// If no user found return null
	if ( user == null ) {
		return null;
	}
	
	// Retrieve the user from the customer authentication provider
	Set<IRole> roleSet = user.getRoles();

	// Add the default role to the list of roles retrieved from the user
    if ( defaultRole != null && !roleSet.contains( defaultRole ) ) {
    	roleSet.add( new CustomRole(defaultRole) );
    }
	
    // Now convert all the custom role to pentaho specific roles
    
    List<String> roles = new ArrayList<String>(roleSet.size());
    for (IRole role : roleSet) {
      if ( roleMapper != null ) {
        roles.add( roleMapper.toPentahoRole( role.getName() ) );
      } else {
        roles.add( role.getName() );
      }
    }
    return roles;
  }

  public void setUserRoleDao(IUserRoleDao userRoleDao) {
    this.userRoleDao = userRoleDao;
  }

  public void setDefaultRole(String defaultRole) {
      this.defaultRole = defaultRole;
  }

  public ITenantedPrincipleNameResolver getUserNameUtils() {
    return userNameUtils;
  }

  public void setUserNameUtils( ITenantedPrincipleNameResolver userNameUtils ) {
    this.userNameUtils = userNameUtils;
  }

  public ITenantedPrincipleNameResolver getRoleNameUtils() {
    return roleNameUtils;
  }

  public void setRoleNameUtils( ITenantedPrincipleNameResolver roleNameUtils ) {
    this.roleNameUtils = roleNameUtils;
  }
  
  public void setRoleMapper( IAuthenticationRoleMapper roleMapper ) {
    this.roleMapper = roleMapper;
  }
}
