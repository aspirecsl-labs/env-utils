package com.aspirecsl.labs.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Executes an operating system command using the <tt>Process</tt> API
 *
 * @author anoopr
 */
public final class OsCommandRunner {

  /** Hidden constructor to prevent instantiation */
  private OsCommandRunner() {}

  /**
   * Executes the specified operating system command, along with its arguments <em>(if any)</em> in
   * the order in which they are specified in the given list.
   *
   * <p>The first element in the given list is the operating system command and the remaining
   * elements are its arguments.
   *
   * <p>This method makes a copy of the specified list. Subsequent updates to the list will not
   * affect the state of the <tt>process</tt>
   *
   * @param cmdWithArgs the operating system command along with its arguments
   * @return the result of executing the specified operating system command
   * @throws OsCommandException if an error occurs while executing the specified command
   * @see OsCommandOutput
   */
  public static OsCommandOutput run(List<String> cmdWithArgs) throws OsCommandException {
    return run(cmdWithArgs.toArray(new String[0]));
  }

  /**
   * Executes the specified operating system command, along with its arguments <em>(if any)</em> in
   * the order in which they are specified in the given list.
   *
   * <p>The first element in the given list is the operating system command and the remaining
   * elements are its arguments.
   *
   * @param cmdWithArgs the operating system command along with its arguments
   * @return the result of executing the specified operating system command
   * @throws OsCommandException if an error occurs while executing the specified command
   * @see OsCommandOutput
   */
  public static OsCommandOutput run(String... cmdWithArgs) throws OsCommandException {
    try {
      final Process p = new ProcessBuilder(cmdWithArgs).redirectErrorStream(true).start();
      final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
      return new OsCommandOutput(p.waitFor(), reader.readLine());
    } catch (InterruptedException | IOException ex) {
      throw new OsCommandException(ex);
    }
  }

  /**
   * Represents the output of a operating system command executed through a <tt>OsCommandRunner</tt>
   * instance
   *
   * @see OsCommandRunner
   */
  public static class OsCommandOutput {

    /**
     * The return code of a operating system command.
     *
     * <p>Generally speaking, a <tt>zero</tt> value means the successful completion of the command
     * and a <tt>non-zero</tt> value means the non-successful completion of the command
     */
    private final int status;

    /** The completion message <em>(if any)</em> of the operating system command */
    private final String response;

    /**
     * Constructs an instance using the specified values
     *
     * @param status the completion status of the operating system command
     * @param response the completion message of the operating system command
     */
    private OsCommandOutput(int status, String response) {
      this.status = status;
      this.response = response;
    }

    /**
     * Returns the status of the operating system command
     *
     * @return the status of the operating system command
     */
    public int status() {
      return status;
    }

    /**
     * Returns the completion message of the operating system command
     *
     * @return the completion message of the operating system command
     */
    public String response() {
      return response;
    }

    @Override
    public String toString() {
      return "OsCommandOutput{" + "returnCode=" + status + ", response='" + response + '\'' + '}';
    }
  }

  /**
   * An error condition in executing an operating system command that a reasonable application might
   * want to catch
   */
  public static class OsCommandException extends Exception {

    private static final long serialVersionUID = 7923120119315992756L;

    /**
     * Creates an instance with the specified <tt>cause</tt>
     *
     * @param cause the root cause exception
     */
    @SuppressWarnings("WeakerAccess")
    public OsCommandException(Exception cause) {
      super(cause);
    }
  }
}
