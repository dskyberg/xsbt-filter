sys.props.get("plugin.version") match {
  case None =>
    throw new RuntimeException(
      """|The system property 'plugin.version' is not defined.
         |Please specify this property using the SBT flag -D.""".stripMargin)
  case Some(pluginVersion) =>
    addSbtPlugin("org.dskyberg.sbt" % "xsbt-filter" % pluginVersion)
}
