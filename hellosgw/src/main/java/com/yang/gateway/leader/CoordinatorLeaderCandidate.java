package com.yang.gateway.leader;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.integration.leader.Context;
import org.springframework.integration.leader.DefaultCandidate;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

@Component("coordinatorLeaderCandidate")
public class CoordinatorLeaderCandidate extends DefaultCandidate {
    private static final Logger log = getLogger(CoordinatorLeaderCandidate.class);

    public static final String COORDINATOR_LEADER_ROLE = "COORDINATOR_LEADER_ROLE";

    public CoordinatorLeaderCandidate() {
        super(UUID.randomUUID().toString(), COORDINATOR_LEADER_ROLE);
    }

    private volatile boolean leader;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void onGranted(Context ctx) {
        super.onGranted(ctx);
        leader = true;
        log.info("Leader election: onGranted for role: " + ctx.getRole());
        eventPublisher.publishEvent(new LeaderElectionEvent(this, leader));
    }

    @Override
    public void onRevoked(Context ctx) {
        super.onRevoked(ctx);
        leader = false;
        eventPublisher.publishEvent(new LeaderElectionEvent(this, leader));
        log.info("Leader election: onRevoked for role: " + ctx.getRole());
    }
}