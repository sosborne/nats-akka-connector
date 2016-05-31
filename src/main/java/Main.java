import io.nats.connector.Connector;

public class Main {
    public static void main(String[] args)
    {
        System.setProperty(Connector.PLUGIN_CLASS, AkkaConnector.class.getCanonicalName());
        try {
            new Connector(null).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
