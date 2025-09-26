package hexlet.code.models;

import java.sql.Timestamp;

public final class Url {
    private Long id;
    private String name;
    private Timestamp createdAt;

    public Url(Long id, String name, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    /**
     * Gets the ID of the URL.
     *
     * @return the ID of the URL
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
     * @return the name of the URL
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the URL.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the creation timestamp of the URL.
     *
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the URL.
     *
     * @param createdAt the timestamp to set
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
