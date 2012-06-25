/*
 * Definition of an ant type for jedit build environment.
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

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.FileResourceIterator;

/** Takes filesets as nested arguments: <code>fsSrc</code> and
    <code>fsExtras</code> and retrieves plugin information.
    Alternatively <code>jar</code> attibute may be given to parse the
    jar contents instead of filesets.
    */

public class PluginInfoType extends DataType implements Cloneable
{
  // input parameters
  private FileSet fsSrc;
  private FileSet fsExtras;
  private String sJarIn;
  
  // plugin info
  private String sClass;
  private String sJar;
  private String sVersion;
  private String sJeditVersionShort;
  private String sJeditVersionFull;
  private DepList deps = new DepList();
  
  /** Whether the info is already filled. */
  private boolean bFilled;
  private Project p;
  
  //{{{ filled method
  /** Returns <code>PluginInfoType</code> object that is ok to operate on.
      That is: filled and being not a reference. Serves as a shorthand */
  private PluginInfoType filled()
  {
    if (isReference()) { return getRef().filled(); }
    fill();
    return this;
  }
  
  //{{{ get... methods
  public String getClassName() { return filled().sClass; }
  public String getJarName() { return filled().sJar; }
  public String getVersion() { return filled().sVersion; }
  public String getJeditVersionShort() { return filled().sJeditVersionShort; }
  public String getJeditVersionFull() { return filled().sJeditVersionFull; }
  public int getDepCount() { return filled().deps.size(); }
  public String getDepsString() { return filled().deps.toString(); }
  public Dep getDep(int i) { return filled().deps.get(i); }
  //}}}
  
  //{{{ getPluginClassName method 
  /** Gets an <code>Iterator</code> over objects implementing
      <code>toString</code> and discovers the plugin name.
      The strings are treated as filenames and the filename ending
      with <code>Plugin.java</code> denotes the plugin name.
      This is the same as done in
      <code>org.gjt.sp.jedit.PluginJAR.generateCache()</code>.
      @param sBaseDir The base directory will be substracted from plugin
                      filename to get only the part containing the
                      package name. May be <code>null</code>
      @param it The <code>iterator</code> over <code>Object</code>s,
                which implement <code>toString()</code>
      @return <code>null</code> if not a plugin.
      */
  public static String getPluginClassName(String sBaseDir, Iterator it) {
    String sPluginClass = null;
    while (it.hasNext()) {
      String sFile = it.next().toString();
      sFile = sFile.replaceFirst("\\.class", ".java");
      // There are some class ending with Plugin not being real plugins.
      // For example in XML plugin.
      // How to tell them? Donna. Inserting exceptions for them.
      // TODO: One coulde try all potential plugin classes until finds
      //       the one specifing jedit version. Too difficult.
      if (sFile.endsWith("Plugin.java")
          && !sFile.endsWith("CssSideKickPlugin.java")
          && !sFile.endsWith("HtmlSideKickPlugin.java")
          && !sFile.endsWith("JavaScriptSideKickPlugin.java")) {
        sPluginClass = sFile;
        if (sBaseDir != null) {
          sPluginClass = sPluginClass.substring(sBaseDir.length()+1);
        }
        sPluginClass = sPluginClass.replaceFirst("\\.java$", "");
        sPluginClass = sPluginClass.replaceAll("[/\\\\]", ".");
        break;
      }
    }
    return sPluginClass;
  } //}}}

