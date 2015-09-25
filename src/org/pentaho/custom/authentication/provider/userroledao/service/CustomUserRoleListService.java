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
package org.pentaho.custom.authentication.provider.userroledao.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pentaho.platform.api.engine.IUserRoleListService;
import org.pentaho.platform.api.engine.security.IAuthenticationRoleMapper;
import org.pentaho.platform.api.mt.ITenant;
import org.pentaho.platform.api.mt.ITenantedPrincipleNameResolver;
import org.pentaho.custom.authentication.provider.IRole;
import org.pentaho.custom.authentication.provider.IUser;
import org.pentaho.custom.authentication.provider.IUserRoleDao;
import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

/**
 * An {@link IUserRoleListService} that delegates to an {@link IUserRoleDao}.
 * 
 * @author mlowery
 */
public class CustomUserRoleListService implements IUserRoleListService {

  // ~ Static fields/initializers ====================================================================================== 

  // ~ Instance fields =================================================================================================

  private IUserRoleDao userRoleDao;

  private UserDetailsService userDetailsService;
  
  private ITenantedPrincipleNameResolver userNameUtils;

  private ITenantedPrincipleNameResolver roleNameUtils;

  private IAuthenticationRoleMapper roleMapper;

  // ~ Constructors ====================================================================================================

  public CustomUserRoleListService() {
    super();
  }

  // ~ Methods =========================================================================================================


  public List<String> getAllRoles() {
    List<IRole> roles = userRoleDao.getRoles();

    List<String> auths = new ArrayList<String>(roles.size());

    for ( IRole role : roles ) {
      if ( roleMapper != null ) {
        auths.add( roleMapper.toPentahoRole( role.getName() ) );
      } else {
        auths.add( role.getName() );
      }
    }
    return auths;
  }

  public List<String> getAllUsers() {
    List<IUser> users = userRoleDao.getUsers();

    List<String> usernames = new ArrayList<String>();

    for (IUser user : users) {
      usernames.add(user.getUsername());
    }

    return usernames;
  }

  private List<String> getRolesForUser(String username) throws UsernameNotFoundException,
      DataAccessException {
    UserDetails user = userDetailsService.loadUserByUsername(userNameUtils.getPrincipleName( username ));
    List<String> roles = new ArrayList<String>(user.getAuthorities().length);
    for (GrantedAuthority role : user.getAuthorities()) {
      if ( roleMapper != null ) {
        roles.add( roleMapper.toPentahoRole( role.getAuthority() ) );
      } else {
        roles.add( role.getAuthority() );
      }
    }
    return roles;
  }

  private List<String> getUsersInRole(String roleName) {
    String updateRole = roleNameUtils.getPrincipleName( roleName );
    IRole role = userRoleDao.getRole(updateRole);
    if (role == null) {
      return Collections.emptyList();
    }

    List<String> usernames = new ArrayList<String>();

    for (IUser user : role.getUsers()) {
      usernames.add(user.getUsername());
    }

    return usernames;
  }

  public void setUserRoleDao(IUserRoleDao userRoleDao) {
    this.userRoleDao = userRoleDao;
  }

  public void setUserDetailsService(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Override
  public List<String> getSystemRoles() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<String> getAllRoles( ITenant tenant ) {
    // TODO Auto-generated method stub
    return getAllRoles();
  }

  @Override
  public List<String> getAllUsers( ITenant tenant ) {
    // TODO Auto-generated method stub
    return getAllUsers();
  }

  @Override
  public List<String> getUsersInRole( ITenant tenant, String role ) {
    // TODO Auto-generated method stub
    return getUsersInRole(roleNameUtils.getPrincipleName(role));
  }

  @Override
  public List<String> getRolesForUser( ITenant tenant, String username ) {
    // TODO Auto-generated method stub
    return getRolesForUser(userNameUtils.getPrincipleName(username));
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

  public IAuthenticationRoleMapper getRoleMapper() {
    return roleMapper;
  }

  public void setRoleMapper( IAuthenticationRoleMapper roleMapper ) {
    this.roleMapper = roleMapper;
  }
}
