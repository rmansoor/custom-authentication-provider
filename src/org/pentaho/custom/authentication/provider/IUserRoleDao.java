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

import java.util.List;

/**
 * Contract for data access objects that read and write users and roles.
 * 
 * @author mlowery
 */
public interface IUserRoleDao {

  void createUser(IUser newUser) throws AlreadyExistsException, UncategorizedUserRoleDaoException;

  void deleteUser(IUser user) throws NotFoundException, UncategorizedUserRoleDaoException;

  IUser getUser(String name) throws UncategorizedUserRoleDaoException;

  List<IUser> getUsers() throws UncategorizedUserRoleDaoException;

  void updateUser(IUser user) throws NotFoundException, UncategorizedUserRoleDaoException;

  void createRole(IRole newRole) throws AlreadyExistsException, UncategorizedUserRoleDaoException;

  void deleteRole(IRole role) throws NotFoundException, UncategorizedUserRoleDaoException;

  IRole getRole(String name) throws UncategorizedUserRoleDaoException;

  List<IRole> getRoles() throws UncategorizedUserRoleDaoException;

  void updateRole(IRole role) throws NotFoundException, UncategorizedUserRoleDaoException;

}
