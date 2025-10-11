package hexlet.code.models;

import java.time.LocalDateTime;

/**
 * Represents a URL in the database.
 */
public final class Url {
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    /**
     * Gets the ID of the URL.
     *
     * @return the ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the URL.
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the URL.
     *
     * @return the URL name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the URL.
     *
     * @param name the URL name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the creation timestamp of the URL.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the URL.
     *
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
