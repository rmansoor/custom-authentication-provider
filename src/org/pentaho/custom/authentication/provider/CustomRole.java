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
 * A role in the Pentaho platform. Contains a set of users to which the role is assigned. A role is also known as an 
 * authority.
 * 
 * <p>Note that users are not considered during equals comparisons and hashCode calculations. This is because instances 
 * are sometimes stored in Java collections. The users set is mutable and we don't want two roles that have the same 
 * name but different users in the same set.</p>
 * 
 * @see CustomUser
 * @author mlowery
 */
public class CustomRole implements IRole {

  // ~ Static fields/initializers ====================================================================================== 

  private static final long serialVersionUID = 7280850318778455743L;

  private static final String FIELD_NAME = "name"; //$NON-NLS-1$

  // ~ Instance fields =================================================================================================

  private String name;

  private String description;

  private Set<IUser> users = new HashSet<IUser>();

  // ~ Constructors ====================================================================================================

  public CustomRole() {
    // constructor reserved for use by Hibernate
  }

  public CustomRole(String name) {
    this(name, null);
  }

  public CustomRole(String name, String description) {
    this.name = name;
    this.description = description;
  }

  /**
   * Copy constructor
   */
  public CustomRole(IRole roleToCopy) {
    this.name = roleToCopy.getName();
    this.description = roleToCopy.getDescription();
    users = new HashSet<IUser>(roleToCopy.getUsers());
  }

  // ~ Methods =========================================================================================================

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean equals(Object obj) {
    if (obj instanceof CustomRole == false) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    CustomRole rhs = (CustomRole) obj;
    return new EqualsBuilder().append(name, rhs.name).isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder(61, 167).append(name).toHashCode();
  }

  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append(FIELD_NAME, name).toString();
  }

  public void setUsers(Set<IUser> users) {
    this.users = users;
  }

  public Set<IUser> getUsers() {
    return users;
  }

  public boolean addUser(IUser user) {
    return users.add(user);
  }

  public boolean removeUser(IUser user) {
    return users.remove(user);
  }

  public void clearUsers() {
    users.clear();
  }

}
