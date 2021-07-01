# Microservices-with-Spring

# Centralized Configuration Pattern
Why?

* Traditional configuration methods

  1. Program Arguments: Set configuration properties that gets passed on to to your application main method.
  
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
  
  2. System Properties/VM Options: Set configuration properties for the JVM instance created for your application, so that only your app would be able to access it.
  
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
            
  3. Environment Variables: Set configuration properties at the OS level which would be available for every application running on the machine.
      
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
  
  
* Modern Centralized configuration method

  1. Spring Cloud Config Server

```
