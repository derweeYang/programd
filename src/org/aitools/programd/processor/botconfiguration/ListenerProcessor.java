/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.aitools.programd.processor.botconfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import org.aitools.programd.Core;
import org.aitools.programd.bot.Bot;
import org.aitools.programd.bot.BotProcesses;
import org.aitools.programd.listener.Listener;
import org.aitools.programd.listener.ListenerRegistry;
import org.aitools.programd.parser.BotsConfigurationFileParser;
import org.aitools.programd.util.DeveloperError;
import org.aitools.programd.util.NotARegisteredClassException;

/**
 * The <code>listener</code> element is a container for defining parameters of
 * a listener.
 * 
 * @version 4.2
 * @author Noel Bush
 */
public class ListenerProcessor extends BotConfigurationElementProcessor
{
    /** The label (as required by the registration scheme). */
    public static final String label = "listener";

    // Convenience constants.

    /** The string &quot;parameter&quot;. */
    private static final String PARAMETER = "parameter";

    /** The string &quot;type&quot;. */
    private static final String TYPE = "type";

    /** The string &quot;true&quot;. */
    private static final String TRUE = "true";

    /** The string &quot; : &quot;. */

    private static final String SEPARATOR = " : ";

    /**
     * Creates a new ListenerProcessor using the given Core.
     * @param coreToUse the Core object to use
     */
    public ListenerProcessor(Core coreToUse)
    {
        super(coreToUse);
    }

    /**
     * @see org.aitools.programd.processor.botconfiguration.BotConfigurationElementProcessor#process(org.w3c.dom.Element, org.aitools.programd.parser.BotsConfigurationFileParser)
     */
    public void process(Element element, BotsConfigurationFileParser parser)
    {
        // Does it have an href attribute?
        if (element.hasAttribute(HREF))
        {
            parser.verifyAndParse(element.getAttribute(HREF));
            return;
        }
        // otherwise...

        // Get type attribute
        String type = element.getAttribute(TYPE);

        // Does its type correspond to a registered listener type?
        Class listenerClass = null;
        try
        {
            listenerClass = ListenerRegistry.getSelf().get(type);
        }
        catch (NotARegisteredClassException e)
        {
            throw new DeveloperError("Unknown listener \"" + type + "\".", e);
        }

        // Get enabled attribute
        String enabled = element.getAttribute(ENABLED);

        // Is the enabled attribute true?
        if (!enabled.equals(TRUE))
        {
            return;
        }

        // Get the current bot.
        Bot bot = parser.getCurrentBot();

        // Count the parameters
        int parameterCount = element.getElementsByTagName(PARAMETER).getLength();

        // Set up the parameters for the listener.
        Map<String, String> parameters = Collections.checkedMap(new HashMap<String, String>(parameterCount), String.class, String.class);
        for (int index = parameterCount; index > 0; index--)
        {
            Element parameter = (Element) parser.getNode(PARAMETER, element.getChildNodes(), index);
            parameters.put(parameter.getAttribute(NAME), parameter.getAttribute(VALUE));
        }

        // Instantiate a new listener for the bot.
        Listener listener;
        try
        {
            listener = (Listener) listenerClass.getConstructor(Core.class, Bot.class, String.class, HashMap.class).newInstance(this.core, bot);
        }
        catch (IllegalAccessException e)
        {
            throw new DeveloperError("The constructor for the \"" + type + "\" listener class is inaccessible.", e);
        }
        catch (IllegalArgumentException e)
        {
            throw new DeveloperError("The constructor for the \"" + type + "\" listener class is incorrectly specifed.", e);
        }
        catch (InstantiationException e)
        {
            throw new DeveloperError("The \"" + type + "\" listener class is abstract.", e);
        }
        catch (NoSuchMethodException e)
        {
            throw new DeveloperError("The constructor for the \"" + type + "\" listener class is incorrectly specifed.", e);
        }
        catch (InvocationTargetException e)
        {
            throw new DeveloperError("The constructor for the \"" + type + "\" listener class threw an exception.", e);
        }

        // Start listener
        BotProcesses.start(listener, type + SEPARATOR + bot.getID());

        /*
         if (Settings.showConsole())
         {
         Log.userinfo("Started \"" + type + "\" listener for bot \"" + bot.getID() + "\".", Log.STARTUP);
         }
         */
    }
}