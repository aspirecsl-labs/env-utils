package com.aspirecsl.labs.os;

import java.util.Locale;

/**
 * Represents the operating system type.
 *
 * <p>Additionally provides a static method to get the current operating system type
 *
 * @author anoopr
 */
public enum OsType {
  WINDOWS,
  LINUX,
  OTHER,
  ;

  /** cached result of OS detection */
  private static OsType detectedOS;

  /**
   * Detects the operating system from the os.name System property and cache the result
   *
   * @return the operating system type
   */
  public static OsType getOperatingSystemType() {
    if (detectedOS == null) {
      String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
      if (OS.contains("win")) {
        detectedOS = OsType.WINDOWS;
      } else if (OS.contains("nux")) {
        detectedOS = OsType.LINUX;
      } else {
        detectedOS = OsType.OTHER;
      }
    }
    return detectedOS;
  }
}
