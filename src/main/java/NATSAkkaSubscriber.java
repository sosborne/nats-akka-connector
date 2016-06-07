import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.japi.Creator;
import io.nats.client.Message;
import io.nats.connector.plugin.NATSConnector;

public class NATSAkkaSubscriber extends UntypedActor {
    NATSConnector connector = null;

    public static Props props(final NATSConnector natsConnector) {
        return Props.create(new Creator<NATSAkkaSubscriber>() {
            private static final long serialVersionUID = 1L;

            public NATSAkkaSubscriber create() throws Exception {
                return new NATSAkkaSubscriber(natsConnector);
            }
        });
    }

    public NATSAkkaSubscriber(NATSConnector connector) {
        this.connector = connector;
        ActorRef mediator =
                DistributedPubSub.get(getContext().system()).mediator();
        mediator.tell(new DistributedPubSubMediator.Subscribe("nats-incoming", getSelf()),
                getSelf());
    }

    public void onReceive(Object msg) {
        if (msg instanceof String) {
            Message message = new Message("nats-incoming", "", ((String) msg).getBytes());
            connector.publish(message);
        } else if (msg instanceof DistributedPubSubMediator.SubscribeAck) {
            // TODO: Log
        }
        else
            unhandled(msg);
    }
}
