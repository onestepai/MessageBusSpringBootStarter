package com.one_step.messagebusspringbootstarter;

import com.one_step.messagebusspringbootstarter.model.MessageBusListenResult;
import com.one_step.messagebusspringbootstarter.model.MessageBusResult;
import com.one_step.messagebusspringbootstarter.model.messagebus.EventData;
import com.one_step.messagebusspringbootstarter.service.MessageBusTemplate;
import org.junit.Assert;
import org.junit.Test;


public class MessageTest {

    @Test
    public void testSendMessage() {
        MessageBusTemplate messageBusTemplate = new MessageBusTemplate("https://dev-cn1.api.1stepai.cn/message-bus", "testClient");
        MessageBusResult result = messageBusTemplate.sendMessage("1f69c669-a6f1-463e-bf5b-4ccbdb7ab966", "test message");
        Assert.assertEquals(result.getIsSuccess(), true);
    }

    @Test
    public void testGetMessage() throws InterruptedException {
        Object obj =  new Object();
        MessageBusTemplate messageBusTemplate = new MessageBusTemplate("https://dev-cn1.api.1stepai.cn/message-bus", "testClient");
        MessageBusListenResult result = messageBusTemplate.listen("1f69c669-a6f1-463e-bf5b-4ccbdb7ab966", (EventData data) -> {
            System.out.println("message data receive:" + data.getData());
            messageBusTemplate.stopListen(data.getToken());
            synchronized (obj) {
                obj.notifyAll();
            }
        });
        Assert.assertEquals(result.getIsSuccess(), true);
        synchronized (obj) {
            obj.wait();
        }
    }
}
