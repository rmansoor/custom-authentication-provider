package org.pentaho.custom.authentication.provider;

import java.util.Collections;

import org.pentaho.platform.api.engine.IPlatformReadyListener;
import org.pentaho.platform.api.engine.IPluginLifecycleListener;
import org.pentaho.platform.api.engine.PluginLifecycleException;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.springframework.security.userdetails.UserDetailsService;


public class CustomAuthenticationProviderPluginLifecycleListener implements IPluginLifecycleListener, IPlatformReadyListener {

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
