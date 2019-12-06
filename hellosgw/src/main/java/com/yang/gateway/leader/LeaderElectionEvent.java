package com.yang.gateway.leader;

import org.springframework.context.ApplicationEvent;

public class LeaderElectionEvent extends ApplicationEvent {

    /**
     * TRUE - if given node just elected as a leader
     * FALSE - if given node just lost leadership
     */
    private final boolean elected;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public LeaderElectionEvent(Object source, boolean elected) {
        super(source);
        this.elected = elected;
    }

    public boolean isElected() {
        return elected;
    }
}

