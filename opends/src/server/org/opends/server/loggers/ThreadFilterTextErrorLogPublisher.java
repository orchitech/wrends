/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at
 * trunk/opends/resource/legal-notices/OpenDS.LICENSE
 * or https://OpenDS.dev.java.net/OpenDS.LICENSE.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at
 * trunk/opends/resource/legal-notices/OpenDS.LICENSE.  If applicable,
 * add the following below this CDDL HEADER, with the fields enclosed
 * by brackets "[]" replaced with your own identifying information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Portions Copyright 2006-2007 Sun Microsystems, Inc.
 */
package org.opends.server.loggers;

import org.opends.server.types.ErrorLogCategory;
import org.opends.server.types.ErrorLogSeverity;
import org.opends.server.types.InitializationException;
import org.opends.server.types.DN;
import org.opends.server.admin.std.server.ErrorLogPublisherCfg;
import org.opends.server.config.ConfigException;
import org.opends.server.api.ErrorLogPublisher;
import org.opends.server.util.TimeThread;

/**
 * This class provides an implementation of an error logger where only messages
 * generated by a specified thread is actually logged.
 */
public class ThreadFilterTextErrorLogPublisher
    extends ErrorLogPublisher<ErrorLogPublisherCfg>
{
  private Thread thread;

  private TextWriter writer;

  /**
   * Construct a new instance with the provided settings.
   *
   * @param thread The thread to log from.
   * @param writer The writer used to write the messages.
   */
  public ThreadFilterTextErrorLogPublisher(Thread thread,
                                           TextWriter writer)
  {
    this.thread = thread;
    this.writer = writer;
  }

  /**
   * {@inheritDoc}
   */
  public void initializeErrorLogPublisher(ErrorLogPublisherCfg config)
      throws ConfigException, InitializationException
  {
    // This class should only be used internally in the server and not be
    // configurable via the admin framework.
  }

  /**
   * {@inheritDoc}
   */
  public void close()
  {
    writer.shutdown();
  }

  /**
   * {@inheritDoc}
   */
  public void logError(ErrorLogCategory category,
                       ErrorLogSeverity severity, String message,
                       int errorID)
  {
    Thread currentThread = Thread.currentThread();
    if(this.thread.equals(currentThread) ||
        this.thread.getThreadGroup().equals(currentThread.getThreadGroup()))
    {
      StringBuilder sb = new StringBuilder();
      sb.append("[");
      sb.append(TimeThread.getLocalTime());
      sb.append("] category=").append(category.getCategoryName()).
          append(" severity=").append(severity.getSeverityName()).
          append(" msgID=").append(String.valueOf(errorID)).
          append(" msg=").append(message);

      this.writer.writeRecord(sb.toString());
    }
  }

  /**
   * {@inheritDoc}
   */
  public DN getDN()
  {
    // This class should only be used internally in the server and not be
    // configurable via the admin framework.
    return null;
  }
}
