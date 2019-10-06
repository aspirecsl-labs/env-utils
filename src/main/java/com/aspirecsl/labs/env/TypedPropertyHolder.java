package com.aspirecsl.labs.env;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a type safe way to access properties held in a <tt>ResourceBundle</tt>.
 *
 * <p>
 *
 * <p>A properties file named <em>foobar</em> as below:
 *
 * <pre>
 *     FOO=foo
 *     BAR=bar
 * </pre>
 *
 * is mapped to the class below:
 *
 * <pre>
 *     public class FooBarPropertyHolder extends TypedPropertyHolder< FooBarPropertyHolder.FooBar >
 *     {
 *         private static final String PROPERTY_FILENAME = "foobar";
 *
 *         StaticTestProperties()
 *         {
 *             super( StaticTestProperty.class, PROPERTY_FILENAME );
 *         }
 *
 *         public enum FooBar
 *         {
 *             FOO,
 *             BAR
 *         }
 *     }
 * </pre>
 *
 * which provides a <em>type-safe</em> way to access the property values as below:
 *
 * <pre>
 *     new FooBarPropertyHolder().get(FOO);
 * </pre>
 *
 * @param <T> the type of the <tt>enum</tt> that maps to the properties in a <tt>ResourceBundle</tt>
 */
public abstract class TypedPropertyHolder<T extends Enum<T>> {
  /** class level logger * */
  private static final Logger logger = LoggerFactory.getLogger(TypedPropertyHolder.class);

  /** a dictionary of property values referred to by the relevant enum constants * */
  private final EnumMap<T, String> properties;

  /**
   * Creates a new instance with the supplied values.
   *
   * <p>The property file name can be specified as a static value like <em>foobar</em> or as a
   * dynamic value like <em>foobar-${profile-dev}</em> or <em>foobar-${profile:-dev}</em>.
   *
   * <p>When a static value is specified as the property file name then the property file is
   * searched via an exact match - so <em>foobar</em> will load <em>foobar.properties</em> file.
   *
   * <p>Whereas by specifying a dynamic value the property file is searched via the value of the
   * system property referenced by the <em>key</em> in the property file name. For example:-
   * <em>foobar-${profile-dev}</em> will load the properties file named
   * <em>foobar-test.properties</em> if the system property <em>profile</em> has the value
   * <em>test</em>; or <em>foobar-dev.properties</em> if the system property <em>profile</em> is not
   * set. Similarly, <em>foobar-${profile:-dev}</em> will load <em>foobar-dev.properties</em> if the
   * system property <em>profile</em> is not set or is empty <em>(all blanks)</em>.
   *
   * @param propertyType the <tt>enum</tt> that maps to the properties
   * @param propertyFilename the properties file name to load the properties from
   */
  protected TypedPropertyHolder(Class<T> propertyType, String propertyFilename) {
    properties = new EnumMap<>(propertyType);

    final ResourceBundle appConfigResource = resourceBundle(propertyFilename);

    for (T property : propertyType.getEnumConstants()) {
      try {
        properties.put(property, appConfigResource.getString(property.name()));
      } catch (MissingResourceException ex) {
        logger.warn("no property found for key {}. default value may be used", property.name());
        properties.put(property, null);
      }
    }
  }

  /**
   * Returns the <tt>ResourceBundle</tt> with properties read from the properties file corresponding
   * to the specified <tt>propertyFilename</tt>
   *
   * @param propertyFilename the property file name
   * @return the <tt>ResourceBundle</tt> with properties read from the properties file corresponding
   *     to the specified <tt>propertyFilename</tt>
   */
  private ResourceBundle resourceBundle(String propertyFilename) {
    final Pattern pattern = Pattern.compile("^(\\S+)(\\$\\S+)$");
    final Matcher matcher = pattern.matcher(propertyFilename);
    if (matcher.find()) {
      final String runtimePropertyFileName =
          matcher.group(1) + SystemInfo.expandParameter(matcher.group(2));
      return ResourceBundle.getBundle(runtimePropertyFileName);
    } else {
      return ResourceBundle.getBundle(propertyFilename);
    }
  }

  /**
   * Returns the value of the specified <tt>property</tt>; or the given <tt>defaultValue</tt> if the
   * property is not set
   *
   * @param property the property
   * @param defaultValue the default value to use in case the property is not set
   * @return the value of the specified <tt>property</tt>; or the given <tt>defaultValue</tt> if the
   *     property is not set
   */
  public String getOrDefault(T property, String defaultValue) {
    return get(property).orElse(defaultValue);
  }

  /**
   * Returns the <tt>Optional</tt> describing the value corresponding to the specified
   * <tt>property</tt>; or an <em>empty</em> <tt>Optional</tt> if the specified <tt>property</tt> is
   * not set
   *
   * @param property the property
   * @return the <tt>Optional</tt> describing the value corresponding to the specified *
   *     <tt>property</tt>; or an <em>empty</em> <tt>Optional</tt> if the specified
   *     <tt>property</tt> is not set
   */
  public Optional<String> get(T property) {
    return Optional.ofNullable(properties.get(property));
  }
}
