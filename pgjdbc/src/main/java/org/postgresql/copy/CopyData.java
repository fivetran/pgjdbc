package org.postgresql.copy;

public class CopyData {
  private final byte[] data;
  private final int originalLength;

  private CopyData(byte[] data, int originalLength) {
    this.data = data;
    this.originalLength = originalLength;
  }

  public static CopyData complete(byte[] data) {
    return new CopyData(data, -1);
  }

  public static CopyData partial(byte[] data, int originalLength) {
    return new CopyData(data, originalLength);
  }

  public boolean isPartial() {
    return originalLength == -1;
  }

  public int getOriginalLength() {
    return originalLength;
  }

  public byte[] getData() {
    return data;
  }
}
