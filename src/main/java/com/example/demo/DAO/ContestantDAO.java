package com.example.demo.DAO;

import com.example.demo.Entity.Contestant;
import com.example.demo.Entity.Show;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ContestantDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** ========== ROW MAPPER ========== */
    private final RowMapper<Contestant> contestantRowMapper = (rs, rowNum) -> {
        Show show = new Show();
        show.setEpisodeId(rs.getString("episode_id"));
        show.setShowTitle(rs.getString("show_title"));
        show.setShowDescription(rs.getString("show_description"));
        show.setShowType(rs.getString("show_type"));
        if (rs.getDate("show_date") != null) {
            show.setShowDate(rs.getDate("show_date").toLocalDate());
        }
        if (rs.getTime("show_time") != null) {
            show.setShowTime(rs.getTime("show_time").toLocalTime());
        }
        show.setStatus(rs.getString("status"));
        show.setSessions(new ArrayList<>());

        Contestant c = new Contestant();
        c.setContestantId(rs.getString("contestant_id"));
        c.setName(rs.getString("name"));
        c.setBio(rs.getString("bio"));
        c.setImage(rs.getBytes("image"));
        c.setStatus(rs.getString("status"));
        c.setShow(show);
        c.setResults(new ArrayList<>());
        return c;
    };

    /** ========== SAVE ========== */
    public void save(Contestant contestant) {
        String sql = """
            INSERT INTO contestant (contestant_id, name, bio, image, status, episode_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                contestant.getContestantId(),
                contestant.getName(),
                contestant.getBio(),
                contestant.getImage(),
                contestant.getStatus(),
                contestant.getShow().getEpisodeId()
        );
    }

    /** ========== UPDATE ========== */
    public int update(Contestant contestant) {
        String sql = """
            UPDATE contestant
            SET name=?, bio=?, image=?, status=?, episode_id=?
            WHERE contestant_id=?
        """;
        return jdbcTemplate.update(sql,
                contestant.getName(),
                contestant.getBio(),
                contestant.getImage(),
                contestant.getStatus(),
                contestant.getShow().getEpisodeId(),
                contestant.getContestantId()
        );
    }

    /** ========== DELETE ========== */
    public int delete(String contestantId) {
        String sql = "DELETE FROM contestant WHERE contestant_id=?";
        return jdbcTemplate.update(sql, contestantId);
    }

    /** ========== FIND ALL ========== */
    public List<Contestant> findAll() {
        String sql = """
            SELECT c.*, s.show_title, s.show_description, s.show_type, s.show_date, s.show_time, s.status
            FROM contestant c
            JOIN showepi s ON c.episode_id = s.episode_id
        """;
        return jdbcTemplate.query(sql, contestantRowMapper);
    }

    /** ========== FIND BY EPISODE ========== */
    public List<Contestant> findByEpisodeId(String episodeId) {
        String sql = """
            SELECT c.*, s.show_title, s.show_description, s.show_type, s.show_date, s.show_time, s.status
            FROM contestant c
            JOIN showepi s ON c.episode_id = s.episode_id
            WHERE c.episode_id=?
        """;
        return jdbcTemplate.query(sql, contestantRowMapper, episodeId);
    }

    /** ========== FIND BY STATUS ========== */
    public List<Contestant> findByStatus(String status) {
        String sql = """
            SELECT c.*, s.show_title, s.show_description, s.show_type, s.show_date, s.show_time, s.status
            FROM contestant c
            JOIN showepi s ON c.episode_id = s.episode_id
            WHERE c.status=?
        """;
        return jdbcTemplate.query(sql, contestantRowMapper, status);
    }

    /** ========== FIND BY ID ========== */
    public Optional<Contestant> findById(String contestantId) {
        String sql = """
            SELECT c.*, s.show_title, s.show_description, s.show_type, s.show_date, s.show_time, s.status
            FROM contestant c
            JOIN showepi s ON c.episode_id = s.episode_id
            WHERE c.contestant_id=?
        """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, contestantRowMapper, contestantId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
