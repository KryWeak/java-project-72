package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Deque;

@Setter
@Getter
@AllArgsConstructor
public class Url {
    private Long id;
    private final String name;
    private Timestamp createdAt;
    private Deque<UrlCheck> urlChecks;

    public Url(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Url(String name) {
        this.name = name;
    }
}
