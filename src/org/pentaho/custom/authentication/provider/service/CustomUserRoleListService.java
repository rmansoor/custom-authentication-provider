/*!
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
 * Copyright (c) 2002-2015 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.custom.authentication.provider.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.pentaho.platform.api.engine.IUserRoleListService;
import org.pentaho.platform.api.mt.ITenant;
import org.pentaho.platform.api.mt.ITenantedPrincipleNameResolver;
import org.pentaho.platform.authentication.hibernate.CustomRole;
import org.pentaho.platform.authentication.hibernate.IRole;
import org.pentaho.platform.authentication.hibernate.IUser;
import org.pentaho.platform.authentication.hibernate.IUserRoleDao;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.springframework.security.GrantedAuthorityImpl;

/**
 * An {@link IUserRoleListService} that delegates to an {@link IUserRoleDao}.
 * 
 * 
 */
public class CustomUserRoleListService implements IUserRoleListService {

  // ~ Static fields/initializers ======================================================================================

  // ~ Instance fields =================================================================================================

  private IUserRoleDao userRoleDao;

  private String defaultRole;

  private ITenantedPrincipleNameResolver userNameUtils;

  private ITenantedPrincipleNameResolver roleNameUtils;

  // ~ Constructors ====================================================================================================

  public CustomUserRoleListService() {
    super();
  }

  // ~ Methods =========================================================================================================

  @Override
  public List<String> getAllRoles() {
    return getAllRoles( null );
  }

  @Override
  public List<String> getAllUsers() {
    return getAllUsers( null );
  }

  @Override
  public List<String> getSystemRoles() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<String> getAllRoles( ITenant tenant ) {
    List<IRole> roles = userRoleDao.getRoles();

    List<String> auths = new ArrayList<String>( roles.size() );

    for ( IRole role : roles ) {
      auths.add( role.getName() );
    }

    return auths;
  }

  @Override
  public List<String> getAllUsers( ITenant tenant ) {
    List<IUser> users = userRoleDao.getUsers();

    List<String> usernames = new ArrayList<String>();

    for ( IUser user : users ) {
      usernames.add( user.getUsername() );
    }

    return usernames;
  }

  @Override
  public List<String> getUsersInRole( ITenant tenant, String roleName ) {

    String updateRole = roleNameUtils.getPrincipleName( roleName );
    IRole role = userRoleDao.getRole( updateRole );
    if ( role == null ) {
      return Collections.emptyList();
    }

    List<String> usernames = new ArrayList<String>();

    for ( IUser user : role.getUsers() ) {
      usernames.add( user.getUsername() );
    }

    return usernames;

  }

  @Override
  public List<String> getRolesForUser( ITenant tenant, String username ) {
    IUser user = userRoleDao.getUser( username );
    // If no user found return null
    if ( user == null ) {
      return null;
    }

    // Retrieve the user from the customer authentication provider
    Set<IRole> roleSet = user.getRoles();

    // Add the default role to the list of roles retrieved from the user
    if ( defaultRole != null && !roleSet.contains( defaultRole ) ) {
      roleSet.add( new CustomRole( defaultRole ) );
    }
    List<String> roles = new ArrayList<String>( roleSet.size() );
    for ( IRole role : roleSet ) {
      roles.add( role.getName() );
    }
    return roles;
  }

  public void setUserRoleDao( IUserRoleDao userRoleDao ) {
    this.userRoleDao = userRoleDao;
  }

  public String getDefaultRole() {
    if (defaultRole == null) {
      defaultRole = PentahoSystem.get( String.class, "defaultRole", null ); 
    }
    return defaultRole;
  }

  public ITenantedPrincipleNameResolver getUserNameUtils() {
    if ( userNameUtils == null ) {
      userNameUtils =
          PentahoSystem.get( ITenantedPrincipleNameResolver.class, "tenantedUserNameUtils", null );
    }
    return userNameUtils;
  }

  public ITenantedPrincipleNameResolver getRoleNameUtils() {
    if ( roleNameUtils == null ) {
      roleNameUtils =
          PentahoSystem.get( ITenantedPrincipleNameResolver.class, "tenantedRoleNameUtils", null );
    }
    return roleNameUtils;
  }

}
