import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import io.nats.client.Message;

public class NATSPublisher extends UntypedActor {
    // activate the extension
    ActorRef mediator =
            DistributedPubSub.get(getContext().system()).mediator();

    public void onReceive(Object msg) {
        if (msg instanceof Message) {
            mediator.tell(new DistributedPubSubMediator.Publish("content", ((Message) msg).getData()), getSelf());
        } else {
            unhandled(msg);
        }
    }
}
