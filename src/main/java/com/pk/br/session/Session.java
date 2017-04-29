package com.pk.br.session;

/**
 * Created by PavelK on 4/29/2017.
 */
public class Session {
    private final SessionID id = new SessionID();

    private Session(){}

    public Session(int id){
        this.id.setSubscriptionId(id);
    }

    public final long getId(){
        return id.getId();
    }
}
