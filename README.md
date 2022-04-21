# Starlify connector for mule gateway
Exports the mulesoft api details to starlify as Service, System and Flow.

## Dependencies
1. Java-8 +

### spring-boot-starter-web
For exposure of connector etc. on http.



## Start
First clone the project using below link
https://github.com/entiros/starlify-mulesoft-anypoint-platform-connector.git

## Configuration
Make sure proper MuleSoft api gateway and starlify url's configured properly in properties file like this

```
mulesoft:
  server:
    url: https://anypoint.mulesoft.com
starlify:
  url: https://api.starlify.com
```

Go to cloned location and run below command to start the process
mvn clean spring-boot:run

## import mule api details to Starlify
Use below endpoint to start importing api details to starlify as services, systems and flows

```
	Method : POST
	URL : http://localhost:8080/submitRequest
	Body : 
			{
				"starlifyKey":"starlify-api-key",
				"apiKey":"mulesoft-api-key",
				"networkId":"starlify-network-id-to-create-services-systems-and-flows"
			}
```

## Output
After successful request submission, you should be able to see all the systems and services from mulesoft in give starlify network.
