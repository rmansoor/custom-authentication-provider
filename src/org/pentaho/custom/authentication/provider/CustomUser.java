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
package org.pentaho.custom.authentication.provider;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A user of the Pentaho platform. Contains a set of roles for which this user is a member.
 * 
 * <p>Note that roles are not considered during equals comparisons and hashCode calculations. This is because instances 
 * are sometimes stored in Java collections. The roles set is mutable and we don't want two users that have the same 
 * username but different roles in the same set.</p>
 * 
 * @see CustomRole
 * @author mlowery
 */
public class CustomUser implements IUser {

  // ~ Static fields/initializers ====================================================================================== 

  private static final long serialVersionUID = 3647003745944124252L;

  private static final String FIELD_ENABLED = "enabled"; //$NON-NLS-1$

  private static final String PASSWORD_MASK = "[PROTECTED]"; //$NON-NLS-1$

  private static final String FIELD_PASSWORD = "password"; //$NON-NLS-1$

  private static final String FIELD_USERNAME = "username"; //$NON-NLS-1$

  private static final String FIELD_DESCRIPTION = "description"; //$NON-NLS-1$

  // ~ Instance fields =================================================================================================

  private String username;

  private String password;

  private String description;

  private boolean enabled = true;

  private Set<IRole> roles = new HashSet<IRole>();

  // ~ Constructors ====================================================================================================

  public CustomUser() {
    // constructor reserved for use by Hibernate
  }

  public CustomUser(String username) {
    this(username, null, null, true);
  }

  public CustomUser(String username, String password, String description, boolean enabled) {
    this.username = username;
    this.password = password;
    this.description = description;
    this.enabled = enabled;
  }

  /**
   * Copy constructor
   */
  public CustomUser(IUser userToCopy) {
    this.username = userToCopy.getUsername();
    this.description = userToCopy.getDescription();
    this.enabled = userToCopy.isEnabled();
    roles = new HashSet<IRole>(userToCopy.getRoles());
  }

  // ~ Methods =========================================================================================================

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setRoles(Set<IRole> roles) {
    this.roles = roles;
  }

  public Set<IRole> getRoles() {
    return roles;
  }

  public boolean equals(Object obj) {
    if (obj instanceof CustomUser == false) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    CustomUser rhs = (CustomUser) obj;
    return new EqualsBuilder().append(username, rhs.username).isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder(71, 223).append(username).toHashCode();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append(FIELD_USERNAME, username).append(
        FIELD_PASSWORD, PASSWORD_MASK).append(FIELD_DESCRIPTION, description).append(FIELD_ENABLED, enabled).toString();
  }

  public boolean addRole(IRole role) {
    return roles.add(role);
  }

  public boolean removeRole(IRole role) {
    return roles.remove(role);
  }

  public void clearRoles() {
    roles.clear();
  }

}
