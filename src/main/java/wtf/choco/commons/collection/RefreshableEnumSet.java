package wtf.choco.commons.collection;

import com.google.common.base.Preconditions;

import java.util.AbstractSet;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an {@link EnumSet} capable of being refreshed by a {@link Supplier}.
 *
 * @author Parker Hawke - Choco
 *
 * @param <E> the type of elements maintained by this set
 */
public class RefreshableEnumSet<E extends Enum<E>> extends AbstractSet<E> {

    private final Set<E> elements;
    private final Supplier<@NotNull List<@NotNull String>> refresher;
    private final Function<@NotNull String, @Nullable E> mapper;

    /**
     * Construct a new {@link RefreshableEnumSet}.
     *
     * @param elementClass the class of the element type
     * @param refresher the refreshing function
     * @param mapper the function to map strings to the element type, E
     */
    public RefreshableEnumSet(@NotNull Class<E> elementClass, @NotNull Supplier<@NotNull List<@NotNull String>> refresher, @NotNull Function<@NotNull String, @Nullable E> mapper) {
        Preconditions.checkArgument(elementClass != null, "elementClass must not be null");
        Preconditions.checkArgument(refresher != null, "refresher must not be null");
        Preconditions.checkArgument(mapper != null, "mapper must not be null");

        this.elements = EnumSet.noneOf(elementClass);
        this.refresher = refresher;
        this.mapper = mapper;

        this.refresh();
    }

    @Override
    public boolean add(E type) {
        Preconditions.checkArgument(type != null, "type must not be null");

        return elements.add(type);
    }

    @Override
    public boolean remove(Object object) {
        return elements.remove(object);
    }

    @Override
    public boolean contains(Object object) {
        return elements.contains(object);
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    /**
     * Clear and refresh this collection from the given refresher.
     */
    public void refresh() {
        if (refresher == null) {
            return;
        }

        this.elements.clear();
        this.refresher.get().forEach(entityIdString -> {
            E element = mapper.apply(entityIdString);
            if (element == null) {
                return;
            }

            this.add(element);
        });
    }

}
