# X-Platform Java Linter

![Build](https://github.com/TheNCuber/intellij-linter-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/18018-x-platform-java-linter.svg)](https://plugins.jetbrains.com/plugin/18018-x-platform-java-linter)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/18018-x-platform-java-linter.svg)](https://plugins.jetbrains.com/plugin/18018-x-platform-java-linter)

<!-- Plugin description -->
This IntelliJ-based Plugin allows you to define custom platform-independent linters in a linter.xml config file in the .idea folder of your project.
This plugin was built as a seminar project for the Software Composition Group at the University of Bern and serves as proof of concept.
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "X-Platform Java Linter"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/TheNCuber/intellij-linter-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Getting started / Usage

- To use the functionality of the plugin you need to create a file <kbd>linter.xml</kbd> within your project's <kbd>.idea</kbd> folder.
- The example xml specification below shows the currently supported matcher types that you can specify in the <kbd>linter.xml</kbd>.
- When you have update the <kbd>linter.xml</kbd> according to the preferences you can apply the changes to the linting rules by pressing the small ![Update rules](https://raw.githubusercontent.com/TheNCuber/intellij-linter-plugin/main/src/main/resources/icons/refresh_16.svg) button in the toolbar.
- When the rules were updated successfully you receive a success notification in IntelliJ.

### Example linter.xml

```xml
<?xml version="1.0" encoding="utf-8" ?>
<linters>
    <linter name="Issue 001: 'Hello World' in production releases" language="Java">
        <note>This linter highlights String literals that match the regex "Hello World".</note>
        <severity>WARNING</severity>
        <matcher type="literal">
            <literal>Hello World</literal>
        </matcher>
    </linter>
    <linter name="Issue 002: Lower camelcase for variables" language="Java">
        <note>This linter highlights variable names that match the provided regex for lower camelcase naming.</note>
        <severity>ERROR</severity>
        <matcher type="variable">
            <variable>^([A-Z][a-z0-9]*)+</variable>
        </matcher>
    </linter>
    <linter name="Issue 003: Method highlighting" language="Java">
        <note>This linter highlights methods that match the given return type, name, modifiers and parameter set.</note>
        <severity>INFORMATION</severity>
        <matcher type="method">
            <method returnType="void" name="myMethodName">
                <modifier>static</modifier>
                <modifier>synchronized</modifier>
                <parameter type="int" />
                <parameter type="java.lang.String" />
            </method>
        </matcher>
    </linter>
    <linter name="Issue 004: Class highlighting" language="Java">
        <note>This linter highlights classes that match the given name, superClass, interfaces, and methods.</note>
        <severity>WARNING</severity>
        <matcher type="class">
            <class name="myClass" superClass="mySuperClass">
                <interface name="myInterface1" />
                <interface name="myInterface2" />
                <method returnType="void" name="myMethod1">
                    <modifier>synchronized</modifier>
                    <parameter type="int" />
                </method>
                <method returnType="void" name="myMethod2"></method>
            </class>
        </matcher>
    </linter>
</linters>
```
---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
