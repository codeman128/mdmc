package com.pk.publisher.sd;

import com.pk.publisher.DistributionLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PavelK on 6/17/2016.
 */
public class ConsumerManager {
    private final List<Institution> institutions = new ArrayList<>();
    private final Map<ConnectionLookup, ConnectionMetadata> ipMap = new HashMap<>();

    public ConsumerManager() {

    }

    public final ConnectionMetadata getConnection(ConnectionLookup cl){
        return ipMap.get(cl);
    }

    public final Institution addInstitution(String name){
        Institution i = new Institution(this, name);
        institutions.add(i);
        return i;
    }

    public final List<Institution> getInstitutions(){
        return institutions;
    }

    final void register(ConnectionMetadata connection) throws Exception {
        if (ipMap.get(connection)==null) {
            ipMap.put(connection, connection);
        }  else throw new Exception("todo: replace with proper exception "+ipMap.get(connection));
    }


}
