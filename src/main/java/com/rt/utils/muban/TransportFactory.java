package com.rt.utils.muban;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import javax.mail.Session;
import javax.mail.Transport;

public class TransportFactory implements PooledObjectFactory<Transport> {

    private final Session session;

    public TransportFactory(Session session) {
        this.session = session;
    }

    @Override
    public PooledObject<Transport> makeObject() throws Exception {
        Transport transport = session.getTransport("smtp");
        transport.connect();
        return new DefaultPooledObject<>(transport);
    }

    @Override
    public void destroyObject(PooledObject<Transport> p) throws Exception {
        Transport transport = p.getObject();
        transport.close();
    }

    @Override
    public boolean validateObject(PooledObject<Transport> p) {
        Transport transport = p.getObject();
        return transport.isConnected();
    }

    @Override
    public void activateObject(PooledObject<Transport> p) throws Exception {
        // No additional activation logic needed
    }

    @Override
    public void passivateObject(PooledObject<Transport> p) throws Exception {
        // No additional passivation logic needed
    }
}

