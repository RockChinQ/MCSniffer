package mapping;

public interface IExceptionListener {
    void onException(MappingRule mappingRule,Exception e);
    void onException(MappingTunnel mappingTunnel,Exception e);
    void onException(Forwarder forwarder,Exception e);
}
