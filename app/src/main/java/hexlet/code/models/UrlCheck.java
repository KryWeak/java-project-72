package hexlet.code.models;

import java.time.LocalDateTime;

/**
 * Represents a URL check result in the database.
 */
public final class UrlCheck {
    private Long id;
    private Long urlId;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private LocalDateTime createdAt;

    /**
     * Gets the ID of the URL check.
     *
     * @return the ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the URL check.
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the ID of the associated URL.
     *
     * @return the URL ID
     */
    public Long getUrlId() {
        return urlId;
    }

    /**
     * Sets the ID of the associated URL.
     *
     * @param urlId the URL ID to set
     */
    public void setUrlId(Long urlId) {
        this.urlId = urlId;
    }

    /**
     * Gets the HTTP status code of the URL check.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the HTTP status code of the URL check.
     *
     * @param statusCode the status code to set
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Gets the title of the checked page.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the checked page.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the H1 tag content of the checked page.
     *
     * @return the H1 content
     */
    public String getH1() {
        return h1;
    }

    /**
     * Sets the H1 tag content of the checked page.
     *
     * @param h1 the H1 content to set
     */
    public void setH1(String h1) {
        this.h1 = h1;
    }

    /**
     * Gets the meta description of the checked page.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the meta description of the checked page.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the creation timestamp of the URL check.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the URL check.
     *
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
