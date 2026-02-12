package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@Getter
@Setter
public class Url {
    private Long id;
    private final String name;
    private LocalDateTime createdAt;
    private Deque<UrlCheck> urlChecks = new ConcurrentLinkedDeque<>();

    public Url(Long id, String name) {
        this.id = id;
        this.name = name;
        this.urlChecks = new ConcurrentLinkedDeque<>();
    }

    public Url(String name) {
        this.name = name;
        this.urlChecks = new ConcurrentLinkedDeque<>();
    }

    public Url() {
        this.urlChecks = new ConcurrentLinkedDeque<>();
        this.name = "";
    }
}
