import akka.actor.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.nats.client.ConnectionFactory;
import io.nats.client.Message;
import io.nats.connector.plugin.NATSConnector;
import io.nats.connector.plugin.NATSConnectorPlugin;
import io.nats.connector.plugin.NATSEvent;
import org.slf4j.Logger;

public class AkkaConnector implements NATSConnectorPlugin
{
    Logger logger = null;
    NATSConnector connector = null;
    ActorSystem system = null;
    ActorRef publisher = null;
    ActorRef subscriber = null;

    public boolean onStartup(Logger logger, ConnectionFactory connectionFactory) {
        this.logger = logger;
        return true;
    }

    public boolean onNatsInitialized(NATSConnector natsConnector) {
        Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + 9000).withFallback(
                ConfigFactory.load());
        this.system = ActorSystem.create("nats-akka", config);
        this.publisher = system.actorOf(Props.create(NATSAkkaPublisher.class), "nats-publisher");
        try {
            connector = natsConnector;
            connector.subscribe("*");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        subscriber = system.actorOf(NATSAkkaSubscriber.props(connector), "nats-subscriber");
        return true;
    }

    public void onNATSMessage(Message message) {
        publisher.tell(message, null);
    }

    public void onNATSEvent(NATSEvent natsEvent, String s) {
        logger.info(s);
    }

    public void onShutdown() {
        // TODO: Shut down subscriber
    }
}
