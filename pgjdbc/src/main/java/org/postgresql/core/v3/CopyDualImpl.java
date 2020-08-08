/*
 * Copyright (c) 2016, PostgreSQL Global Development Group
 * See the LICENSE file in the project root for more information.
 */

package org.postgresql.core.v3;

import org.postgresql.copy.CopyData;
import org.postgresql.copy.CopyDual;
import org.postgresql.util.ByteStreamWriter;
import org.postgresql.util.PSQLException;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Queue;

public class CopyDualImpl extends CopyOperationImpl implements CopyDual {
  private final Queue<CopyData> received = new ArrayDeque<CopyData>();

  public void writeToCopy(byte[] data, int off, int siz) throws SQLException {
    getQueryExecutor().writeToCopy(this, data, off, siz);
  }

  public void writeToCopy(ByteStreamWriter from) throws SQLException {
    getQueryExecutor().writeToCopy(this, from);
  }

  public void flushCopy() throws SQLException {
    getQueryExecutor().flushCopy(this);
  }

  public long endCopy() throws SQLException {
    return getQueryExecutor().endCopy(this);
  }

  @Override
  public @Nullable CopyData readCopyData() throws SQLException {
    return readCopyData(true, null);
  }

  @Override
  public @Nullable CopyData readCopyData(boolean block, @Nullable Integer maxSize) throws SQLException {
    if (received.isEmpty()) {
      getQueryExecutor().readFromCopy(this, block, maxSize);
    }

    return received.poll();
  }

  @Override
  public byte @Nullable [] readFromCopy() throws SQLException {
    return readFromCopy(true);
  }

  @Override
  public byte @Nullable [] readFromCopy(boolean block) throws SQLException {
    CopyData copyData = readCopyData();
    if (copyData == null) return null;
    return copyData.getData();
  }

  @Override
  public void handleCommandStatus(String status) throws PSQLException {
  }

  @Override
  protected void handleCopydata(CopyData data) throws PSQLException {
    received.add(data);
  }
}
