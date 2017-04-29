package com.pk.br.session;

/**
 * Created by PavelK on 4/29/2017.
 */
public class SessionManager {
    private final SubList<Session> sessions;

    public SessionManager(int maxSessions){
        sessions = new SubList<>(maxSessions);
        for (int i=0; i<maxSessions; i++) {
            sessions.add(new Session(i));
        }
    }

    public final Session getSession(int id){
        return sessions.get(id);
    }

    public final Session allocateNewSession() {
        return sessions.popAvailable();
    }

    public final void returnSession(Session session){
        sessions.pushAvailable(session);
    }
}
