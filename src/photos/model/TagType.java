package photos.model;

import java.io.Serializable;
/**
 * Represents a custom tag type that a user can define.
 * Each tag type has a name and a restriction on whether or
 * not the tag type can have multiple tags associated with it.
 * @author Harshi Oleti
 */
public class TagType implements Serializable {
    /**
     * Name of the tag type
     */
    private String name;
    /**
     * Whether or not this tag type is restricted
     */
    private boolean restricted;
    /**
     * Constructs a TagType with the given name
     * @param name the name of the tag type
     * @param restricted true if only one value is allowed per photo, false if multiple photos are allowed
     */
    public TagType(String name, boolean restricted) {
        this.name = name.toLowerCase();
        this.restricted = restricted;
    }
    /**
     * Gets the name of the tag type.
     * @return the tag type name
     */
    public String getName() {
        return name;
    }
    /**
     * Indicates whether the tag type allows only one value per photo
     * @return true if restricted to one tag per photo; false otherwise
     */
    public boolean isRestricted() {
        return restricted;
    }
    /**
     * Compares this TagType to another object for equality based on the names.
     * @param obj the object to compare to
     * @return true if the tag names are equal (case-insensitive), false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TagType) {
            return this.name.equalsIgnoreCase(((TagType) obj).name);
        }
        return false;
    }
    /**
     * Returns the hash code of this tag type, based on its lowercase name.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}

