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

//{{{ imports
import java.io.File;
import java.util.Iterator;
import java.util.Properties;
import org.apache.ivy.ant.IvyRetrieve;
import org.apache.ivy.ant.IvyResolve;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.FileResourceIterator;
//}}}

/** Takes <code>plugininfo</code> as nested argument and downloads plugin
    dependencies. Then checks all downloaded jars and tries to satisfy
    their dependencies. Repeats it until all the dependencies are sastified.
    Task attributes:<ul>
    <li><code>todir</code> - destination directory
    <li><code>ivyfilesdir</code> - directory to place ivy files in. The ivy
        files are numbered from <code>0</code> to <code>n</code>.
    <li><code>template</code> - ivy file serving as a template    
    </ul>
    <p>Warning: erase the contents of <code>todir</code> directory.
    */

public class DownloadPluginDepsRecTask extends Task
{
  private String sToDir;
  private File toDir;
  private String sIvyFilesDir;
  private String sTemplateFile;
  /** Plugin info received from caller. We don't want to change it. */
  private PluginInfoType pi0;
  private Project p;
  
  private static final String sIvyFilePref = "ivy.plugin.deps";

  //{{{ execute method  
  @Override
  public void execute()
  {
    p = getProject();
    checkAttrs();
    clearOldIvyFiles();
    toDir.mkdirs();
    PluginInfoType pi = pi0.clone();
    
    log("Initial dependencies of " + pi.getJarName()
        + " " + pi.getVersion() + ":\n");
    log(pi.getDepsString());
    
    boolean bNewDeps = true;
    int iRound = 0;
    while (true) {
      p.log("Downloading recursively plugin dependencies for " + 
            pi.getJarName() + ", round " + iRound);
      downloadDeps(pi, iRound);
      bNewDeps = addJarDeps(pi);
      if (!bNewDeps) { break; }
      iRound += 1;
      if (iRound >= 3) {
        throw new BuildException("Too many rounds (" + iRound + ") while " +
          "trying to download plugin dependencies recursively.");
      }
    }
    
    log("Final recursive dependencies of " + pi.getJarName()
        + " " + pi.getVersion() + ":\n");
    log(pi.getDepsString());
  } //}}}
  
  //{{{ checkAttrs method
  private void checkAttrs()
  {
    if (pi0 == null) {
      throw new BuildException("pluginInfo not provided");
    }
    if (sToDir == null) {
      throw new BuildException("todir not provided");
    }
    if (sIvyFilesDir == null) {
      throw new BuildException("ivyfilesdir not provided");
    }
    if (sTemplateFile == null) {
      throw new BuildException("template not provided");
    }
    toDir = Misc.getProjectFile(p, sToDir);
  } //}}}
  
  //{{{ clearOldIvyFiles method
  private void clearOldIvyFiles()
  {
    Delete del = (Delete)p.createTask("delete");
    del.setDir(Misc.getProjectFile(p, sIvyFilesDir));
    del.setIncludes(sIvyFilePref + ".*.xml");
    del.execute();
  } //}}}

  //{{{ clearDestDir method
  private void clearDestDir()
  {
    Delete del = (Delete)p.createTask("delete");
    del.setDir(toDir);
    del.setIncludes("*");
    del.execute();
  } //}}}

  //{{{ downloadDeps method
  private void downloadDeps(PluginInfoType pi, int iRound)
  {
    // A simplest way in case a plugin needs to be updated is to
    // erase all and download again. Ivy doesn't really download them
    // each time, but copies from the cache
    clearDestDir();
    
    // generate ivy file
    File ivyFile = new File(Misc.getProjectFile(p, sIvyFilesDir),
                            sIvyFilePref + "." + iRound + ".xml");
    GenPluginDepsIvyFileTask gen = new GenPluginDepsIvyFileTask();
    gen.setProject(p);
    gen.setTemplate(sTemplateFile);
    gen.setOutFile(ivyFile.toString());
    gen.add(pi);
    gen.execute();
    
    // download zips using ivy:resolve and ivy:retrieve tasks
    p.log(ivyFile+"");
    IvyResolve res = new IvyResolve();
    res.setProject(p);
    res.setFile(ivyFile);
    res.setLog("download-only");
    res.execute();
    IvyRetrieve ret = new IvyRetrieve();
    ret.setProject(p);
    ret.setFile(ivyFile);
    ret.setPattern(toDir.toString() + "/[artifact].zip");
    // ret.setOrganisation("jedit");
    // ret.setModule(pi.getJarName());
    // ret.setKeep(false);
    ret.setLog("download-only");
    ret.execute();

    // unzip them
    Expand unzip = (Expand)p.createTask("unzip");
    unzip.setDest(toDir);
    FileSet fsZips = new FileSet();
    fsZips.setProject(p);
    fsZips.setDir(toDir);
    fsZips.setIncludes("*.zip");
    unzip.add(fsZips);
    unzip.execute();
    
    // delete zips
    Delete del = new Delete();
    del.setProject(p);
    del.add(fsZips);
    del.execute();
  } //}}}
  
  //{{{ addJarDeps method
  /** Opens jars in <code>toDir</code> and adds their plugin dependencies
      to current dependencies list <code>pi</code>.
      @param pi Plugin info to which new deps will be added.
      @return <code>true</code> if new deps were added, <code>false</code>
              if the jars needn't nothing more than currently in
              <code>pi</code>. */
  private boolean addJarDeps(PluginInfoType pi)
  {
    boolean bNewDeps = false;
    for (String sFile: toDir.list()) {
      if (sFile.endsWith(".jar")) {
        StringBuilder sbMsg = new StringBuilder();
        PluginInfoType piJar = new PluginInfoType();
        piJar.setProject(p);
        piJar.setJar(new File(toDir, sFile).toString());
        if (pi.joinDeps(piJar, sbMsg)) {
          bNewDeps = true;
          p.log(piJar.getJarName() + " " + piJar.getVersion()
                + " needs also:");
          p.log(sbMsg.toString());
        }
      }
    }
    return bNewDeps;
  } //}}}
  
  // methods setting task parameters {{{
  public void add(PluginInfoType pi)
  {
    this.pi0 = pi;
  }

  public void setToDir(String s)
  {
    sToDir = s;
  }
  
  public void setIvyFilesDir(String s)
  {
    sIvyFilesDir = s;
  }
  
  public void setTemplate(String s)
  {
    sTemplateFile = s;
  }
  //}}}
}