package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Setter
@Getter
@AllArgsConstructor
public class UrlCheck {
    private Long id;
    private Long urlId;
    private Integer statusCode;
    private String title;
    private String h1;
    private String description;
    private Timestamp createdAt;

    public UrlCheck(
            Integer statusCode,
            String title,
            String h1,
            String description) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
    }
}
