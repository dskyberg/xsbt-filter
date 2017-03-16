This started as a fork of [guersam/xsbt-filter (branch tmp/autoplugin)](https://github.com/guersam/xsbt-filter/tree/tmp/autoplugin),
which started as a forg of [sdb/xsbt-filter](https://github.com/sdb/xsbt-filter).  You
should review that project first, before reading this.

## Setup

Add the following to your plugin configuration (in `project/plugins/build.sbt`):

```scala
addSbtPlugin("org.dskyberg.sbt" % "xsbt-filter" % "0.5.0")
```

Add `FilterPlugin` to your `enablePlugins`:

```scala
enablePlugins(FilterPLugin)
```

or

```scala
lazy val root = project.in(file("."))
  .enablePlugins(FilterPlugin)
```

## Build

Build and install the plugin to use the latest SNAPSHOT version:

```sh
$> git clone git://github.com/dskyberg/xsbt-filter.git
$> cd xsbt-filter
$> sbt publish-local
```

## Default Configuration

The sdb/xsbt-filter plugin supported the following default properties:
* The following SBT setting keys (prepended with "project.")
    * organization 
    * name
    * description
    * version
    * scalaVersion
    * sbtVersion
* system properties (prepended with "sys.")
* user-defined properties the settingKey `filterExtraProps`

This plugin adds the following:
* The following SBT setting keys (also prepended with "project."):
    * moduleName
    * homepage
    * scalaBinaryVersion
     * sbtBinaryVersion
    * baseDirectory
    * target
    * sourceDirectory
    * classDirectory
    * compileScalaSource
    * compileJavaSource
    * compileResourceDirectory
    * testScalaSource
    * testJavaSource
    * testResourceDirectory
  * environment variables (prepended with "env.")

## Settings

Take a look at the source code for [FilterPlugin](https://github.com/sdb/xsbt-filter/blob/master/src/FilterPlugin.scala) for all settings.

Note: Because this version is an AutoPlugin, you do not need to add any includes to your 
`.sbt` files.  Just follow the [Setup](#setup) above.

### Filtering resources
The standard copyResources settingKeys are also used by FilterPlugin to define
which resources to act on.

Use the `includeFilter` and `excludeFilter` settings in `filterResources` to change 
which of the `unmanagedResources` FilterPlugin will actuall filters: 

```scala
includeFilter in (Compile) :=  AllPassFilter
excludeFilter in (Compile) := HiddenFileFilter || "*.bmp" 
includeFilter in (Compile, filterResources) ~= { f => f || ("*.props" | "*.conf") }
```

### Filter pattern
You can set `filterPattern` to change the regex pattern used to identify properties
in resources.  The default pattern looks for patterns like`${property}`.  Defined by
the regular expression `(^|[^\\])(\$\{)([^}]+?)(\})`.  If you wish to change the
pattern, your regex must provide 4 groups:  
1. Text before the exchange pattern 
2. Exchange start identifier, such as `\$\{`
3. Property name, such as `project.name`
5. Excnahge end identifier, such as `\}'


### filterUseHandlebars
While the standard dollar sign (${property} is the default pattern. But you can 
opt to use handlebars instead ({{property}}) by setting the following in your `.sbt` file:

```scala
// use handlebars, instead of the default pattern
filterUseHandlebars in Compile := true
```

## License

This project is licensed under the New BSD License.

## Contributing

Fork the repository, push your changes to a new branch and send me a merge request.

## Build, test & publish

### Tests

    sbt scripted

### Publish

See [Deploying to Sonatype](http://www.scala-sbt.org/release/docs/Community/Using-Sonatype.html) and [Sonatype OSS Maven Repository Usage Guide](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide#SonatypeOSSMavenRepositoryUsageGuide-8.ReleaseIt) for more information.

    sbt publish

