package com.pk.br.client;

import com.ebs.jrt.communication.BindFailedError;
import com.ebs.jrt.communication.InvalidAddressError;
import com.ebs.jrt.logging.Logger;

/**
 * Created by PavelK on 5/1/2017.
 */
public interface IClientEventCollector {
    void onBindFailedUnknown(Logger logger, byte[] url, BindFailedError e);

    void onBindFailedInvalidAddress(Logger logger, byte[] url, InvalidAddressError e);

    void onBindSuccessful(Logger logger, byte[] url);

    void onClientAssignSuccessful(Logger logger, Client client);
}