  //{{{ fill() method
  public void fill()
  {
    if (isReference()) { getRef().fill(); return; }
    if (bFilled) { return; }
    Iterator itSrc, itExtras;
    String sBaseDir;
    ZipFile zip = null;
    p = getProject();
    if (sJarIn != null) {
      // process jar file to get the info
      try {
        zip = new ZipFile(sJarIn);
        sBaseDir = null;
        itSrc = Collections.list(zip.entries()).iterator();
        itExtras = itSrc;
      } catch (java.io.IOException ioe) {
        throw new BuildException(ioe);
      }
    } else {
      // filesets given as source for the info
      if (fsSrc == null) {
        throw new BuildException("fsSrc parameter not specified.");
      }
      if (fsExtras == null) {
        throw new BuildException("fsExtras parameter not specified.");
      }
      sBaseDir = fsSrc.getDir().toString(); 
      itSrc = fsSrc.iterator();
      itExtras = fsExtras.iterator();
    }
    sClass = getPluginClassName(sBaseDir, itSrc);
    sJar = GetPluginJarNameTask.getJarName(sClass);

    // load all props files {{{
    Properties props = new Properties();
    while (itExtras.hasNext()) {
      Object entry = itExtras.next();
      if (entry.toString().endsWith(".props")) {
        try {
          if (sJarIn != null) {
            ZipEntry zipEntry = (ZipEntry)entry;
            props.load(zip.getInputStream(zipEntry));
          } else {
            FileResource fr = (FileResource)entry;
            props.load(fr.getInputStream());
          }
        } catch (java.io.IOException e) {
          throw new BuildException(e);
        }
      }
    } // }}}

    sVersion = props.getProperty("plugin." + sClass + ".version", "");
    parseProps(sClass, props);
    bFilled = true;
  } //}}}

  //{{{ parsePropse method 
  /** Reads dependencies from properties.
    * See <code>PluginJAR.checkDependencies()</code> */
  private void parseProps(String sPluginClass, Properties props)
  {
    int i, iPluginDep;
    i = 0; iPluginDep = 0;
    String sDepPropName = "plugin." + sPluginClass + ".depend.";
    String sDep;
    while((sDep = props.getProperty(sDepPropName + i)) != null) {
      String asDeps[] = sDep.split(" ");
      if (asDeps[0].equals("jedit")) {
        sJeditVersionFull = asDeps[1];
        String v[] = sJeditVersionFull.split("\\.");
        sJeditVersionShort = v[0] + "." + v[1];
      }
      if (asDeps[0].equals("optional")) {
        // ignore the optional keyword, treat as usual plugin dep
        asDeps = Arrays.copyOfRange(asDeps, 1, asDeps.length);
      }
      if (asDeps[0].equals("plugin")) {
        Dep dep = new Dep();
        //print("" + iPluginDep + asDeps[1] + "-" + asDeps[2]);
        String sPref = "plugin.dep." + iPluginDep;
        dep.sClass = asDeps[1];
        dep.sVersion = asDeps[2];
        dep.sJar = GetPluginJarNameTask.getJarName(asDeps[1]);
        deps.add(dep);
        iPluginDep++;
      }
      
      i++;
    }
  } //}}}

  public void setJar(String s)
  {
    sJarIn = s;
    checkAttr();
  }

  public void addFsSrc(FileSet fs)
  {
    fsSrc = fs;
    checkAttr();
  }

  public void addFsExtras(FileSet fs)
  {
    fsExtras = fs;
    checkAttr();
  }

  private void checkAttr()
  {
    p = getProject();
    if (sJarIn != null && (fsSrc != null || fsExtras != null)) {
      throw new BuildException("jar and fsSrc/Extras " +
                               "are mutually exclusive."); 
    }
  }
  
  protected PluginInfoType getRef() {
    return (PluginInfoType) getCheckedRef(PluginInfoType.class,
                                          "plugininfotype");
  }

  public String toString()
  {
    if (isReference()) { return getRef().toString(); }
    String s;
    if (!bFilled) { fill(); }
    s = "Plugin class name: " + sClass + ", jar name: " + sJar;
    s += ", version: " + sVersion + "\n";
    s += "jedit version: " + sJeditVersionFull;
    s += ", dependencies count: " + deps.size() + "\n";
    s += deps.toString();
    return s; 
  }
  
  public PluginInfoType clone()
  {
    if (isReference()) { return getRef().clone(); }
    PluginInfoType piNew = null;
    try {
      piNew = (PluginInfoType)super.clone();
      piNew.deps = this.deps.clone();
    } catch (CloneNotSupportedException e) {
      throw new BuildException(e);
    }
    return piNew;
  }
  
