import org.hyperic.sigar.OperatingSystem;
import org.cloudifysource.dsl.utils.ServiceUtils;
import org.cloudifysource.utilitydomain.context.ServiceContextFactory;
import java.util.concurrent.TimeUnit;

println "webapp_preStart.groovy: Starting ..."

def context = ServiceContextFactory.getServiceContext()
def instanceID = context.getInstanceId()

def ctxPath=("default" == context.applicationName)?"":"${context.applicationName}"

println "webapp_preStart.groovy: Installing webapp.js..."

def home = "${context.serviceDirectory}"

context.attributes.thisInstance["home"] = "${home}"

println "webapp_preStart.groovy: webapp(${instanceID}) home is ${home}"

// ----------------------------------------------------------------------------------------
// MYSQL
def mysqlService = context.waitForService("mysql", 180, TimeUnit.SECONDS)
def mysqlInstance = context.attributes.mysql.instances[1]
def mysqlIP = mysqlInstance["dbHost"]


// -----------------------------------------------------------------------------------------
// Ubuntu installation

def configureOnUbuntu(ant,context,mysqlIP)
{
	ant.sequential 
	{
		chmod(dir:"${context.serviceDirectory}", perm:"+x", includes:"*.sh")
		echo(message:"webapp_preStart.groovy: calling install.sh with arg ${mysqlIP}")	
		exec(executable: "${context.serviceDirectory}/configure.sh",failonerror: "true") {
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
			configureOnUbuntu(ant,context,mysqlIP)
			break                                        
	default: throw new Exception("Support for ${currVendor} is not implemented")
}

println "webapp_preStart.groovy: node.js configuration ended"
