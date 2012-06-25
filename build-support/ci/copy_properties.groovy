#!/usr/bin/env groovy
/* This script copies properties provided as System properties in Hudson into actual properties files used by jEdit's
 * Build process.
 */
 
def props = System.properties
def env = System.env

String workspace = props.get("user.dir")
String storeComment = "## THIS FILE IS GENERATED BASED ON SYSTEM PROPERTIES SUPPLIED TO HUDSON, AND IS USUALLY REPLACED ON EACH BUILD."

println "Copying properties over to appropriate directories"
def coreProps = new Properties() 
def pluginProps = new Properties()
props.each { key, value ->
   if(key.startsWith("je.ci.")) {
      println "jEdit CI prop found - ${key}: ${value}"
      if(key.startsWith("je.ci.pl.")) {
         def cleanKey = key - 'je.ci.pl.'
         println "   clean key: ${cleanKey}"
         pluginProps.setProperty(cleanKey, value)
      } else {
         def cleanKey = key - 'je.ci.'
         println "   clean key: ${cleanKey}"
         coreProps.setProperty(cleanKey, value)
      }
   }
}
File corePropsFile = new File(workspace, "jedit/build.properties")
// make sure stuff exists.
corePropsFile.parentFile.exists() ?: corePropsFile.parentFile.mkdirs() ?: {
   System.err.println("'${corePropsFile.parentFile}' did not exist, and could not be created. Exiting.")
   System.exit(1)
}()
corePropsFile.exists() ?: corePropsFile.createNewFile() ?: {
   System.err.println("'${corePropsFile}' did not exist, and could not be created. Exiting.")
   System.exit(1)
}()
coreProps.store(corePropsFile.newWriter(), storeComment)

File pluginPropsFile = new File(workspace, "jedit/jars/build.properties")
// make sure stuff exists.
pluginPropsFile.parentFile.exists() ?: pluginPropsFile.parentFile.mkdirs() ?: {
   System.err.println("'${pluginPropsFile.parentFile}' did not exist, and could not be created. Exiting.")
   System.exit(1)
}()
pluginPropsFile.exists() ?: pluginPropsFile.createNewFile() ?: {
   System.err.println("'${pluginPropsFile}' did not exist, and could not be created. Exiting.")
   System.exit(1)
}()
pluginProps.putAll(coreProps)
pluginProps.store(pluginPropsFile.newWriter(), storeComment)
/* ::mode=groovy:noTabs=true:maxLineLen=120:wrap=soft:: */
