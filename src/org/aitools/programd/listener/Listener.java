/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.aitools.programd.listener;

import java.util.HashMap;
import java.util.logging.Logger;

import org.aitools.programd.Core;
import org.aitools.programd.bot.Bot;
import org.aitools.programd.bot.BotProcess;

/**
 * An abstract listener.
 */
public abstract class Listener implements BotProcess
{
    /** The Core object in use. */
    protected Core core;
    
    /** The bot for which this listener works. */
    protected Bot bot;

    /** The id of the bot for which this listener works. */
    protected String botID;

    /** The parameters that can be set for this listener. */
    protected HashMap<String, String> parameters = new HashMap<String, String>();
    
    /** The logger to use. */
    protected Logger logger;

    /**
     * Creates a new listener with name <code>name</code>.
     * 
     * @param coreToUse
     *            the Core object in use
     * @param botToListenTo
     *            the bot for which this listener works
     * @param listenerParameters
     *            the parameters for the listener and their default values
     */
    public Listener(Core coreToUse, Bot botToListenFor, HashMap<String, String> listenerParameters) throws InvalidListenerParameterException
    {
        this.core = coreToUse;
        this.bot = botToListenFor;
        this.botID = botToListenFor.getID();
        this.parameters = listenerParameters;
        this.logger = Logger.getLogger("programd.listener");
        checkParameters();
    } 

    /**
     * Validates parameters.
     */
    abstract public void checkParameters() throws InvalidListenerParameterException;
}