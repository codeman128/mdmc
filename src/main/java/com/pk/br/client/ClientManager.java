package com.pk.br.client;

import com.ebs.jrt.communication.*;
import com.ebs.jrt.communication.implementation.socket.SocketCommLayer;
import com.ebs.jrt.logging.Logger;

import java.net.URI;

/**
 * Created by PavelK on 5/1/2017.
 */
public class ClientManager {
    private final IClientManagerConfig cnfg;
    private final IClientEventCollector ec;
    private AsyncServer serverInstance;
    private final SocketCommLayer layer;
    private final Client[] clients;
    private volatile boolean isAccepting = false;
    private final Logger logger;
    private final Acceptor acceptor;

    private ClientManager() {
        ec = null;
        cnfg = null;
        layer = null;
        clients = null;
        logger = null;
        acceptor = null;
    }

    public ClientManager(IClientManagerConfig cnfg, IClientEventCollector ec, Logger logger) {
        this.ec = ec;
        this.cnfg = cnfg;
        this.logger = logger;
        layer = new SocketCommLayer();
        layer.init(cnfg.getNetLayerConnectTimeOut(),
                cnfg.getNetLayerReadTimeout(),
                cnfg.getNetLayerApplicationTimeOut(),
                cnfg.getNetLayerProtocolHeartbeatTimeout(),
                cnfg.getNetLayerCloseTimeout(),
                cnfg.getNetLayerHeartBeatTolerance(),
                cnfg.getNetLayerQueueSize(), Header.MAX_PAYLOAD_SIZE);
        //layer.toggleDebug(true);

        clients = new Client[cnfg.getMaxClientCount()];
        for (int i=0; i<cnfg.getMaxClientCount();i++) {
            clients[i] = new Client(this);
        }

        acceptor = new Acceptor() {
            @Override
            public boolean acceptConnection(final Channel channel, URI clientURI) {
                Client client = null;
                if (channel == null) return false;

                // --- todo: ugly workaround to clean NUL char in compId name, sort it out... -------------
                byte[] compId = channel.getClientData().getCompId();
                int size = 0;
                for (; (size<compId.length)&&(compId[size]!=0); size++);

                if (isAccepting) {
                    //if (agent.accept(channel, clientURI)) return true;

                    if (validateIncomingConnection(channel, clientURI)) {
                        client = assignChannel(channel);
                        if (client != null) {
                            ec.onClientAssignSuccessful(logger, client);
                            return true;
                        } else {
                            //GCLogEvents.S_CONNECTION_REJECTED_BUSY.log(logger,
                            //        localURI.getBytes(), 0, localURI.length(),
                            //        compId,  0, size);
                        }
                    } else {
                        //GCLogEvents.S_CONNECTION_REFUSED.log(logger,
                        //        localURI.getBytes(), 0, localURI.length(),
                        //        compId,  0, size); // add user
                    }
                } else {
                    //GCLogEvents.S_NOT_ACCEPTING_CONNECTIONS.log(logger,
                    //        localURI.getBytes(), 0, localURI.length(),
                    //        compId,  0, size);
                }
                return false;
            }



            @Override
            public boolean onError(Throwable throwable) {
                //GCLogEvents.S_ERROR_ACCEPTING_CONNECTION.log(logger, throwable);
                return false;
            }

//            @Override
//            public void serverAborted(CommunicationError e) {
//                GCLogEvents.S_SERVER_ABORTED.log(logger, e);
//                isAccepting = false;
//                //todo revise...
//                terminateProcessors();
//                //todo revise...
//            }
        };

    }


    private boolean validateIncomingConnection(Channel channel, URI clientURI) {
        return true;
    }

    /** Performs binding and start server, if return false server should be shut down ASAP */
    public boolean start() {
        try {
            serverInstance = layer.bindAsyncServer(cnfg.getServerHost(),
                    acceptor, cnfg.getServerProtocolVersionId(), cnfg.getServerCompId(), null);
        } catch (BindFailedError e) {
            ec.onBindFailedUnknown(logger, cnfg.getServerHost().getBytes(), e);
            return false;
        } catch (InvalidAddressError e) {
            ec.onBindFailedInvalidAddress(logger, cnfg.getServerHost().getBytes(), e);
            return false;
        }

        isAccepting = true;
        ec.onBindSuccessful(logger, cnfg.getServerHost().getBytes());
        return true;
    }


    public int numOfAvailableClients() {
        int counter = 0;
        for (int i=0; i<clients.length; i++) {
            if (clients[i].checkState(Client.State.READY)) {
                counter++;
            }
        }
        return counter;
    }

    protected Client assignChannel(Channel channel) {
        Client c;
        for (int i=0; i<clients.length; i++) {
            c = clients[i];
            if (c != null && c.assign(channel)) {
                return c;
            }
        }
        return null;
    }

    /** Perform unbind and close everything */
    public void stop() {
        isAccepting = false;
        for (int i=0; i<clients.length; i++) {
            if (clients[i] != null) {
                clients[i].shutDown();
            }
        }
        layer.shutdown();
    }

}




