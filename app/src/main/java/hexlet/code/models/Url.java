package hexlet.code.models;

import java.sql.Timestamp;

public final class Url {
    private Long id;
    private String name;
    private Timestamp createdAt;

    public Url(String name, Timestamp createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }

    /**
     * Gets the ID of the URL.
     * @return the ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the URL.
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the URL.
     * @return the URL name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the creation timestamp of the URL.
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
