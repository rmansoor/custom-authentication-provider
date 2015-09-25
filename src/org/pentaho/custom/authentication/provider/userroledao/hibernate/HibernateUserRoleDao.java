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
package org.pentaho.custom.authentication.provider.userroledao.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.pentaho.custom.authentication.provider.AlreadyExistsException;
import org.pentaho.custom.authentication.provider.IRole;
import org.pentaho.custom.authentication.provider.IUser;
import org.pentaho.custom.authentication.provider.IUserRoleDao;
import org.pentaho.custom.authentication.provider.NotFoundException;
import org.pentaho.custom.authentication.provider.CustomRole;
import org.pentaho.custom.authentication.provider.CustomUser;
import org.pentaho.custom.authentication.provider.UncategorizedUserRoleDaoException;
import org.pentaho.custom.authentication.provider.userroledao.messages.Messages;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

/**
 * An {@link IUserRoleDao} that uses Hibernate. Furthermore, it uses Spring's <code>HibernateDaoSupport</code>. This 
 * allows instances to be dependency injected. At a minimum, instances require a Hibernate <code>SessionFactory</code>.
 * 
 * <p>The <code>init</code> method must be called after all properties have been set and before calling any of the CRUD 
 * methods. Can be called automatically if using Spring via the <code>init-method</code> attribute. Otherwise, call it 
 * manually after setting all properties.</p>
 * 
 * @author mlowery
 */
public class HibernateUserRoleDao extends HibernateDaoSupport implements IUserRoleDao {

  // ~ Static fields/initializers ====================================================================================== 

  public static final String DEFAULT_ALL_USERS_QUERY = "from CustomUser order by username"; //$NON-NLS-1$

  public static final String DEFAULT_ALL_ROLES_QUERY = "from CustomRole order by name"; //$NON-NLS-1$

  // ~ Instance fields =================================================================================================

  private String allUsersQuery = DEFAULT_ALL_USERS_QUERY;

  private String allRolesQuery = DEFAULT_ALL_ROLES_QUERY;

  private InitHandler initHandler;

  // ~ Constructors ====================================================================================================

  public HibernateUserRoleDao() {
    super();
  }

  // ~ Methods =========================================================================================================

  /**
   * A generic initialization method. Can be used to load initial data into user- and role-related tables.
   */
  public void init() {
    if (initHandler != null) {
      initHandler.handleInit();
    }
  }

  public void createUser(IUser userToCreate) throws AlreadyExistsException, UncategorizedUserRoleDaoException {
    Assert.notNull(userToCreate, Messages.getInstance().getString("HibernateUserRoleDao.ERROR_0001_USER_CANNOT_BE_NULL")); //$NON-NLS-1$
    Assert.hasLength(userToCreate.getUsername(), Messages.getInstance()
        .getString("HibernateUserRoleDao.ERROR_0002_USERNAME_CANNOT_BE_BLANK")); //$NON-NLS-1$
    Assert.notNull(userToCreate.getPassword(), Messages.getInstance()
        .getString("HibernateUserRoleDao.ERROR_0003_PASSWORD_CANNOT_BE_NULL")); //$NON-NLS-1$

    if (getUser(userToCreate.getUsername()) == null) {
      try {
        getHibernateTemplate().save(userToCreate);
      } catch (DataAccessException e) {
        throw new UncategorizedUserRoleDaoException(Messages.getInstance()
            .getString("HibernateUserRoleDao.ERROR_0004_DATA_ACCESS_EXCEPTION"), e); //$NON-NLS-1$
      }
    } else {
      throw new AlreadyExistsException(userToCreate.getUsername());
    }
  }

  public void deleteUser(IUser userToDelete) throws NotFoundException, UncategorizedUserRoleDaoException {
    Assert.notNull(userToDelete, Messages.getInstance().getString("HibernateUserRoleDao.ERROR_0001_USER_CANNOT_BE_NULL")); //$NON-NLS-1$
    Assert.hasLength(userToDelete.getUsername(), Messages.getInstance()
        .getString("HibernateUserRoleDao.ERROR_0002_USERNAME_CANNOT_BE_BLANK")); //$NON-NLS-1$

    IUser user = getUser(userToDelete.getUsername());
    if (user != null) {
      try {
        getHibernateTemplate().delete(user);
      } catch (DataAccessException e) {
        throw new UncategorizedUserRoleDaoException(Messages.getInstance()
            .getString("HibernateUserRoleDao.ERROR_0004_DATA_ACCESS_EXCEPTION"), e); //$NON-NLS-1$
      }
    } else {
      throw new NotFoundException(userToDelete.getUsername());
    }
  }

