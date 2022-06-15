# 1step-message-bus-springboot-starter

[1step-message-bus-springboot-starter](https://github.com/onestepai/MessageBusSpringBootStarter) is a template for customer to listen/receive/consume the messages with in the platform [1step-message-bus](https://github.com/onestepai/MessageBus).


# Usage
> Customer's downstream services are able to use this template to configure pom.xml and add java code into their project for the messages in message bus platform.
> 
>
# Steps
* ### Step 1: add dependency in pom.xml
```
<dependency>
    <groupId>com.one_step</groupId>
    <artifactId>message-bus-spring-boot-starter</artifactId>
    <version>0.0.2</version>
</dependency>
```

* ### Step 2: add java code in project, for example:
receive message:
```
import com.one_step.messagebusspringbootstarter.service.MessageBusTemplate;
@Slf4j
@Service
public class MessageBusConsumer {
    @Autowired
    private MessageBusTemplate messageBusTemplate;
    
    @Value("${messagebus.topic}")
    private String messageBusTopic;
    
    public String processProgressMessage() {
        return messageBusTemplate.listen(messageBusTopic, eventData -> {
            try {
                // process received message in eventData.getData()
            } catch (Exception e) {
                // raise exception
            }
        }).getClientToken();
    }
}

```
send message:
```
messageBusTemplate.sendMessage(messageBusTopic, "this is a test message");
```
