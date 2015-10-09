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
import java.util.List;

import org.pentaho.platform.authentication.hibernate.IRole;
import org.pentaho.platform.authentication.hibernate.IUser;
import org.pentaho.platform.authentication.hibernate.IUserRoleDao;
import org.pentaho.platform.authentication.hibernate.UncategorizedUserRoleDaoException;
import org.pentaho.platform.api.engine.security.IAuthenticationRoleMapper;
import org.pentaho.platform.api.mt.ITenantedPrincipleNameResolver;
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
  
  private IAuthenticationRoleMapper roleMapper;
  
  // ~ Constructors ====================================================================================================

  // ~ Methods =========================================================================================================

  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
    final boolean ACCOUNT_NON_EXPIRED = true;
    final boolean CREDS_NON_EXPIRED = true;
    final boolean ACCOUNT_NON_LOCKED = true;

    // Retrieve the user from the authentication system
    
    IUser user;
    try {
      user = userRoleDao.getUser(getUserNameUtils().getPrincipleName( username));
    } catch (UncategorizedUserRoleDaoException e) {
      throw new UserDetailsException("Unable to get the user role dao"); //$NON-NLS-1$
    }

    // Check if the user is null then throw a UsernameNotFoundException 
    if (user == null) {
      throw new UsernameNotFoundException("Username [ " + getUserNameUtils().getPrincipleName( username) + "] not found"); //$NON-NLS-1$
    } else {
      // Convert the IRole to GrantedAuthority
      int authsSize = user.getRoles() != null ? user.getRoles().size() : 0;
      GrantedAuthority[] auths = new GrantedAuthority[authsSize];
      int i = 0;
      for (IRole role : user.getRoles()) {
        auths[i++] = new GrantedAuthorityImpl(role.getName());
      }

      List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(Arrays.asList(auths));
      
      // Check if user has no roles. In this case, the user is not going to be able to do much
      if (authorities.size() == 0) {
        throw new UsernameNotFoundException("User [ " + getUserNameUtils().getPrincipleName( username) + "] does not have any role"); //$NON-NLS-1$
      }
      
      // This role will be added to all user's roles
      if ( defaultRole != null && !authorities.contains( defaultRole ) ) {
        authorities.add( defaultRole );
      }
      
      // also add roles mapped to pentaho security roles if available
      if ( roleMapper != null ) {
        List<GrantedAuthority> currentAuthorities = new ArrayList<GrantedAuthority>();
        currentAuthorities.addAll( authorities );

        for ( GrantedAuthority role : currentAuthorities ) {
          GrantedAuthority mappedRole = new GrantedAuthorityImpl( roleMapper.toPentahoRole( role.getAuthority() ) );
          if ( !authorities.contains( mappedRole ) ) {
            authorities.add( mappedRole );
          }
        }
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
    return userNameUtils;
  }

  public void setUserNameUtils( ITenantedPrincipleNameResolver userNameUtils ) {
    this.userNameUtils = userNameUtils;
  }  
  
  /**
   * The default role which will be assigned to all users.
   *
   * @param defaultRole the role name, including any desired prefix.
   */
  public void setDefaultRole(String defaultRole) {
      this.defaultRole = new GrantedAuthorityImpl(defaultRole);
  }
  
  public void setRoleMapper( IAuthenticationRoleMapper roleMapper ) {
    this.roleMapper = roleMapper;
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
