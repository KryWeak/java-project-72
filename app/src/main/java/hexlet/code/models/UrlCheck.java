package hexlet.code.models;

import java.sql.Timestamp;

public final class UrlCheck {
    private Long id;
    private Long urlId;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private Timestamp createdAt;

    public UrlCheck(Long urlId, int statusCode, String title, String h1, String description, Timestamp createdAt) {
        this.urlId = urlId;
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.createdAt = createdAt;
    }

    /**
     * Gets the ID of the URL check.
     * @return the ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the URL check.
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the URL ID associated with this check.
     * @return the URL ID
     */
    public Long getUrlId() {
        return urlId;
    }

    /**
     * Gets the HTTP status code of the check.
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Gets the title of the checked page.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the H1 tag content of the checked page.
     * @return the H1 content
     */
    public String getH1() {
        return h1;
    }

    /**
     * Gets the meta description of the checked page.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the creation timestamp of the check.
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
