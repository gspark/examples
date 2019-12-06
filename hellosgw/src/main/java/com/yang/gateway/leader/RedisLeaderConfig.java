package com.yang.gateway.leader;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.leader.Candidate;
import org.springframework.integration.leader.Context;
import org.springframework.integration.leader.DefaultCandidate;
import org.springframework.integration.leader.event.DefaultLeaderEventPublisher;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.leader.LockRegistryLeaderInitiator;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
public class RedisLeaderConfig {

    public static final String COORDINATOR_LEADER_REDIS_KEY = "coordinator-leader-redis-key";

    // 2 sec
    private static final int ELECTION_REDIS_KEY_EXPIRATION_MS = 2 * 1000;

    @Bean(name = "coordinationLeaderRedisLockRegistry")
    public RedisLockRegistry coordinationLeaderRedisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(redisConnectionFactory, COORDINATOR_LEADER_REDIS_KEY,
            ELECTION_REDIS_KEY_EXPIRATION_MS);
    }

    @Bean
    public LockRegistryLeaderInitiator leaderInitiator(
        @Qualifier("coordinationLeaderRedisLockRegistry") LockRegistry coordinationLeaderRedisLockRegistry,
        Candidate coordinatorLeaderCandidate, ApplicationEventPublisher applicationEventPublisher) {
        LockRegistryLeaderInitiator initiator =
            new LockRegistryLeaderInitiator(coordinationLeaderRedisLockRegistry, coordinatorLeaderCandidate);
        initiator.setLeaderEventPublisher(new DefaultLeaderEventPublisher(applicationEventPublisher));

        return initiator;
    }

}
