/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.aitools.programd.interfaces;

import java.io.PrintStream;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * A <code>StdStreamHandler</code> publishes any record it's passed to
 * the given <code>stream</code>,
 * except those messages which are discarded by the given {@link StdFilter}.
 * 
 * @author Noel Bush
 * @since 4.2
 */
public class StdStreamHandler extends StreamHandler
{
    /** A Shell to watch. */
    private Shell shell;
    
    public StdStreamHandler(ConsoleSettings consoleSettings, PrintStream stream, StdFilter filter)
    {
        super(System.out, new ConsoleFormatter(consoleSettings));
        setFilter(filter);
    }
    
    public void watch(Shell shellToWatch)
    {
        this.shell = shellToWatch;
    }
    
    public void publish(LogRecord record)
    {
        super.publish(record);
        if (this.shell != null)
        {
            this.shell.gotLine();
        }
    }
}