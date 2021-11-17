# Reactive Microservices-with-Spring

# WebFlux 
Why?
Blocking and synchronous code is resources intensive as it blocks threads and hence less performant in terms of handling requests in an heavy IO based application.

## Foundations

### [Reactive Systems](https://www.reactivemanifesto.org/)
This has to do with distributed systems and nothing with programming styles.
This defines the standards/architectural principles for your system as a whole (which could comprise of n number of reactive/synchronous applications) to be reactive.
It is used to achieve resilience and elasticity through message passing (i.e communication, and coordination, of distributed systems by passing clear destined messages) and hence referred to as message-driven.


### [Reactive programming](https://en.wikipedia.org/wiki/Reactive_programming#Approaches_to_creating_reactive_programming_languages)
Data Streams + Propogation Of Change
is a paradigm where the availability of new information drives the logic forward rather than having control flow driven by a thread-of-execution 
It suggests decomposing a problem into multiple discrete steps where each can be executed in an asynchronous(not existing or occurring at the same time) and non-blocking fashion, and then be composed to produce a workflow
It is Event driven 

#### Two API options to build reactive programming frameowrks
 
  ##### 1. Passing Callbacks
  i.e passing callback function to specify the logic once data is available
 
  ##### 2. Declarative using functional composition
  i.e using map, filter, etc to specify the logic to be executed on data as and when available
           