  public IUser getUser(String username) throws UncategorizedUserRoleDaoException {
    Assert.hasLength(username, Messages.getInstance().getString("HibernateUserRoleDao.ERROR_0002_USERNAME_CANNOT_BE_BLANK")); //$NON-NLS-1$

    try {
      return (CustomUser) getHibernateTemplate().get(CustomUser.class, username);
    } catch (DataAccessException e) {
      throw new UncategorizedUserRoleDaoException(Messages.getInstance()
          .getString("HibernateUserRoleDao.ERROR_0004_DATA_ACCESS_EXCEPTION"), e); //$NON-NLS-1$
    }
  }

  @SuppressWarnings("unchecked")
  public List<IUser> getUsers() throws UncategorizedUserRoleDaoException {
    try {
      return getHibernateTemplate().find(getAllUsersQuery());
    } catch (DataAccessException e) {
      throw new UncategorizedUserRoleDaoException(Messages.getInstance()
          .getString("HibernateUserRoleDao.ERROR_0004_DATA_ACCESS_EXCEPTION"), e); //$NON-NLS-1$
    }
  }

  public void updateUser(IUser userToUpdate) throws NotFoundException, UncategorizedUserRoleDaoException {
    Assert.notNull(userToUpdate, Messages.getInstance().getString("HibernateUserRoleDao.ERROR_0001_USER_CANNOT_BE_NULL")); //$NON-NLS-1$
    Assert.hasLength(userToUpdate.getUsername(), Messages.getInstance()
        .getString("HibernateUserRoleDao.ERROR_0002_USERNAME_CANNOT_BE_BLANK")); //$NON-NLS-1$
    Assert.notNull(userToUpdate.getPassword(), Messages.getInstance()
        .getString("HibernateUserRoleDao.ERROR_0003_PASSWORD_CANNOT_BE_NULL")); //$NON-NLS-1$

    if (getUser(userToUpdate.getUsername()) != null) {
      try {
        getHibernateTemplate().update(getHibernateTemplate().merge(userToUpdate));
      } catch (DataAccessException e) {
        throw new UncategorizedUserRoleDaoException(Messages.getInstance()
            .getString("HibernateUserRoleDao.ERROR_0004_DATA_ACCESS_EXCEPTION"), e); //$NON-NLS-1$
      }
    } else {
      throw new NotFoundException(userToUpdate.getUsername());
    }
  }

  /**
   * This method is more complex because this is the inverse end of a bidirectional many-to-many relationship. See 
   * Hibernate documentation section 6.3.2. Bidirectional associations. Basically, this means that the users set of this
   * role must be managed manually.
   */
  public void createRole(IRole roleToCreate) throws AlreadyExistsException, UncategorizedUserRoleDaoException {
    Assert.notNull(roleToCreate, Messages.getInstance().getString("HibernateUserRoleDao.ERROR_0005_ROLE_CANNOT_BE_NULL")); //$NON-NLS-1$
    Assert.hasLength(roleToCreate.getName(), Messages.getInstance()
        .getString("HibernateUserRoleDao.ERROR_0006_ROLE_NAME_CANNOT_BE_BLANK")); //$NON-NLS-1$

    if (getRole(roleToCreate.getName()) == null) {
      try {
        getHibernateTemplate().save(roleToCreate);
      } catch (DataAccessException e) {
        throw new UncategorizedUserRoleDaoException(Messages.getInstance()
            .getString("HibernateUserRoleDao.ERROR_0004_DATA_ACCESS_EXCEPTION"), e); //$NON-NLS-1$
      }
    } else {
      throw new AlreadyExistsException(roleToCreate.getName());
    }

    // manually manage users set

    for (IUser user : roleToCreate.getUsers()) {
      addUser(roleToCreate, user.getUsername());
    }
  }

