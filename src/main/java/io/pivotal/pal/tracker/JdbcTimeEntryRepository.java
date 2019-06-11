package io.pivotal.pal.tracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private Logger log = LoggerFactory.getLogger(getClass());
    private JdbcTemplate template;

    private final String INSERT_SQL = "insert into time_entries(project_id, user_id, date, hours) values(?, ?, ?, ?)";
    private final String FIND_SQL = "select id, project_id, user_id, date, hours from time_entries";
    private final String UPDATE_SQL = "update time_entries set project_id = ?, user_id = ?, date = ?, hours = ? where id = ?";
    private final String DELETE_SQL = "DELETE from time_entries where id = ?";

    @Autowired
    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry toCreate) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            PreparedStatement stmt = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);

            stmt.setLong(1, toCreate.getProjectId());
            stmt.setLong(2, toCreate.getUserId());
            stmt.setDate(3, Date.valueOf(toCreate.getDate()));
            stmt.setInt(4, toCreate.getHours());

            return stmt;
        }, generatedKeyHolder);


        return find(generatedKeyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(Long aId) {
        return this.template.query(String.format("%s where id = ?", FIND_SQL),
                new Object[]{aId},
                extractor);
    }

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

    private final ResultSetExtractor<TimeEntry> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;

    @Override
    public List<TimeEntry> list() {
        return this.template.query(FIND_SQL, mapper);
    }

    @Override
    public TimeEntry update(Long id, TimeEntry entry) {
        this.template.update(UPDATE_SQL,
                entry.getProjectId(),
                entry.getUserId(),
                Date.valueOf(entry.getDate()),
                entry.getHours(),
                id);
        return find(id);
    }

    @Override
    public void delete(Long id) {
        this.template.update(DELETE_SQL, id);
    }
}
