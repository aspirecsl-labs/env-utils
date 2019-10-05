package com.aspirecsl.labs.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NativeCommandRunner {

  private NativeCommandRunner() {}

  public NativeCommandOutput run(String... cmdWithArgs) throws NativeCommandException {
    try {
      final Process p = new ProcessBuilder(cmdWithArgs).redirectErrorStream(true).start();
      final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
      return NativeCommandOutput.builder()
          .returnCode(p.waitFor())
          .response(reader.readLine())
          .build();
    } catch (InterruptedException | IOException ex) {
      throw new NativeCommandException("error running the command", ex);
    }
  }

  public static class NativeCommandOutput {
    private final int returnCode;
    private final String response;

    private NativeCommandOutput(Builder builder) {
      returnCode = builder.returnCode;
      response = builder.response;
    }

    public static Builder builder() {
      return new Builder();
    }

    public int returnCode() {
      return returnCode;
    }

    public String response() {
      return response;
    }

    @Override
    public String toString() {
      return "NativeCommandOutput{"
          + "returnCode="
          + returnCode
          + ", response='"
          + response
          + '\''
          + '}';
    }

    public static class Builder {
      private int returnCode;
      private String response;

      public Builder returnCode(int returnCode) {
        this.returnCode = returnCode;
        return this;
      }

      public Builder response(String response) {
        this.response = response;
        return this;
      }

      public NativeCommandOutput build() {
        return new NativeCommandOutput(this);
      }
    }
  }

  public static class NativeCommandException extends Exception {
    private static final long serialVersionUID = 7923120119315992756L;

    public NativeCommandException(String message) {
      super(message);
    }

    public NativeCommandException(String message, Exception cause) {
      super(message, cause);
    }
  }
}
