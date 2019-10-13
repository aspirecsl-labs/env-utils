package com.aspirecsl.labs;

import java.util.function.Function;

import org.junit.Test;

import com.aspirecsl.labs.common.EnumProperty;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test class for {@link EnumProperty}
 *
 * @author anoopr
 */
public class EnumPropertyTest {

  @Test
  public void parsesValueInToPropertyCorrectly() {
    assertThat(EnumProperty.fromValue(FooBar.class, "bar")).hasValue(FooBar.BAR);
  }

  @Test
  public void returnsEmptyOptionalWhenNoMatchingPropertyExists() {
    assertThat(EnumProperty.fromValue(FooBar.class, "foobar")).isEmpty();
  }

  @Test
  public void returnsEmptyOptionalWhenANonStandardValueParsedWithoutSanitising() {
    assertThat(EnumProperty.fromValue(FooBar.class, "FOO")).isEmpty();
  }

  @Test
  public void returnsPropertyWhenANonStandardValueParsedWithSanitising() {
    assertThat(EnumProperty.fromValue(FooBar.class, "FOO", true)).hasValue(FooBar.FOO);
  }

  @Test
  public void returnsPropertyFromASystemPropertyValue() {
    System.setProperty("foo", "bar");
    assertThat(EnumProperty.ofSystemProperty(FooBar.class, "foo")).isEqualTo(FooBar.BAR);
  }

  private enum FooBar implements EnumProperty<FooBar, String> {
    FOO("foo"),
    BAR("bar");

    private final String value;

    FooBar(String value) {
      this.value = value;
    }

    @Override
    public String value() {
      return value;
    }

    @Override
    public Function<String, String> sanitiser() {
      return String::toLowerCase;
    }
  }
}
