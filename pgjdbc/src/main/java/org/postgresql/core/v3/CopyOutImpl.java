/*
 * Copyright (c) 2009, PostgreSQL Global Development Group
 * See the LICENSE file in the project root for more information.
 */

package org.postgresql.core.v3;

import org.postgresql.copy.CopyData;
import org.postgresql.copy.CopyOut;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.sql.SQLException;

/**
 * <p>Anticipated flow of a COPY TO STDOUT operation:</p>
 *
 * <p>CopyManager.copyOut() -&gt;QueryExecutor.startCopy() - sends given query to server
 * -&gt;processCopyResults(): - receives CopyOutResponse from Server - creates new CopyOutImpl
 * -&gt;initCopy(): - receives copy metadata from server -&gt;CopyOutImpl.init() -&gt;lock()
 * connection for this operation - if query fails an exception is thrown - if query returns wrong
 * CopyOperation, copyOut() cancels it before throwing exception &lt;-returned: new CopyOutImpl
 * holding lock on connection repeat CopyOut.readFromCopy() until null
 * -&gt;CopyOutImpl.readFromCopy() -&gt;QueryExecutorImpl.readFromCopy() -&gt;processCopyResults() -
 * on copydata row from server -&gt;CopyOutImpl.handleCopydata() stores reference to byte array - on
 * CopyDone, CommandComplete, ReadyForQuery -&gt;unlock() connection for use by other operations
 * &lt;-returned: byte array of data received from server or null at end.</p>
 */
public class CopyOutImpl extends CopyOperationImpl implements CopyOut {
  @Nullable private CopyData currentDataRow;

  @Override
  public @Nullable CopyData readCopyData() throws SQLException {
    return readCopyData(true, null);
  }

  @Override
  public @Nullable CopyData readCopyData(boolean block, Integer maxSize) throws SQLException {
    currentDataRow = null;
    getQueryExecutor().readFromCopy(this, block, null);
    return currentDataRow;
  }

  protected void handleCopydata(CopyData data) {
    currentDataRow = data;
  }
}