  //{{{ setProjectProperties() method
  /** Stores plugin info in project properties. For details see
      {@link GetPluginInfoTask}.
      @param sPref Prefix added to the properties. May not be
             <code>null</code>
  */
  public void setProjectProperties(String sPref)
  {
    if (isReference()) { getRef().setProjectProperties(sPref); return; }
    fill();
    PropertySet ps = (PropertySet)p.createDataType("propertyset"); 
    p.addReference(sPref + "plugin.props.set", ps); 
    p.setProperty(sPref + "plugin.class.name", sClass);
    p.setProperty(sPref + "plugin.jar.name", sJar);
    p.setProperty(sPref + "plugin.jedit.version.full", sJeditVersionFull);
    p.setProperty(sPref + "plugin.jedit.version", sJeditVersionShort);
    p.setProperty(sPref + "plugin.dep.count", "" + deps.size());
    ps.appendName(sPref + "plugin.class.name");
    ps.appendName(sPref + "plugin.jar.name");
    ps.appendName(sPref + "plugin.jedit.version.full");
    ps.appendName(sPref + "plugin.jedit.version");
    ps.appendName(sPref + "plugin.dep.count");
    for (int i=0; i<deps.size(); i++) {
      Dep dep = deps.get(i);
      String sDepPref = sPref + "plugin.dep." + i;
      p.setProperty(sDepPref + ".class", dep.sClass);
      p.setProperty(sDepPref + ".version", dep.sVersion);
      p.setProperty(sDepPref + ".jar.name", dep.sJar);
      ps.appendRegex(sDepPref + "\\.*");
    }
  } //}}}
  
  //{{{ joinDeps method
  /** Adds dependencies from <code>pi</code> to current dependencies.
      @return <code>true</code> if there were new dependencies. */
  public boolean joinDeps(PluginInfoType pi2, StringBuilder sb)
  {
    boolean bNewDeps = false;
    for (int i2 = 0; i2 < pi2.getDepCount(); i2++) {
      int iState = 1;   // 0 - same, 1 - new, 2 - update
      Dep dep2 = pi2.getDep(i2);
      for (Dep dep: this.filled().deps) {
        if (dep.getJarName().equals(dep2.getJarName())) {
          if (Misc.compareStrings(dep.getVersion(), dep2.getVersion(), true)
                   < 0) {
            iState = 2;
            dep.sVersion = dep2.getVersion();
          } else {
            iState = 0;
          }
          break;
        }
      }
      if (iState == 1) {
        this.filled().deps.add(dep2);
      }
      if (iState != 0) {
        sb.append(dep2.getJarName() + " " + dep2.getVersion() + " (" +
                  (iState == 2 ? "update" : "new") + ")\n");
        bNewDeps = true;
      }
    }
    return bNewDeps;
  } //}}}

  //{{{ Dep class
  /** Plugin dependency information */
  public static class Dep
  {
    String sClass;
    String sVersion;
    String sJar;
    
    //{{{ get... methods
    public String getClassName() { return sClass; }
    public String getJarName() { return sJar; }
    public String getVersion() { return sVersion; }
    //}}}

  } //}}}

  //{{{ DepList class
  /** A list of dependencies being plugins */
  public static class DepList implements Iterable<Dep>, Cloneable
  {
    private ArrayList<Dep> a = new ArrayList<Dep>();

    // several methods imitating ArrayList {{{
    public void add(Dep d)
    {
      a.add(d);
    }

    public Dep get(int i)
    {
      return a.get(i);
    }

    public int size()
    {
      return a.size();
    }
    
    public Iterator<Dep> iterator()
    {
      return a.iterator();
    }
    
    @SuppressWarnings (value="unchecked")
    public DepList clone()
    {
      DepList depsNew = new DepList();
      depsNew.a = (ArrayList)this.a.clone();
      return depsNew;
    }
    //}}}
    
    public String toString()
    {
      StringBuilder sb = new StringBuilder();
      for (Dep dep: a) {
        sb.append("dependency: " + dep.sJar + " " + dep.sVersion + "\n");
      }
      return sb.toString();
    }
 
  } //}}}
}