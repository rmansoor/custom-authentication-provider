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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.pentaho.platform.authentication.hibernate.IRole;
import org.pentaho.platform.authentication.hibernate.IUser;
import org.pentaho.platform.authentication.hibernate.IUserRoleDao;
import org.pentaho.platform.authentication.hibernate.UncategorizedUserRoleDaoException;
import org.pentaho.platform.api.mt.ITenantedPrincipleNameResolver;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.User;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;


/**
 * A <code>UserDetailsService</code> that delegates to an {@link IUserRoleDao} to load users by username.
 * 
 */
public class CustomUserDetailsService implements UserDetailsService {

  // ~ Static fields/initializers ====================================================================================== 

  // ~ Instance fields =================================================================================================

  private IUserRoleDao userRoleDao;
  
  /**
   * A default role which will be assigned to all authenticated users if set
   */
  private GrantedAuthority defaultRole;
  
  private ITenantedPrincipleNameResolver userNameUtils;
  
  // ~ Constructors ====================================================================================================

  // ~ Methods =========================================================================================================

  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
    final boolean ACCOUNT_NON_EXPIRED = true;
    final boolean CREDS_NON_EXPIRED = true;
    final boolean ACCOUNT_NON_LOCKED = true;

    // Retrieve the user from the custom authentication system
    IUser user;
    try {
      user = userRoleDao.getUser(getUserNameUtils().getPrincipleName( username));
    } catch (UncategorizedUserRoleDaoException e) {
      throw new UserDetailsException("Unable to get the user role dao"); //$NON-NLS-1$
    }

    // If the user is null, throw a NameNotFoundException
    if (user == null) {
      throw new UsernameNotFoundException("Username [ " + getUserNameUtils().getPrincipleName( username) + "] not found"); //$NON-NLS-1$
    } else {
      // convert IUser to a UserDetails instance
      int authsSize = user.getRoles() != null ? user.getRoles().size() : 0;
      GrantedAuthority[] auths = new GrantedAuthority[authsSize];
      int i = 0;
      for (IRole role : user.getRoles()) {
        auths[i++] = new GrantedAuthorityImpl(role.getName());
      }

      List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(Arrays.asList(auths));
      
      if (authorities.size() == 0) {
        throw new UsernameNotFoundException("User [ " + getUserNameUtils().getPrincipleName( username) + "] does not have any role"); //$NON-NLS-1$
      }
      
      // Add default role to all authenticating users
      if ( getDefaultRole() != null && !authorities.contains( defaultRole ) ) {
        authorities.add( defaultRole );
      }

      GrantedAuthority[] arrayAuths = authorities.toArray(new GrantedAuthority[authorities.size()]);

      return new User(user.getUsername(), user.getPassword(), user.isEnabled(), ACCOUNT_NON_EXPIRED, CREDS_NON_EXPIRED,
          ACCOUNT_NON_LOCKED, arrayAuths);
    }
  }

  
  public void setUserRoleDao(IUserRoleDao userRoleDao) {
    this.userRoleDao = userRoleDao;
  }


  public ITenantedPrincipleNameResolver getUserNameUtils() {
	if (userNameUtils == null) {
	  userNameUtils = PentahoSystem.get( ITenantedPrincipleNameResolver.class, "tenantedUserNameUtils", null );
    }
    return userNameUtils;
  }

  public GrantedAuthority getDefaultRole() {
	  if (defaultRole == null) {
		  String defaultRoleAsString =  PentahoSystem.get( String.class, "defaultRole", null ); 
		  if(defaultRoleAsString != null && defaultRoleAsString.length() > 0) {
			  this.defaultRole = new  GrantedAuthorityImpl(defaultRoleAsString);  
		  }
	  }
	  return defaultRole;
  }
  /**
   * A data access exception specific to a <code>IUserRoleDao</code>-based <code>UserDetailsService</code>.
   */
  protected class UserDetailsException extends DataAccessException {

    private static final long serialVersionUID = -3598806635515478946L;

    public UserDetailsException(String msg) {
      super(msg);
    }

    public UserDetailsException(String msg, Throwable cause) {
      super(msg, cause);
    }

  }


}
