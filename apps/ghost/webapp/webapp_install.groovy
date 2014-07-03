import org.hyperic.sigar.OperatingSystem;
import org.cloudifysource.dsl.utils.ServiceUtils;
import org.cloudifysource.utilitydomain.context.ServiceContextFactory;
import java.util.concurrent.TimeUnit;

println "webapp_install.groovy: Starting ..."

def context = ServiceContextFactory.getServiceContext()
def instanceID = context.getInstanceId()

def ctxPath=("default" == context.applicationName)?"":"${context.applicationName}"

println "webapp_install.groovy: Installing webapp.js..."

def home = "${context.serviceDirectory}"

context.attributes.thisInstance["home"] = "${home}"

println "webapp_install.groovy: webapp(${instanceID}) home is ${home}"

// ----------------------------------------------------------------------------------------
// MYSQL
def mysqlInstance = context.attributes.mysql.instances[1]
def mysqlIP = mysqlInstance["dbHost"]


// -----------------------------------------------------------------------------------------
// Ubuntu installation

def installOnUbuntu(ant,context,mysqlIP)
{
	ant.sequential 
	{
		echo(message:"webapp_install.groovy: installing on Ubuntu...")
		mkdir(dir:'install')
		
		chmod(dir:"${context.serviceDirectory}", perm:"+x", includes:"*.sh")
		chmod(dir:".", perm:"+x", includes:"*.sh")
		exec(executable: "${context.serviceDirectory}/install_deps.sh",failonerror: "true")
		echo(message:"webapp_install.groovy: calling install.sh with args ${context.privateAddress} ${mysqlIP}")	
		exec(executable: "${context.serviceDirectory}/install.sh",failonerror: "true") {
			arg(value:"${context.privateAddress}")
			arg(value:"${mysqlIP}")
		}
	}
}

ant = new AntBuilder()
def os = OperatingSystem.getInstance()
def currVendor=os.getVendor()
switch (currVendor) {
	case ["Ubuntu"]:                        
			installOnUbuntu(ant,context,mysqlIP)
			break                                        
	default: throw new Exception("Support for ${currVendor} is not implemented")
}

println "webapp_install.groovy: node.js installation ended"
