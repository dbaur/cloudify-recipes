application {
    name = "ghost"
    
    service {
        name = "webapp"
	 dependsOn = ["apacheLB","mysql"]
    }

    service {
	name = "apacheLB"
    }

    service {
	name = "mysql"
    }
}
