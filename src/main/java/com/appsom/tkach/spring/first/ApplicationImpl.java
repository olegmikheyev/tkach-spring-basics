package com.appsom.tkach.spring.first;

import com.appsom.tkach.spring.first.client.Client;
import com.appsom.tkach.spring.first.events.Event;
import com.appsom.tkach.spring.first.events.EventType;
import com.appsom.tkach.spring.first.loggers.EventLogger;
import com.appsom.tkach.spring.first.loggers.LoggersConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

@Component("app")
public class ApplicationImpl {
    @Autowired
    private Client client;
    @Autowired
    @Qualifier("fileEventLogger")
    private EventLogger defaultLogger;
    @Autowired
    private Map<EventType, EventLogger> loggers;

    public ApplicationImpl() {}

    /*
    public ApplicationImpl(Client client, EventLogger defaultLogger, Map<EventType, EventLogger> loggers) {
        this.client = client;
        this.defaultLogger = defaultLogger;
        this.loggers = loggers;
    }
    */

    public void logEvent(EventType eventType, String msg) {
        String message = msg.replaceAll(client.getId(), client.getFullName());
        Event event = new Event(new Date(), DateFormat.getDateTimeInstance());
        event.setMessage(message);

        EventLogger logger = loggers.get(eventType);
        if (logger == null) {
            logger = defaultLogger;
        }
        logger.logEvent(event);
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        ctx.scan("com.appsom.tkach.spring.first");

        ctx.register(LoggersConfig.class);

        ctx.refresh();

        ApplicationImpl app = (ApplicationImpl) ctx.getBean("app");

        app.logEvent(EventType.INFO, "Some event for user 1");
        app.logEvent(EventType.ERROR, "Some event for user 2");

        ctx.close();
    }
}
