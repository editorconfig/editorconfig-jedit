# EditorConfig jEdit Plugin

[![Build Status](https://secure.travis-ci.org/editorconfig/editorconfig-jedit.png?branch=master)](http://travis-ci.org/editorconfig/editorconfig-jedit)

This is an [EditorConfig][] plugin for [jEdit][].

## Installation

1.  Download [ant][] and install it.

2.  Copy `build.properties.in` to `build.properties`. Edit the
    `build.properties` file and modify `jedit.install.dir` to the jEdit
    installation directory in your system.

3.  Switch to EditorConfig jEdit Plugin project root directory and run `ant`.

4.  If succeeded, Plugin should be built in `build/jar/`. Copy
    `build/jar/EditorConfigPlugin.jar` and `build/jar/editorconfig.jar` to your
    jEdit plugin directory (this should be `~/.jedit/jars` on UNIX and
    `${JEDIT_INATALLATION_DIRECTORY}/jars` on Windows).

5.  If jEdit is running, restart jEdit.

For example, on Debian, the commands are like this:

```Shell
$ sudo apt-get install ant git
$ git clone git://github.com/editorconfig/editorconfig-jedit.git
$ git submodule update --init
$ cd editorconfig-jedit
$ cp build.properties.in build.properties # Copy build properties and modify jedit.install.dir as needed
$ ant
$ cp ./build/jar/*.jar ~/.jedit/jars
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
