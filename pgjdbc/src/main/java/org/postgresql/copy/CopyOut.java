/*
 * Copyright (c) 2009, PostgreSQL Global Development Group
 * See the LICENSE file in the project root for more information.
 */

package org.postgresql.copy;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.sql.SQLException;

public interface CopyOut extends CopyOperation {
  /**
   * Blocks wait for a row of data to be received from server on an active copy operation.
   *
   * @return byte array received from server, null if server complete copy operation
   * @throws SQLException if something goes wrong for example socket timeout
   */
  @Nullable CopyData readCopyData() throws SQLException;

  /**
   * Wait for a row of data to be received from server on an active copy operation.
   *
   * @param block {@code true} if need wait data from server otherwise {@code false} and will read
   *              pending message from server
   * @param maxSize
   * @return byte array received from server, if pending message from server absent and use no
   *         blocking mode return null
   * @throws SQLException if something goes wrong for example socket timeout
   */
  @Nullable CopyData readCopyData(boolean block, @Nullable Integer maxSize) throws SQLException;

  default byte @Nullable [] readFromCopy() throws SQLException {
    CopyData copyData = readCopyData();
    if (copyData == null) return null;
    return copyData.getData();
  }

  default byte @Nullable [] readFromCopy(boolean block) throws SQLException {
    CopyData copyData = readCopyData(block, null);
    if (copyData == null) return null;
    return copyData.getData();
  }
}
