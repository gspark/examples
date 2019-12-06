package com.yang.gateway.leader;

import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.integration.leader.Context;
import org.springframework.integration.leader.event.OnGrantedEvent;
import org.springframework.integration.leader.event.OnRevokedEvent;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class LeadershipRegistrar {
    private static final Logger log = getLogger(LeadershipRegistrar.class);

    private Integer maxLeaderRoles = 2;

    private final List<Context> leaderContextRoles = Collections.synchronizedList(new LinkedList<>());

    @EventListener(OnGrantedEvent.class)
    public synchronized void start(OnGrantedEvent evt) {
        Context ctx = evt.getContext();
        leaderContextRoles.add(ctx);
        log.info("Leader election: onGranted for role: " + ctx.getRole() + ", leaderContextRoles: " + leaderContextRoles
            .size() + " of max " + maxLeaderRoles);
    }

    @EventListener(OnRevokedEvent.class)
    public synchronized void stop(OnRevokedEvent evt) {
        Context ctx = evt.getContext();
        leaderContextRoles.remove(ctx);
        log.info("Leader election: onRevoked for role: " + ctx.getRole() + ", leaderContextRoles: " + leaderContextRoles
            .size() + " of max " + maxLeaderRoles);
    }

    //@Scheduled(fixedDelay = 5 * 1000)
    public synchronized void run() {
        if (leaderContextRoles.size() > maxLeaderRoles) {

            for (int i = leaderContextRoles.size() - 1; i > -1; i--) {
                Context ctx = leaderContextRoles.get(i);
                if (!CoordinatorLeaderCandidate.COORDINATOR_LEADER_ROLE.equalsIgnoreCase(ctx.getRole())) {
                    ctx.yield();
                    log.info("Leader election: giving up on " + leaderContextRoles.size() + "-th role: " + ctx.getRole()
                        + " with max " + maxLeaderRoles);
                    break;
                }
            }
        }
    }

    /**
     *
     */
    public synchronized boolean hasLeaderRole(final String leaderRole) {
        for (Context ctx : leaderContextRoles) {
            if (leaderRole.equalsIgnoreCase(ctx.getRole())) {
                return true;
            }
        }

        return false;
    }
}
