# Microservices-with-Spring

# Centralized Configuration Pattern
Why?

* ## Traditional configuration methods

  1. ### Program Arguments: Set configuration properties that gets passed on to to your application main method.
  
      * Set Arguments
      
          ```
          java -jar app.jar 8080 myApp
          ```
          
      * Access Arguments
      
          ```
          public static void main(String args[]){
              port = args[0];
              appName = args[1]
          }
          ```
  
  
  
  2. ### System Properties/VM Options: Set configuration properties for the JVM instance created for your application, so that only your app would be able to access it.
  
      * Set VM Options
      
          ```
          java -jar app.jar -Dserver.port=8060 -Dspring.application.name=myApp
          ```
      
      * Access VM Options
      
          * Using System Class
          
            ```
            System.getProperty("port");
            System.getProperty("name");
            ```
            
          * Injecting using @Value annotation 
          
            ```
            int port;
            
            //You could do field injection as well but its not safe as your contructor will get called first then spring will inject fields 
            //so if you try to access the injected field within contructor code you will get null
            public MyConstructor( @Value("port:8080") int port ) {    
            
                //:8080 after port is a default value in case your application can't find the property.
                this.port = port;
            
            }
            ```
            
            
            
  3. ### Environment Variables: Set configuration properties at the OS level which would be available for every application running on the machine.
      
     * Set Environment variables
        
        ```
        export PORT=8080
        export DATA_SOURCE=8080
        ```
        
     * Access Environemnt Variable
        
        * Using Environement Map<K,V>
     
            1. Using System.getEnv()

                ```
                System.getEnv().get("PORT")
                ```

            2. Injecting Environment  

                ```
                @Autowired
                Environment env;

                env.getProperty();
                ```
            
        * Injecting using @Value annotation 
          
            ```
            int port;
            
            //You could do field injection as well but its not safe as your contructor will get called first then spring will inject fields 
            //so if you try to access the injected field within contructor code you will get null
            public MyConstructor( @Value("port:8080") int port ) {    
            
                //:8080 after port is a default value in case your application can't find the property.
                this.port = port;
            
            }
            ```
            
            
            
* ## Externalized Configurations using application-<sit|dev|..|prod>.properties/yml files
       
    1. ### Create and keep these files in the src/main/resources folder 
       This works by setting all the properties mentioned in the files as environement variables.
       This will be bundled and kept within the jar.

          * Set properties by putting it in application-<sit|dev|..|prod>.properties/yml files

              application.properties
              ```
              server.port=8080
              spring.profiles.active=dev
              ```

              OR

              application.yml
              ```
              server:
                 port:8080
              spring:
                  profiles:
                      active: dev
              ```

          * Access the properties

              Will be same as we use to access environment variables above


  2. ### Keep these files in the folder where jar is present
      This way it will automatically override the existing application properties present.

  3. ### Use CMD to override few properties in the files

      ```
      --server.port = 8080
      ```

  4. ### Use CMD and specify the path to property files if not present in the same directory or within jar

      ```
      -Dspring.config.location = C:myproperties/application.properties
      ```
      

* ## Modern Centralized configuration method

  1. ### Creating a config git repository on local or remote(github, bitbucket).

      * #### Configure 1 repository for multiple microservices
          * Put all the application-<sit|dev|..|prod>.properties/yml 
              
              
      * #### Configure multiple repositories for multiple microservices
          * Create folder for each micro-service and put your configuration for that micro-service in the respective folder


  2. ### Spring Cloud Config Server
     A separate spring application to act as a Central Configuration server for multiple applications where configuration are stored in a central git repository.

      1. #### Dependency
          
          ```
          implementation 'org.springframework.cloud:spring-cloud-config-server'
          ```
          
      2. #### Annotate the application

          ```
          @EnableConfigServer
          ```
          
      3. #### Add configuration properties

          ```
          spring:
            application:
              name: config-server
            cloud:
              config:
                server:
                  git:
                    uri: https://github.com/itsdevbrat/cloud-config-server
                    cloneOnStart: true
                    timeout: 4
                    default-label: main
                    
                    ## The below configuration is only needed if you have kept config for multiple micro-services in one repo otherwise the config till here is enough
                    repos:
                      userservice:
                        pattern: userservice
                        uri: https://github.com/itsdevbrat/cloud-config-server
                        search-paths: userservice/
                        cloneOnStart: true
                        timeout: 4
                        default-label: main

                      companyservice:
                        pattern: companyservice
                        uri: https://github.com/itsdevbrat/cloud-config-server
                        search-paths: companyservice/
                        cloneOnStart: true
                        timeout: 4
                        default-label: main
                        
            ```
        4. #### Run the server before running your application.

  3. ### Spring Cloud Config Client within your microservice

      1. #### Dependency

          ```
          implementation 'org.springframework.cloud:spring-cloud-starter-config'
          ```
          
      2. #### Add configuration properties

          ```
          config:
            import: optional:configserver:http://localhost:8000/
          ```
          
      3. #### To fetch properties for a specific environment from config server

          ```
          spring:
            profiles:
              active: dev
          ```
          
  4. ### Refresh properties while the JAR is running without redeploying the JAR

      1. #### Annotate the class where you are accessing these properties with @RefreshScope
          
          ```
          @RefreshScope
          public class UserController {
            @Value("my.property")
            String myProperty;
          }
          ```
      2. #### Use either an actuator or a message bus 
      
         * Using Spring Boot Starter Actuator

             1. Add Dependency

                ```
                implementation 'org.springframework.boot:spring-boot-starter-actuator'

                ```
              
             2. Add configuration for actuator to expose a refresh endpoint which forces the microservice to refresh the application properties

                ```
                management:
                  endpoints:
                    web:
                      exposure:
                        include: "/refresh"
                ```
                
              3. After JAR is deployed refresh the properties by making a **POST** call with empty body to http(s)://application-url/actuator/refresh

              


          
          

          
    
    
    
    
