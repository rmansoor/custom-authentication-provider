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

import java.io.Serializable;
import java.util.Set;

/**
 * A role in the Pentaho platform. Contains a set of users to which the role is assigned. A role is also known as an 
 * authority.
 * 
 * @author mlowery
 */
public interface IRole extends Serializable {

  String getName();

  String getDescription();

  void setDescription(String description);

  Set<IUser> getUsers();

  void setUsers(Set<IUser> users);

  /**
   * @return Same meaning as Set.add return value.
   */
  boolean addUser(IUser user);

  /**
   * @return Same meaning as Set.remove return value.
   */
  boolean removeUser(IUser user);

  void clearUsers();
}
