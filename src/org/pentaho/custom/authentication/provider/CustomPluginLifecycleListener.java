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
package org.pentaho.custom.authentication.provider;

import java.util.Collections;

import org.pentaho.platform.api.engine.IPlatformReadyListener;
import org.pentaho.platform.api.engine.IPluginLifecycleListener;
import org.pentaho.platform.api.engine.PluginLifecycleException;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.springframework.security.userdetails.UserDetailsService;


public class CustomPluginLifecycleListener implements IPluginLifecycleListener, IPlatformReadyListener {

  @Override
  public void ready() throws PluginLifecycleException {
    
    PentahoSystem.get( UserDetailsService.class, null, Collections.singletonMap( "providerName", "custom" ) );
  }

  @Override
  public void init() throws PluginLifecycleException {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void loaded() throws PluginLifecycleException {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void unLoaded() throws PluginLifecycleException {
	// TODO Auto-generated method stub
	
  }

}
