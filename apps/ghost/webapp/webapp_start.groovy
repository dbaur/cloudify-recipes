import java.util.concurrent.TimeUnit
import org.hyperic.sigar.OperatingSystem
import org.cloudifysource.dsl.utils.ServiceUtils;
import org.cloudifysource.utilitydomain.context.ServiceContextFactory

println "webapp_start.groovy: Run Ghost server"

def serviceContext = ServiceContextFactory.getServiceContext()
def instanceID = serviceContext.getInstanceId()
def home= serviceContext.attributes.thisInstance["home"]

println "webapp_start.groovy: webapp(${instanceID})"

ant = new AntBuilder()
def os = OperatingSystem.getInstance()
def currVendor=os.getVendor()
switch (currVendor) {
                case ["Ubuntu"]:                        
                        builder = new AntBuilder()
						builder.sequential {
							echo(message:"webapp_start.groovy: Running npm ...")
							chmod(dir:"${serviceContext.serviceDirectory}", perm:"+x", includes:"*.sh")
							exec(executable:"${serviceContext.serviceDirectory}/start.sh", failonerror: "true")
						}
                        break                                        
                default: throw new Exception("Support for ${currVendor} is not implemented")
}
