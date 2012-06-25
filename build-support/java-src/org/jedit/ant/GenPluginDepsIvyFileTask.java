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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Generates an <code>ivy.xml</code> file specified by <code>outFile</code>
    based on <code>template</code> ivy file. The dependencies are taken
    from obligatory nested <code>plugininfo</code> element.
    I had problems with automatic creation of parent directory
    of <code>outFile</code>, so it must be done manually before calling
    this task.
    */

public class GenPluginDepsIvyFileTask extends Task
{
  private String sTemplateFile;
  private String sOutFile;
  private PluginInfoType pi;
  private Project p;
  
  @Override
  public void execute()
  {
    p = getProject();
    if (sTemplateFile == null) {
      throw new BuildException("\"template\" parameter not specified.");
    }
    if (sOutFile == null) {
      throw new BuildException("\"outFile\" parameter not specified.");
    }
    if (pi == null) {
      throw new BuildException("Nested \"plugininfo\" data not specified.");
    }

    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                                .newDocumentBuilder();
      Document doc = builder.parse(new File(sTemplateFile));
      Element docElem = doc.getDocumentElement();
      Node lfNode = doc.createTextNode(System.getProperty("line.separator"));
      Element info = (Element)docElem.getElementsByTagName("info").item(0);
      info.setAttribute("module", pi.getJarName());
      Element deps = (Element)docElem.getElementsByTagName("dependencies")
                                     .item(0);
      for (int iDep=0; iDep<pi.getDepCount(); iDep++) {                                             
        PluginInfoType.Dep depInfo = pi.getDep(iDep);
        Element dep = doc.createElement("dependency");
        dep.setAttribute("org", "jedit-plugins-zip");
        dep.setAttribute("name", depInfo.getJarName());
        dep.setAttribute("rev", depInfo.getVersion());
        deps.appendChild(dep);
        deps.appendChild(lfNode.cloneNode(true));
      }
      Transformer transformer = TransformerFactory.newInstance()
                                       .newTransformer();
      DOMSource src = new DOMSource(doc);
      StreamResult dest = new StreamResult(new File(sOutFile));
      transformer.transform(src, dest);
    }
    catch (javax.xml.parsers.ParserConfigurationException e1) {
      throw new BuildException(e1);
    }
    catch (org.xml.sax.SAXException e2) {
      throw new BuildException(e2);
    }
    catch (javax.xml.transform.TransformerException e3) {
      throw new BuildException(e3);
    }
    catch (java.io.IOException e4) {
      throw new BuildException(e4);
    }
  }
  
  public void setTemplate(String s)
  {
    sTemplateFile = s;
  }

  public void setOutFile(String s)
  {
    sOutFile = s;
  }

  public void add(PluginInfoType pi)
  {
    this.pi = pi;
  }

}