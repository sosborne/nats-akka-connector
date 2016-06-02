import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class AkkaSubscriber extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public AkkaSubscriber() {
        ActorRef mediator =
                DistributedPubSub.get(getContext().system()).mediator();
        // subscribe to the topic named "content"
        mediator.tell(new DistributedPubSubMediator.Subscribe("content", getSelf()),
                getSelf());
    }

    public void onReceive(Object msg) {
        if (msg instanceof String)
            log.info("Got: {}", msg);
        else if (msg instanceof byte[])
            log.info("Got: {}", ((byte[]) msg).length);
        else if (msg instanceof DistributedPubSubMediator.SubscribeAck)
            log.info("subscribing");
        else
            unhandled(msg);
    }
}
