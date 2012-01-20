# EditorConfig jEdit Plugin

This is an [EditorConfig][] plugin for [jEdit][].

## Installation

1.  Download the [EditorConfig core][] and follow the instructions in the README
and INSTALL files to install it.

2.  Download [ant][] and install it.

3.  In the EditorConfig jEdit Plugin project parent directory, get the jEdit
    build-support files:

        svn co https://jedit.svn.sourceforge.net/svnroot/jedit/build-support/trunk build-support

4.  Copy `build.properties.in` to `build.properties`. Edit the
    `build.properties` file and modify `jedit.install.dir` to the jEdit
    installation directory in your system.

5.  Switch to EditorConfig jEdit Plugin project root directory and run `ant`.

6.  If succeeded, Plugin should be built in `build/jar/`. Copy
    `build/jar/EditorConfigPlugin.jar` to your jEdit plugin directory (this
    should be `~/.jedit/jars` on UNIX and
    `${JEDIT_INATALLATION_DIRECTORY}/jars` on Windows).

7.  If jEdit is running, restart jEdit.

For example, on Debian, the commands are like this:

```Shell
$ apt-get install subversion ant git  # Do this with root privilege
$ git clone git://github.com/editorconfig/editorconfig-jedit.git
$ svn co https://jedit.svn.sourceforge.net/svnroot/jedit/build-support/trunk build-support
$ cd editorconfig-jedit
$ cp build.properties.in build.properties # Copy build properties and modify jedit.install.dir as needed
$ ant
$ cp ./build/jar/EditorConfigPlugin.jar ~/.jedit/jars
```

[ant]: http://ant.apache.org
[EditorConfig]: http://editorconfig.org
[EditorConfig core]: https://github.com/editorconfig/editorconfig
[jEdit]: http://www.jedit.org
