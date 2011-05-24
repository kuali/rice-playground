package org.kuali.rice.core.api.mo.common;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.util.KeyValue;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a generic attributes class in rice.  It is essentially a list of key-value
 * pairs where the key & value are strings & the keys are unique.
 */
@XmlJavaTypeAdapter(value = Attributes.ImmutableKeyValueAttributesAdapter.class)
public final class Attributes implements Serializable {

    private static final Attributes EMPTY = new Attributes(Collections.<String, String>emptyMap());

    private final HashMap<String, String> keyValues;

    private final Object lock = new Object();
    private Set<KeyValue> cache;

    /**
     * This constructor should never be called except during JAXB unmarshalling.
     */
    private Attributes() {
        this.keyValues = null;
    }

    private Attributes(final Map<String, String> map) {
        this.keyValues = new HashMap<String, String>(map);
    }

    /**
     * Creates empty attributes.
     *
     * @return Attributes
     */
    public static Attributes empty() {
        return EMPTY;
    }

    /**
     * Creates attributes from a {@link Map}.  Map cannot be null.
     *
     * @return Attributes
     * @throws IllegalArgumentException if map is null
     */
    public static Attributes fromMap(Map<String, String> map) {
        return new Attributes(map);
    }

    /**
     * Creates attributes from a {@link Map.Entry}.  Map.Entry cannot be null.
     *
     * @return Attributes
     * @throws IllegalArgumentException if entry is null
     */
    public static Attributes fromMapEntry(Map.Entry<String, String> entry) {
        return new Attributes(Collections.singletonMap(entry.getKey(), entry.getValue()));
    }

    /**
     * Creates attributes from strings.  Key cannot be null. Value can be null
     *
     * @return Attributes
     * @throws IllegalArgumentException if value is null
     */
    public static Attributes fromStrings(String key, String value) {
        return new Attributes(Collections.singletonMap(key, value));
    }

    /**
     * Creates attributes from a {@link KeyValue}.  KeyValue cannot be null.
     *
     * @return Attributes
     * @throws IllegalArgumentException if keyValue is null
     */
    public static Attributes fromKeyValue(KeyValue keyValue) {
        return new Attributes(Collections.singletonMap(keyValue.getKey(), keyValue.getValue()));
    }

    /**
     * Converts key value to a mutable {@link Map}.
     * The map returned is disconnected from this Attributes class.
     *
     * @return a Map
     */
    public Map<String, String> toMap() {
        return new HashMap<String, String>(keyValues);
    }

    //map-like methods

    /**
     * Returns the amount of attributes in this data structure.
     *
     * @return the size
     */
    public int size() {
        return keyValues.size();
    }

    /**
     * Whether this data structure does not contain any attributes.
     *
     * @return true if empty false if not
     */
    public boolean isEmpty() {
        return keyValues.isEmpty();
    }

    /**
     * Whether this data structure contains an attribute with a given key. Key may not be null.
     *
     * @return true if the key exists false if not
     * @throws IllegalArgumentException if the key is null
     */
    public boolean containsKey(String key) {
        validateKey(key);
        return keyValues.containsKey(key);
    }

    /**
     * Whether this data structure contains an attribute with a given value. Value may be null.
     *
     * @return true if the value exists false if not
     */
    public boolean containsValue(String value) {
        return keyValues.containsValue(value);
    }

    /**
     * Gets an attribute value from a key. Key may not be null.
     *
     * @return true if the value exists false if not
     */
    public String get(String key) {
        validateKey(key);
        return keyValues.get(key);
    }

    /**
     * Gets a mutable {@link Set} of all attribute keys.
     *
     * @return the set.
     */
    public Set<String> keySet() {
        return keyValues.keySet();
    }

    /**
     * Gets a mutable {@link Collection} of all attribute values.  Could contain null values.
     *
     * @return the collection.
     */
    public Collection<String> values() {
        return keyValues.values();
    }

    /**
     * Gets a mutable {@link Set} attribute key-value pairs.
     *
     * @return the set.
     */
    public Set<KeyValue> keyValueSet() {
        //not sure if we really need caching here - but adding it
        //b/c it is easy enough to implement
        synchronized (lock) {
            if (cache == null) {
                cache = new HashSet<KeyValue>();
                for (Map.Entry<String, String> e : keyValues.entrySet()) {
                    if (e != null) {
                        cache.add(ImmutableKeyValue.fromMapEntry(e));
                    }
                }
            }
            return cache;
        }
    }

    private static void validateKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {"cache", "lock"};
    }

    public static class ImmutableKeyValueAttributesAdapter extends XmlAdapter<ImmutableKeyValue[], Attributes> {
        @Override
        public Attributes unmarshal(final ImmutableKeyValue[] vs) {
            if (vs == null) {
                return null;
            }
            final Map<String, String> m = new HashMap<String, String>();
            for (ImmutableKeyValue v : vs) {
                if (v != null) {
                    m.put(v.getKey(), v.getValue());
                }
            }
            return Attributes.fromMap(m);
        }

        @Override
        public ImmutableKeyValue[] marshal(final Attributes vs) {
            if (vs == null) {
                return null;
            }
            final List<ImmutableKeyValue> c = new ArrayList<ImmutableKeyValue>();
            for (Map.Entry<String, String> v : vs.toMap().entrySet()) {
                if (v != null) {
                    c.add(ImmutableKeyValue.fromMapEntry(v));
                }
            }
            return c.toArray(new ImmutableKeyValue[]{});
        }
    }
}
