import java.util.concurrent.TimeUnit;

service {
	name "webapp"
	type "APP_SERVER"

	elastic true	
	numInstances 1
	minAllowedInstances 1
	maxAllowedInstances 3
	
	def instanceId = context.instanceId
	
	compute {
		template "SMALL_LINUX"
	}
	lifecycle {
	
		details {
			def currPublicIP = context.publicAddress			
			def applicationURL = "http://${currPublicIP}:2368"
			println "webapp-service.groovy: applicationURL is ${applicationURL}"
			
			return [
				"Application URL":"<a href=\"${applicationURL}\" target=\"_blank\">${applicationURL}</a>"
			]
		}	
	
		preInstall {
			def mysqlService = context.waitForService("mysql", 180, TimeUnit.SECONDS)
		}		
	
		install "webapp_install.groovy"
		
		start   "webapp_start.groovy"

		postStart {
            		def apacheService = context.waitForService("apacheLB", 180, TimeUnit.SECONDS)
		        def privateIP = System.getenv()["CLOUDIFY_AGENT_ENV_PRIVATE_IP"]
            		def instanceID = context.instanceId
            		def currURL="http://${privateIP}:2368"
            		apacheService.invoke("addNode", currURL as String, instanceID as String)
        	}

        	postStop {
            		try {
                		def apacheService = context.waitForService("apacheLB", 180, TimeUnit.SECONDS)
                		if ( apacheService != null ) {
                    			def privateIP =System.getenv()["CLOUDIFY_AGENT_ENV_PRIVATE_IP"]
                    			def instanceID = context.instanceId
                    			def currURL="http://${privateIP}:2368"
                    			apacheService.invoke("removeNode", currURL as String, instanceID as String)
                		}
            		}
            catch (all) {
                println "app-service.groovy: Exception in Post-stop: ${all}"
            }
        }
		
	   	startDetectionTimeoutSecs 800
		startDetection {                        
				def privateIP = System.getenv()["CLOUDIFY_AGENT_ENV_PRIVATE_IP"]
				ServiceUtils.isPortOccupied("${privateIP}",2368)
		}        
		
		locator {                                           
               		def myPids = ServiceUtils.ProcessUtils.getPidsWithQuery("State.Name.eq=node")
               		println ":current PIDs: ${myPids}"
               		return myPids
		}            
	}
}
