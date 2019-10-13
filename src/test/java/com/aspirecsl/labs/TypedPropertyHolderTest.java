package com.aspirecsl.labs;

import org.junit.Test;

import com.aspirecsl.labs.env.TypedPropertyHolder;

import static com.aspirecsl.labs.TypedPropertyHolderTest.RuntimeTestProperties.RuntimeTestProperty.RUNTIME_TEST_PROPERTY;
import static com.aspirecsl.labs.TypedPropertyHolderTest.StaticTestProperties.StaticTestProperty.STATIC_TEST_PROPERTY;
import static com.aspirecsl.labs.TypedPropertyHolderTest.StaticTestProperties.StaticTestProperty.UNSET_PROPERTY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test class for {@link TypedPropertyHolder}
 *
 * @author anoopr
 */
public class TypedPropertyHolderTest {

  @Test
  public void readsTheCorrectPropertyFile_WhenEnvVariableIsSet() {
    System.getProperties().put("test.profile", "prod");
    final RuntimeTestProperties runtimeTestProperties = new RuntimeTestProperties();

    assertThat(runtimeTestProperties.get(RUNTIME_TEST_PROPERTY)).contains("RUNTIME PROD PROPERTY");
  }

  @Test
  public void readsTheDefaultPropertyFile_WhenEnvVariableIsNotSet() {
    System.getProperties().remove("test.profile");
    final RuntimeTestProperties runtimeTestProperties = new RuntimeTestProperties();

    assertThat(runtimeTestProperties.get(RUNTIME_TEST_PROPERTY)).contains("RUNTIME DEV PROPERTY");
  }

  @Test
  public void readsTheStaticPropertyFile_Correctly() {
    final StaticTestProperties staticTestProperties = new StaticTestProperties();

    assertThat(staticTestProperties.get(STATIC_TEST_PROPERTY)).contains("STATIC TEST PROPERTY");
  }

  @Test
  public void getsDefaultValue_WhenPropertyIsNotSet() {
    final StaticTestProperties staticTestProperties = new StaticTestProperties();

    assertThat(staticTestProperties.getOrDefault(UNSET_PROPERTY, "DEFAULT VALUE"))
        .contains("DEFAULT VALUE");
  }

  static class RuntimeTestProperties
      extends TypedPropertyHolder<RuntimeTestProperties.RuntimeTestProperty> {
    private static final String PROPERTY_FILENAME = "test-${test.profile:-dev}";

    RuntimeTestProperties() {
      super(RuntimeTestProperty.class, PROPERTY_FILENAME);
    }

    public enum RuntimeTestProperty {
      RUNTIME_TEST_PROPERTY
    }
  }

  static class StaticTestProperties
      extends TypedPropertyHolder<StaticTestProperties.StaticTestProperty> {
    private static final String PROPERTY_FILENAME = "test";

    StaticTestProperties() {
      super(StaticTestProperty.class, PROPERTY_FILENAME);
    }

    public enum StaticTestProperty {
      STATIC_TEST_PROPERTY,
      UNSET_PROPERTY
    }
  }
}