  /**
   * This method is more complex because this is the inverse end of a bidirectional many-to-many relationship. See 
   * Hibernate documentation section 6.3.2. Bidirectional associations. Basically, this means that the users set of this
   * role must be managed manually.
   */
  public void deleteRole(IRole roleToDelete) throws NotFoundException, UncategorizedUserRoleDaoException {
    Assert.notNull(roleToDelete, Messages.getInstance().getString("HibernateUserRoleDao.ERROR_0005_ROLE_CANNOT_BE_NULL")); //$NON-NLS-1$
    Assert.hasLength(roleToDelete.getName(), Messages.getInstance()
        .getString("HibernateUserRoleDao.ERROR_0006_ROLE_NAME_CANNOT_BE_BLANK")); //$NON-NLS-1$

    IRole role = getRole(roleToDelete.getName());
    if (role != null) {
      try {
        // for each user that is a member of this role, manually remove the role assignment from the user
        for (IUser user : role.getUsers()) {
          user.removeRole(role);
          updateUser(user);
        }
        // delete the role itself now that it is no longer referenced anywhere 
        getHibernateTemplate().delete(role);
      } catch (DataAccessException e) {
        throw new UncategorizedUserRoleDaoException(Messages.getInstance()
            .getString("HibernateUserRoleDao.ERROR_0004_DATA_ACCESS_EXCEPTION"), e); //$NON-NLS-1$
      }
    } else {
      throw new NotFoundException(roleToDelete.getName());
    }
  }

  public IRole getRole(String name) throws UncategorizedUserRoleDaoException {
    Assert.hasLength(name, Messages.getInstance().getString("HibernateUserRoleDao.ERROR_0006_ROLE_NAME_CANNOT_BE_BLANK")); //$NON-NLS-1$

    try {
      return (CustomRole) getHibernateTemplate().get(CustomRole.class, name);
    } catch (DataAccessException e) {
      throw new UncategorizedUserRoleDaoException(Messages.getInstance()
          .getString("HibernateUserRoleDao.ERROR_0004_DATA_ACCESS_EXCEPTION"), e); //$NON-NLS-1$
    }
  }

  @SuppressWarnings("unchecked")
  public List<IRole> getRoles() throws UncategorizedUserRoleDaoException {
    try {
      return getHibernateTemplate().find(getAllRolesQuery());
    } catch (DataAccessException e) {
      throw new UncategorizedUserRoleDaoException(Messages.getInstance()
          .getString("HibernateUserRoleDao.ERROR_0004_DATA_ACCESS_EXCEPTION"), e); //$NON-NLS-1$
    }
  }

  /**
   * This method is more complex because this is the inverse end of a bidirectional many-to-many relationship. See 
   * Hibernate documentation section 6.3.2. Bidirectional associations. Basically, this means that the users set of this
   * role must be managed manually.
   */
  @SuppressWarnings("unchecked")
  public void updateRole(IRole roleToUpdate) throws NotFoundException, UncategorizedUserRoleDaoException {
    Assert.notNull(roleToUpdate, Messages.getInstance().getString("HibernateUserRoleDao.ERROR_0005_ROLE_CANNOT_BE_NULL")); //$NON-NLS-1$
    Assert.hasLength(roleToUpdate.getName(), Messages.getInstance()
        .getString("HibernateUserRoleDao.ERROR_0006_ROLE_NAME_CANNOT_BE_BLANK")); //$NON-NLS-1$

    IRole originalRole = getRole(roleToUpdate.getName());

    // make a copy of originalRole's users since the merge call below will change the users
    Set<IUser> originalRoleUsers = new HashSet<IUser>(originalRole.getUsers());

    if (originalRole != null) {
      try {
        getHibernateTemplate().update(getHibernateTemplate().merge(roleToUpdate));
      } catch (DataAccessException e) {
        throw new UncategorizedUserRoleDaoException(Messages.getInstance()
            .getString("HibernateUserRoleDao.ERROR_0004_DATA_ACCESS_EXCEPTION"), e); //$NON-NLS-1$
      }
    } else {
      throw new NotFoundException(roleToUpdate.getName());
    }

    // manually manage users set

    // use relative complement (aka set-theoretic difference, aka subtraction) to get the users to add and users to 
    // remove
    Set<IUser> usersToAdd = new HashSet<IUser>(CollectionUtils.subtract(roleToUpdate.getUsers(),
        originalRoleUsers));
    Set<IUser> usersToRemove = new HashSet<IUser>(CollectionUtils.subtract(originalRoleUsers,
        roleToUpdate.getUsers()));

    for (IUser user : usersToAdd) {
      addUser(roleToUpdate, user.getUsername());
    }

    for (IUser user : usersToRemove) {
      removeUser(roleToUpdate, user.getUsername());
    }

  }

