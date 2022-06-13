package com.one_step.messagebusspringbootstarter.autoconfigure;

import com.one_step.messagebusspringbootstarter.service.MessageBusTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(MessageBusTemplate.class)
@EnableConfigurationProperties(MessageBusProperties.class)
public class MessageBusAutoConfiguration {

    @Autowired
    private MessageBusProperties messageBusProperties;

    @Bean
    @ConditionalOnMissingBean
    public MessageBusTemplate messageBusTemplate() {
        return new MessageBusTemplate(messageBusProperties.getUrl(), messageBusProperties.getClientId());
    }
}
