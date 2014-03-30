# EditorConfig jEdit Plugin

[![Build Status](https://secure.travis-ci.org/editorconfig/editorconfig-jedit.png?branch=master)](http://travis-ci.org/editorconfig/editorconfig-jedit)

This is an [EditorConfig][] plugin for [jEdit][].

## Installation

### Install from jEdit Plugin Manager (Preferred)

1.  Launch the jEdit plugin manager. It's in **Plugins** - **Plugin Manager...**.

2.  Click on the **Install** tab, find **EditorConfig** in the plugin list.
    Check it and click on the **Install** button.

If you have problem using the Plugin Manager, you can also download the binaries
from [here](http://plugins.jedit.org/plugins/?EditorConfig).

### Install from Source

1.  Make sure that submodules are checked out and up-to-date:

        git submodule update --recursive --init

2.  Download [ant][] and install it.

3.  In the EditorConfig jEdit Plugin project root directory, get the jEdit
    build-support files:

        svn co https://svn.code.sf.net/p/jedit/svn/build-support/trunk build-support

4.  Copy `build.properties.in` to `build.properties`. Edit the
    `build.properties` file and modify `jedit.install.dir` to the jEdit
    installation directory in your system.

5.  Switch to EditorConfig jEdit Plugin project root directory and run `ant`.

6.  If succeeded, Plugin should be built in `build/jars/`. Copy
    `build/jars/EditorConfigPlugin.jar` and `build/jars/editorconfig.jar` to your
    jEdit plugin directory (this should be `~/.jedit/jars` on UNIX and
    `${JEDIT_INATALLATION_DIRECTORY}/jars` on Windows).

6.  If jEdit is running, restart jEdit.

For example, on Debian, the commands are like this:

```Shell
$ sudo apt-get install ant git
$ git clone git://github.com/editorconfig/editorconfig-jedit.git
$ git submodule update --init --recursive
$ cd editorconfig-jedit
$ svn co https://jedit.svn.sourceforge.net/svnroot/jedit/build-support/trunk build-support
$ cp build.properties.in build.properties # Copy build properties and modify jedit.install.dir as needed
$ ant
$ cp ./build/jars/*.jar ~/.jedit/jars
```

## Supported properties

The EditorConfig jEdit plugin supports the following EditorConfig [properties][]:

* indent_style
* indent_size
* tab_width
* end_of_line
* charset
* root (only used by EditorConfig core)

In addition, this plugin also supports a specific property which is only valid for jEdit:

* jedit_charset

The usage of this property is similar to `charset`, but the value is the
encoding string defined by jEdit, and is case sensitive. If both `charset` and
`jedit_charset` are present, only `charset` will be used.

## Bugs and Feature Requests

Feel free to submit bugs, feature requests, and other issues to the main
[EditorConfig issue tracker](https://github.com/editorconfig/editorconfig/issues).


[ant]: http://ant.apache.org
[EditorConfig]: http://editorconfig.org
[EditorConfig core]: https://github.com/editorconfig/editorconfig-core
[jEdit]: http://www.jedit.org
[properties]: http://editorconfig.org/#supported-properties