  /**
   * This method is necessary because this is the inverse end of a bidirectional many-to-many relationship. See 
   * Hibernate documentation section 6.3.2. Bidirectional associations.
   */
  protected void addUser(IRole roleToUpdate, String username) throws NotFoundException,
      UncategorizedUserRoleDaoException {
    Assert.notNull(roleToUpdate, Messages.getInstance().getString("HibernateUserRoleDao.ERROR_0005_ROLE_CANNOT_BE_NULL")); //$NON-NLS-1$
    Assert.hasLength(roleToUpdate.getName(), Messages.getInstance()
        .getString("HibernateUserRoleDao.ERROR_0006_ROLE_NAME_CANNOT_BE_BLANK")); //$NON-NLS-1$
    Assert.hasLength(username, Messages.getInstance().getString("HibernateUserRoleDao.ERROR_0002_USERNAME_CANNOT_BE_BLANK")); //$NON-NLS-1$

    IUser user = getUser(username);
    if (user != null) {
      user.addRole(roleToUpdate);
      updateUser(user);
    } else {
      throw new NotFoundException(username);
    }
  }

  /**
   * This method is necessary because this is the inverse end of a bidirectional many-to-many relationship. See 
   * Hibernate documentation section 6.3.2. Bidirectional associations.
   */
  protected void removeUser(IRole roleToUpdate, String username) throws NotFoundException,
      UncategorizedUserRoleDaoException {
    Assert.notNull(roleToUpdate, Messages.getInstance().getString("HibernateUserRoleDao.ERROR_0005_ROLE_CANNOT_BE_NULL")); //$NON-NLS-1$
    Assert.hasLength(roleToUpdate.getName(), Messages.getInstance()
        .getString("HibernateUserRoleDao.ERROR_0006_ROLE_NAME_CANNOT_BE_BLANK")); //$NON-NLS-1$
    Assert.hasLength(username, Messages.getInstance().getString("HibernateUserRoleDao.ERROR_0002_USERNAME_CANNOT_BE_BLANK")); //$NON-NLS-1$

    IUser user = getUser(username);
    if (user != null) {
      user.removeRole(roleToUpdate);
      updateUser(user);
    } else {
      throw new NotFoundException(username);
    }
  }

  public void setAllUsersQuery(String allUsersQuery) {
    Assert.hasLength(allUsersQuery, Messages.getInstance()
        .getString("HibernateUserRoleDao.ERROR_0007_ALL_USERS_QUERY_CANNOT_BE_BLANK")); //$NON-NLS-1$
    this.allUsersQuery = allUsersQuery;
  }

  public String getAllUsersQuery() {
    return allUsersQuery;
  }

  public void setAllRolesQuery(String allRolesQuery) {
    Assert.hasLength(allUsersQuery, Messages.getInstance()
        .getString("HibernateUserRoleDao.ERROR_0008_ALL_ROLES_QUERY_CANNOT_BE_BLANK")); //$NON-NLS-1$
    this.allRolesQuery = allRolesQuery;
  }

  public String getAllRolesQuery() {
    return allRolesQuery;
  }

  public void setInitHandler(InitHandler initHandler) {
    this.initHandler = initHandler;
  }

  
  
  /**
   * Generic interface to allow extensibility without tight coupling. Example use: insert sample users and roles into
   * empty tables.
   * 
   * @author mlowery
   */
  public static interface InitHandler {
    void handleInit();
  }

}