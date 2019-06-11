package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TimeEntryHealthIndicator implements HealthIndicator {
    public static final int MAX_TIME_ENTRIES = 5;
    private final TimeEntryRepository repository;

    @Autowired
    public TimeEntryHealthIndicator(TimeEntryRepository timeEntryRepo) {
        this.repository = timeEntryRepo;
    }

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();

        if (repository.list().size() < MAX_TIME_ENTRIES) {
            builder.up();
        } else {
            builder.down();
        }
        return builder.build();
    }
}
