package com.aspirecsl.labs.env;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aspirecsl.labs.os.OsCommandRunner;

import static java.lang.Long.parseLong;
import static java.lang.System.getProperty;
import static java.lang.management.ManagementFactory.getRuntimeMXBean;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;

/**
 * Provides information from the execution environment
 *
 * @author anoopr
 */
public final class SystemInfo {

  /**
   * pattern that matches a <tt>BASH</tt> style parameter reference - like ${foo-bar} or ${foo:-bar}
   */
  private static final Pattern pattern;

  static {
    pattern =
        compile(
            "^\\$\\{(?<emptiableParameter>[\\w.]+)-(?<defaultValueWhenUnset>[\\S ]+)}$"
                + "|"
                + "^\\$\\{(?<nonEmptiableParameter>[\\w.]+):-(?<defaultValueWhenEmptyOrUnset>[\\S ]+)}$");
  }

  /** Hidden constructor to prevent instantiation */
  private SystemInfo() {}

  /**
   * Returns the current logged in user name
   *
   * @return the current logged in user name
   */
  public static String userName() {
    return getProperty("user.name");
  }

  /**
   * Returns the current logged in user's home directory
   *
   * @return the current logged in user's home directory
   */
  public static String userHome() {
    return getProperty("user.home");
  }

  /**
   * Returns the fully qualified path of the user's present working directory
   *
   * @return the fully qualified path of the user's present working directory
   */
  public static String pwd() {
    return getProperty("user.dir");
  }

  /**
   * Returns the process id of this Java virtual machine
   *
   * @return the process id of this Java virtual machine
   */
  public static long pid() {
    /*
     * getRuntimeMXBean().getName() returns "processId@hostname" string
     */
    return parseLong(getRuntimeMXBean().getName().split("@")[0]);
  }

  /**
   * Returns the host name of the system
   *
   * @return the host name of the system
   * @throws RuntimeException if the host name retrieval fails
   */
  public static String hostname() {
    try {
      return OsCommandRunner.run("hostname").response();
    } catch (OsCommandRunner.OsCommandException e) {
      throw new RuntimeException("Hostname retrieval failed!");
    }
  }

  /**
   * Returns the value of a system property by applying "bash-style" parameter expansion on the
   * supplied property key.
   * <li>${foo-bar} returns the value of the system property <i>foo</i>, or <i>bar</i> if <i>foo</i>
   *     system property is not set
   * <li>${foo:-bar} returns the value of the system property <i>foo</i>, or <i>bar</i> if
   *     <i>foo</i> is not set or it is empty or blank(only spaces)
   *
   * @param parameterName parameter name
   * @return value of the property, or the default value
   * @throws IllegalArgumentException if the specified <tt>parameterName</tt> is malformed
   */
  public static String expandParameter(String parameterName) {
    final Matcher matcher = pattern.matcher(parameterName);

    if (matcher.find()) {
      return ofNullable(matcher.group("emptiableParameter"))
          .map(
              key -> {
                final String value = getProperty(key);
                return value == null ? matcher.group("defaultValueWhenUnset") : value;
              })
          .orElseGet(
              () -> {
                final String value = getProperty(matcher.group("nonEmptiableParameter"));
                return (value == null || value.isEmpty())
                    ? matcher.group("defaultValueWhenEmptyOrUnset")
                    : value;
              });
    } else {
      throw new IllegalArgumentException("malformed parameter name");
    }
  }
}
