package wtf.choco.commons.collection;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation of a Collection based on a TreeMap. The goal of the RandomCollection
 * is to simplify the process of retrieving a random object based on its mapped weight.
 *
 * @param <E> - The type of object to be stored in the Collection
 */
public class RandomCollection<E> {

    private final NavigableMap<Double, E> map = new TreeMap<>();
    private final Random random;
    private double total = 0;

    /**
     * Construct a new RandomCollection with a given Random object.
     *
     * @param random an instance of Random to use
     */
    public RandomCollection(@NotNull Random random) {
        Preconditions.checkArgument(random != null, "random must not be null");
        this.random = random;
    }

    /**
     * Construct a new RandomCollection with a new Random object.
     */
    public RandomCollection() {
        this(new Random());
    }

    /**
     * Add a new object to the collection with a given weighted random.
     *
     * @param weight the weight of the object
     * @param result the object to add
     *
     * @return true if successfully added, false if an invalid weight was provided
     */
    public boolean add(double weight, @Nullable E result) {
        if (weight <= 0.0D) {
            return false;
        }

        this.total += weight;
        this.map.put(total, result);
        return true;
    }

    /**
     * Add all elements of another RandomCollection.
     *
     * @param collection the collection to add
     */
    public void addAll(@NotNull RandomCollection<@Nullable E> collection) {
        Preconditions.checkArgument(collection != null, "Cannot add null collection");
        collection.map.forEach(this::add);
    }

    /**
     * Remove the provided element from this collection.
     *
     * @param element the element to remove
     */
    public void remove(@Nullable E element) {
        this.map.values().remove(element);
    }

    /**
     * Check whether this collection contains a specific value.
     *
     * @param value whether the value to check for
     *
     * @return true if the collection contains the value. false otherwise
     */
    public boolean contains(@Nullable E value) {
        return map.containsValue(value);
    }

    /**
     * Get a Collection of all values in the underlying TreeMap.
     *
     * @return get all values in the collection
     */
    @NotNull
    public Collection<@Nullable E> values() {
        return map.values();
    }

    /**
     * Retrieve the next object, being a random object in the collection based on its
     * weighted value.
     *
     * @param random a random instance
     *
     * @return a random weighted object
     */
    @Nullable
    public E next(@NotNull Random random) {
        double value = random.nextDouble() * total;
        Entry<Double, E> entry = map.ceilingEntry(value);

        return (entry != null ? entry.getValue() : null);
    }

    /**
     * Retrieve the next object, being a random object in the collection based on its
     * weighted value.
     *
     * @return a random weighted object
     */
    @Nullable
    public E next() {
        return next(random);
    }

    /**
     * Clear all data from the random collection.
     */
    public void clear() {
        this.map.clear();
        this.total = 0;
    }

    /**
     * Check if this collection is completely empty and contains no entries.
     *
     * @return true if empty, false if elements exist
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Get the size of the random collection.
     *
     * @return the collection size
     */
    public int size() {
        return map.size();
    }

    /**
     * Get the values of this random collection as an iterable java.util.Collection object
     * for easy manoeuvrability.
     *
     * @return the resulting collection
     */
    @NotNull
    public Collection<@Nullable E> toCollection() {
        return map.values();
    }

    /**
     * Copy a RandomCollection with identical elements and Random instance.
     *
     * @param toCopy the collection to copy
     * @param <E> - The type of object stored in the Collections
     *
     * @return the collection copy
     */
    @NotNull
    public static <E> RandomCollection<@Nullable E> copyOf(@NotNull RandomCollection<E> toCopy) {
        Preconditions.checkArgument(toCopy != null, "Cannot copy null collection");

        RandomCollection<E> result = new RandomCollection<>(toCopy.random);
        result.addAll(toCopy);
        return result;
    }

}
