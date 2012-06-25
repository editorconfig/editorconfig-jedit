/*
 * Definition of a task for jedit build environment.
 * :tabSize=2:indentSize=2:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2012 Jarek Czekalski
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jedit.ant;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.FileResourceIterator;

/** Takes <code>plugininfo</code> as nested argument and stores the plugin
    info as properties.
    <p>The following properties are set:<ul>
    <li>plugin.class.name - e.g. projectviewer.ProjectPlugin
    <li>plugin.jar.name - e.g. ProjectViewer
    <li>plugin.jedit.version.full - e.g. 4.4.99.0
    <li>plugin.jedit.version - e.g. 4.4
    <li>plugin.dep.X.class - e.g. CommonControlsPlugin, X=0..n
    <li>plugin.dep.X.version - e.g. 1.3
    <li>plugin.dep.X.jar.name - e.g. CommonControls
    </ul>
    and a property set <code>plugin.props.set</code>.
    Properties names and property set name may be prefixed if
    the <code>prefix</code> attribute is specified. When providing this
    attribute include a dot at the end.
    */

public class GetPluginInfoTask extends Task
{
  private PluginInfoType pi;
  private String sPrefix = "";
  
  @Override
  public void execute()
  {
    if (pi == null) {
      throw new BuildException("pluginInfo not provided");
    }
    pi.setProjectProperties(sPrefix);
  }
  
  public void add(PluginInfoType pi)
  {
    this.pi = pi;
  }

  public void setPrefix(String s)
  {
    sPrefix = s;
  }
  
}