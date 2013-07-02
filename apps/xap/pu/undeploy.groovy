import org.cloudifysource.dsl.context.Service
import org.cloudifysource.dsl.context.ServiceInstance
import org.openspaces.admin.AdminFactory
import org.openspaces.admin.gsm.GridServiceManagers
import org.openspaces.admin.pu.ProcessingUnit;

import java.util.concurrent.TimeUnit
import org.openspaces.admin.space.SpaceDeployment;
import org.cloudifysource.dsl.context.ServiceContextFactory

def pus = args[0]

def context = ServiceContextFactory.getServiceContext()
locators = context.attributes.thisApplication["locators"]
admin = new AdminFactory().useDaemonThreads(true).addLocator(locators).create();
String gsHome = context.attributes.thisApplication["home"]

println "start undeploy"
def mgtService = context.waitForService("mgt", 180, TimeUnit.SECONDS)
mgtInstances = mgtService.waitForInstances(mgtService.numberOfPlannedInstances, 30, TimeUnit.SECONDS)
def puService = context.waitForService("pu", 180, TimeUnit.SECONDS)

instanceID = context.getInstanceId();

if (instanceID == 1) {
  println "found instance"
	
	println "waiting for gsm ..."
	GridServiceManagers gridServiceManagers = admin.getGridServiceManagers();
	gridServiceManagers.waitFor(1, 120, TimeUnit.SECONDS);
	println "gsm is ready..."

		StringTokenizer grids = new StringTokenizer(dataGrids, ",");
		StringTokenizer primaries = new StringTokenizer(dataGridsPrimary, ",");
		StringTokenizer backups = new StringTokenizer(dataGridsBackups, ",");
		
		while (pus.hasMoreElements()) {
			def pu = pus.nextElement();
			//undeploy
		}
}

			
println "undeploy closing admin"
admin.close();

println "undeploy End"