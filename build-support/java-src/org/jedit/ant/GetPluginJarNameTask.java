/*
 * GetPluginJarNameTask.java - Definition of a task for jedit build
 * environment.
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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/** Takes <code>className</code> and inserts jar name for the class into
    <code>outputProp</code>
    property. This is needed only for plugins that are dependencies for
    other plugins. Conversion from class name to jar name is not always
    straightforward as there are no strict rules for that. Here are some
    rules applied and then exceptions come.
    */

public class GetPluginJarNameTask extends Task
{
  private String sClassName;
  private String sOutputProp;
  
  /** To be used as a non-task
      @return <code>null</code> if <code>sClassName</code>
              is <code>null</code>. When no other clue - 
              <code>sClassName</code> */
  public static String getJarName(String sClassName)
  {
    if (sClassName == null) { return null; }
    String sJarName = sClassName;
    sJarName = sJarName.replaceFirst("^.*\\.([^\\.]+)$", "$1");
    if (sJarName.length()>9) {
      sJarName = sJarName.replaceFirst("^(.*)Plugin", "$1");
    }
    // 3 letter plugins are not included as the "Plugin"
    // suffix is not removed in such a case
    if (sJarName.equals("Antelope") ||
        sJarName.equals("AntViz") || // {{{ similar exceptions
        sJarName.equals("AStyle") ||
        sJarName.equals("AStyle") ||
        sJarName.equals("CheckStyle") ||
        sJarName.equals("ClearCase") ||
        sJarName.equals("Gesture") ||
        sJarName.equals("Global") ||
        sJarName.equals("GroovyScriptEngine") ||
        sJarName.equals("Headline") ||
        sJarName.equals("HexTools") ||
        sJarName.equals("JavascriptScriptEngine") ||
        sJarName.equals("Jazzy") ||
        sJarName.equals("JCrontab") ||
        sJarName.equals("JDiff") ||
        sJarName.equals("JDoc") ||
        sJarName.equals("JFugue") ||
        sJarName.equals("JSwat") ||
        sJarName.equals("JTidy") ||
        sJarName.equals("Lucene") ||
        sJarName.equals("Markdown") ||
        sJarName.equals("Maven") ||
        sJarName.equals("Rhino") ||
        sJarName.equals("Ruby") ||
        sJarName.equals("Saxon") ||
        sJarName.equals("Scalac") ||
        sJarName.equals("ScriptEngine") || // }}}
        sJarName.equals("Xerces")) sJarName += "Plugin";
    if (sJarName.equals("Project")) sJarName = "ProjectViewer";
    return sJarName;
  }
  
  @Override
  public void execute()
  {
    if (sClassName == null) {
      throw new BuildException("className parameter not specified.");
    }
    if (sOutputProp == null) {
      throw new BuildException("outputProp parameter not specified.");
    }
    String sJarName = getJarName(sClassName);
    getProject().setProperty(sOutputProp, sJarName);
    getProject().log("class:"+sClassName + " prop:"+sOutputProp
                     + " jar:"+sJarName, Project.MSG_VERBOSE);
  }
  
  public void setClassName(String s)
  {
    sClassName = s;
  }

  public void setOutputProp(String s)
  {
    sOutputProp = s;
  }
}