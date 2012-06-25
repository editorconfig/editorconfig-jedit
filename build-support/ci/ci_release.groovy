#!/usr/bin/env groovy

/**
 * The Command class makes it easy to create a command line command and execute it.
 */
class Command {
	String name
	Map options
	File directory

	Map execute() {
		run(toParts())
	}

	protected Map run(List<String> commandParts) {
		println "Executing: ${commandParts}"
		def out = new StringBuffer()
		def err = new StringBuffer()
		def p = null;
		long start = System.nanoTime()
		try {
			def pb = new ProcessBuilder()
			pb.command(commandParts.toArray() as String[])
			pb.directory(directory)
			p = pb.start()
		} catch (Exception e) {
			StringWriter writer = new StringWriter()
			e.printStackTrace(new PrintWriter(writer))
			err.append(writer.toString())
		}
		p?.waitForProcessOutput(out, err)
		long end = System.nanoTime()
		long runtime = end - start
		def output = [value: p?.exitValue(), out: out.toString(), err: err.toString()]
		output.execution = [start: start, end: end, runtime: runtime]
		return output
	}

	List<String> toParts() {
		def parts = [name] + options.collect {
			if(it.value) {
				if(it.value instanceof Collection) {
					[it.key, it.value.flatten()]
				} else {
					[it.key, it.value]
				}
			} else {
				[it.key]
			}
		}
		parts.flatten()
	}

	String toString() {
		return toParts().join(" ")
	}
}


def props = System.properties
def env = System.env

String baseUrl = "https://jedit.svn.sourceforge.net/svnroot/jedit/jEdit"

String workspace = props.get("user.dir")

// Delete tag if it already exists
String tagUrl = "${baseUrl}/branches/ci_release_test/${env.release_tag_name}"
def command = new Command(name: "svn", options: [
	"delete": tagUrl,
	"-m": '"Tag deleted by Hudson CI to be recreated."'
])
def output = command.execute()
// we ignore the output. The tag will be created regardless.

// Create tag
command = new Command(name: "svn", options: [
	"copy": ["${baseUrl}/${env.release_branch}", "${tagUrl}"],
	"-m": "\"Tag created by Hudson CI - Version: ${env.release_version}\""
])
output = command.execute()

if(output.value != 0) {
	println " code: ${output.value}"
	println "  err: ${output.err}"
	println "  out: ${output.out}"
	System.exit(output.value)
}

// checkout the new tag.
command = new Command(name: "svn", options: [
	"co": [tagUrl, "jedit"]
])
output = command.execute()
if(output.value == 0) {
	println "Tag checked out into 'jedit' directory"
}
