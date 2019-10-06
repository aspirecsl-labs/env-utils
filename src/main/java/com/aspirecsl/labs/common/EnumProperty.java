package com.aspirecsl.labs.common;

import java.util.Optional;
import java.util.function.Function;

import static java.lang.System.getProperty;

/**
 * Represents an <tt>Enum</tt> with a value.
 *
 * <p>Enumerations that implement this interface hold a <em>value</em> that is more meaningful or
 * usable than the enum itself. For example an <tt>Enum</tt> which is serialised to a
 * <tt>String</tt> value when shared with an external system can implement this interface as
 * <tt>HasValue<String></tt>.
 *
 * <p>The static methods {@link #fromValue(Class, Object)} and {@link #fromValue(Class, Object,
 * boolean)} enable the users to map a value into its <tt>Enum</tt> representation
 *
 * @see #fromValue(Class, Object)
 * @see #fromValue(Class, Object, boolean)
 * @author anoopr
 * @param <P> type of the <tt>Enum</tt> property
 * @param <T> type of the value held by this property
 */
public interface EnumProperty<P extends Enum<P> & EnumProperty<P, T>, T> {

  /**
   * Returns the value that this property corresponds to
   *
   * @return the value that this property corresponds to
   */
  T value();

  /**
   * Returns the <em>default</em> enumeration constant for this property.
   *
   * <p>Implementations that wish to offer a default value must override this method
   *
   * <p>This implementation always throws an {@code UnsupportedOperationException}.
   *
   * @return the <em>default</em> enumeration constant for this property
   */
  default P getDefault() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns a <tt>Function</tt> that always returns its input argument.
   *
   * <p>Implementations that expect the value to be in a canonical form should override this method.
   * For example an implementation that has an all <em>uppercase</em> string value should override
   * this method to return a <tt>Function</tt> that converts the input string to all
   * <em>uppercase</em>
   *
   * @return a <tt>Function</tt> that always returns its input argument
   */
  default Function<T, T> sanitiser() {
    return Function.identity();
  }

  /**
   * Maps the specified <tt>value</tt> into its corresponding <tt>enumType</tt> and returns an
   * <tt>Optional</tt> representing the <tt>enumType</tt>
   *
   * @param enumType the type of the <tt>Enum</tt> property that has a value
   * @param value the value to map to an <tt>Enum</tt> property
   * @param <P> the type of the <tt>Enum</tt> property
   * @param <V> the type of the value held by the <tt>Enum</tt> property
   * @return the <tt>Optional</tt> representing the <tt>enumType</tt> corresponding to the specified
   *     <tt>value</tt>
   */
  static <P extends Enum<P> & EnumProperty<P, V>, V> Optional<P> fromValue(
      Class<P> enumType, V value) {
    return fromValue(enumType, value, false);
  }

  /**
   * Maps the specified <tt>value</tt> into its corresponding <tt>enumType</tt> and returns an
   * <tt>Optional</tt> representing the <tt>enumType</tt>
   *
   * <p>Additionally if the <tt>sanitise</tt> flag is set to <tt>True</tt> then the value is
   * <em>sanitised</em> into a canonical form using the {@link #sanitiser()} implementation
   *
   * @param enumType the type of the <tt>Enum</tt> property that has a value
   * @param value the value to map to an <tt>Enum</tt> property
   * @param sanitise indicates if the value should be <em>sanitised</em> to a canonical
   *     representation
   * @param <P> the type of the <tt>Enum</tt> property
   * @param <V> the type of the value held by the <tt>Enum</tt> property
   * @return the <tt>Optional</tt> representing the <tt>enumType</tt> corresponding to the specified
   *     <tt>value</tt>
   */
  static <P extends Enum<P> & EnumProperty<P, V>, V> Optional<P> fromValue(
      Class<P> enumType, V value, boolean sanitise) {
    if (value != null) {
      final P[] properties = enumType.getEnumConstants();
      final V searchBy = sanitise ? properties[0].sanitiser().apply(value) : value;

      for (P result : properties) {
        if (result.value().equals(searchBy)) {
          return Optional.of(result);
        }
      }
    }
    return Optional.empty();
  }

  /**
   * Returns an enum constant that represents the value of a system property corresponding to the
   * specified <tt>key</tt>.
   *
   * <p>If the value in the system property doesn't match any enum value then the <em>default</em>
   * enum constant is returned
   *
   * @param key property key
   * @return enum constant representing the value of the property; or the <em>default</em> enum
   *     value if the property doesnt match any enum value
   * @throws UnsupportedOperationException when no enum constant matches the property value and no
   *     default enum constant is defined
   */
  static <P extends Enum<P> & EnumProperty<P, String>>
      EnumProperty<P, String> enumerateSystemProperty(Class<P> type, String key) {
    return fromValue(type, getProperty(key)).orElse(type.getEnumConstants()[0].getDefault());
  }
}
