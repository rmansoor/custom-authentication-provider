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
import org.pentaho.platform.api.mt.ITenant;
import org.pentaho.custom.authentication.provider.IPentahoRole;
import org.pentaho.custom.authentication.provider.IPentahoUser;
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

  // ~ Constructors ====================================================================================================

  public CustomUserRoleListService() {
    super();
  }

  // ~ Methods =========================================================================================================

  public List<String> getAllRoles() {
    List<IPentahoRole> roles = userRoleDao.getRoles();

    List<String> auths = new ArrayList<String>(roles.size());

    for (IPentahoRole role : roles) {
      auths.add(role.getName());
    }

    return auths;
  }

  public List<String> getAllUsers() {
    List<IPentahoUser> users = userRoleDao.getUsers();

    List<String> usernames = new ArrayList<String>();

    for (IPentahoUser user : users) {
      usernames.add(user.getUsername());
    }

    return usernames;
  }

  private List<String> getRolesForUser(String username) throws UsernameNotFoundException,
      DataAccessException {
    UserDetails user = userDetailsService.loadUserByUsername(username);
    List<String> roles = new ArrayList<String>(user.getAuthorities().length);
    for (GrantedAuthority role : user.getAuthorities()) {
      roles.add(role.getAuthority());
    }
    return roles;
  }

  private List<String> getUsersInRole(String roleName) {
    IPentahoRole role = userRoleDao.getRole(roleName);
    if (role == null) {
      return Collections.emptyList();
    }

    List<String> usernames = new ArrayList<String>();

    for (IPentahoUser user : role.getUsers()) {
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
    return getUsersInRole(role);
  }

  @Override
  public List<String> getRolesForUser( ITenant tenant, String username ) {
    // TODO Auto-generated method stub
    return getRolesForUser(username);
  }

}
