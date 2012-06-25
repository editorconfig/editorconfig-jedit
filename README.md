# EditorConfig jEdit Plugin

This is an [EditorConfig][] plugin for [jEdit][].

## Installation

1.  Download the [EditorConfig core][] and follow the instructions in the README
and INSTALL files to install it.

2.  Download [ant][] and install it.

3.  Copy `build.properties.in` to `build.properties`. Edit the
    `build.properties` file and modify `jedit.install.dir` to the jEdit
    installation directory in your system.

4.  Switch to EditorConfig jEdit Plugin project root directory and run `ant`.

5.  If succeeded, Plugin should be built in `build/jar/`. Copy
    `build/jar/EditorConfigPlugin.jar` to your jEdit plugin directory (this
    should be `~/.jedit/jars` on UNIX and
    `${JEDIT_INATALLATION_DIRECTORY}/jars` on Windows).

6.  If jEdit is running, restart jEdit.

For example, on Debian, the commands are like this:

```Shell
$ sudo apt-get install subversion ant git
$ git clone git://github.com/editorconfig/editorconfig-jedit.git
$ svn co https://jedit.svn.sourceforge.net/svnroot/jedit/build-support/trunk build-support
$ cd editorconfig-jedit
$ cp build.properties.in build.properties # Copy build properties and modify jedit.install.dir as needed
$ ant
$ cp ./build/jar/EditorConfigPlugin.jar ~/.jedit/jars
```

## Supported properties

The EditorConfig jEdit plugin supports the following EditorConfig [properties][]:

* indent_style
* indent_size
* tab_width
* end_of_line
* root (only used by EditorConfig core)


[ant]: http://ant.apache.org
[EditorConfig]: http://editorconfig.org
[EditorConfig core]: https://github.com/editorconfig/editorconfig-core
[jEdit]: http://www.jedit.org
[properties]: http://editorconfig.org/#supported-properties