#### Programming abstractions for Reactive Programming

  ##### 1. Futures 
  (Before JAVA 8)
  ##### 2. CompletableFutures 
  (After JAVA 8, since Futures use to block current thread when get() method is called)
  ##### 3. Reactive Streams 
  (Completable futures didn't had features to handle backpressure, hence reactive streams specifically designed to add this extra feature)
  
  
### Event driven vs Message driven
In Message driven, components pass clear messages destined to a particular component.
In Event driven, components just emit events when they reach a particular state and other components observe/listen to that.




### [Reactive Streams](https://www.reactive-streams.org/)
It is a JVM standard/set of interfaces defined by group of companies(incl. Netflix) to do reactive programming.
It specifies the design to do Async Stream processing with Non-Blocking Backpressure following a Push based model.
Reactive Streams = Push based Iterable Pattern + Observer Pattern + Functional Programming.

It consists of 4 interfaces
  1. Publisher (Subscribers this interfaces to subscribe to the publisher
     ```
     public interface Publisher<T> {
         public void subscribe(Subscriber<? super T> s);
     }
     ```
     
  2. Subscriber (Publishers use this interface to emit events to Suscriber)
     ```
     public interface Subscriber<T> {
        public void onSubscribe(Subscription s);
        public void onNext(T t);
        public void onError(Throwable t);
        public void onComplete();
     }
     ```
  3. Subscription (When subscriber subscribe to a publisher a subscription is created)
     ```
     public interface Subscription {
        public void request(long n);
        public void cancel();
     }
     ```
  4. Processor (This is both a publisher and subscriber as it subscribes to the publisher get the data and after processing publishes those to subscribers)
     ```
     public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {
     
     }
     ```



# Service Discovery Pattern
Why? 
IPs, ports of services could change hence hardcoding it would need service redeployment in case IP or PORT changes
Horizontally scale services and load balance across them(supports client side load balncing with Ribbon)
Health monitoring and routing based on it.

* ## Discovery Ckient

  1. ### Add the below dependency to your microservice

      ```
      implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
      ```
  2. ### Annotate your microservice application class which is now an optional step as spring does it by default once dependency is added

      ```
      @SpringBootApplication
      @ENableEurekaClient
      public class UserserviceApplication { ..... }
      ```
      
  3. ### Create a Ribbon based rest template so that it can load balance from the list of IPs that eureka server will be returning when requested using a service name.

      ```
      @Bean
      @LoadBalanced
      public RestTemplate getRestTemplate(){ 
        return new RestTemplate();
      }
      ```
      
   4. ### Configure the client to talk to server again its optional if your server is running on same machone and on default port 8761

      ```
      eureka:
        client:
          serviceUrl:
            defaultZone: http://localhost:8761
      ```

* ## Discovery Server

  1. ### Create a spring application which wil act as Discovery server

  2. ### Add the below dependency
      
      ```
      implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-server'
      ```
  3. ### Annotate the spring application class as below
  
      ```
      @SpringBootApplication
      @EnableEurekaServer
      public class EurekaserverApplication { ......... }
     ``` 
  4. ### Configure the server

      ```
      server:
        port: 8761

      eureka:
        client:
          #below properties will make sure that eureka doesn't register with itself, 
          #this is only needed when you have a cluster of eureka server or multiple eureka servers
          register-with-eureka: false 
          fetch-registry: false
      ```
    
   5. ### Build the jar or run the application and visit to see the eureka dashboard where you can see the services only if your client microservices are up and running and registered to this server
     
      ```
      http://localhost:8761
      ```

# Centralized Configuration Pattern
Why?
Cemntralized hence easy to manage
Change application properties at runtime
Version control for configuration if stored on git server


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
            
            
  4. Use **@PropertySource** annotation in conjuction with **@Configuration** annotation for Configuration Classes

       ```
       @Configuration
       @PropertySource({
           "classpath:config.properties",
           "classpath:db.properties" //if same property, this will override
       })
       public class AppConfig {
           @Value("hostUrl")
           String hostUrl;
       }
       ```
       
  5. Use **@TestPropertySource** annotation in conjuction with **@SpringTest** annotation for Test Classes

      ```
      @RunWith(SpringRunner.class)
      @TestPropertySource("classpath:foo.properties")
      public class FilePropertyInjectionUnitTest {
        @Value("${foo}")
        private String foo;
      }
      ```
      
  6. Use **@ConfigurationProperties** annotation in conjuction with **@Configuration** annotation for Hierarchical Properties
     
      ```
      @Configuration
      @ConfigurationProperties(prefix = "database") //All the properties that have the prefix **database** will get mapped to the variables defined
      public class Database {
          String url;
          String username;
          String password;
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
  * Switching configuration based on environment(dev, sit, prod)
    
      1. Use CMD and specify the active profile property

        ```
        java -jar app.jar -Dspring.profiles.active=dev
        ```
        
      2. Use @Profile annotation on top of classes for a granular control

        ```
        @Profile("dev")
        @RestController
        public class UserController {
          @Value("downstreamUrl")
          int downstreamUrl;
        }
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
                    deleteUntrackedBranches: true
                    force-pull: true
                    cloneOnStart: true
                    timeout: 4
                    default-label: main
                    
                    ## The below configuration is only needed if you have kept config for multiple micro-services in one repo 
                    ## otherwise the config till here is enough
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
          spring:
            cloud:
              config:
                fail-fast: true
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

              


          
          
          
# Mongo DB setup
## Download MongoDB server
Ubuntu: 
  ```
  sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2930ADAE8CAF5059EE73BB4B58712A2291FA4AD5
  sudo apt install mongodb-org
  ```

## Download Mongo Client (Compass)

## Connect client application to mongodb by specifying the below properties
  ```
  spring:
    data:
      mongodb:
        uri="mongodb://localhost:27017"
  ```
  
  
  
  
  
# Spring Security
## Fundamentals Architecture Components

SecurityContextHolder - The SecurityContextHolder is where Spring Security stores the details of who is authenticated.

SecurityContext - is obtained from the SecurityContextHolder and contains the Authentication of the currently authenticated user.

Authentication - Can be the input to AuthenticationManager to provide the credentials a user has provided to authenticate or the current user from the SecurityContext.

GrantedAuthority - An authority that is granted to the principal on the Authentication (i.e. roles, scopes, etc.)

AuthenticationManager - the API that defines how Spring Security’s Filters perform authentication.

ProviderManager - the most common implementation of AuthenticationManager.

AuthenticationProvider - used by ProviderManager to perform a specific type of authentication.

![image](https://user-images.githubusercontent.com/48611876/131000875-884eef19-32a6-4b40-af55-50a4b8a50f2d.png)

    
    
    
    
