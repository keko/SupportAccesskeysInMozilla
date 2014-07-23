/**************************************************************************
# SupportAccesskeysInMozilla - Plugin for OmegaT which
#
# Copyright (C) 2014 Enrique Estevez (keko.gl@gmail.com)
#               Home page: 
#
# This file is part of SupportAccesskeysInMozilla.
#
# TransvisionForOmegaT is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# TransvisionForOmegaT is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#**************************************************************************/

package org.omegat.plugins.supportaccesskeys;

import java.util.List;
import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.data.SourceTextEntry;
import org.omegat.core.events.IEntryEventListener;
import org.omegat.util.gui.UIThreadsUtil;
import org.omegat.gui.comments.CommentsTextArea;


/**
 * This is a plugin that displays support for accesskeys in Mozilla files.
 *
 * @author Enrique Estévez <keko.gl@gmail.com>
 */
@SuppressWarnings("serial")
public class SupportAccesskeys {

    private static CommentsTextArea areaComments;
    
    /**
     * Register plugin into OmegaT.
     */
    public static void loadPlugins() {
        
        CoreEvents.registerEntryEventListener(new IEntryEventListener() {
            
    
            public void onEntryActivated(SourceTextEntry newEntry) {
                areaComments = Core.getComments();
                
                UIThreadsUtil.mustBeSwingThread();
                
                StringBuilder text = new StringBuilder(1024);

                // To facilitate the assignation of the accesskey and to check that the
                // accesskey exists into the string of the graphic interface. It works with
                // properties and dtd files.
                String referenciaAtallo = newEntry.getKey().id;
                if( referenciaAtallo.endsWith("accesskey"))
                {
                    // To get the key without the ending ".accesskey".
                    String raiz = referenciaAtallo.substring(0, referenciaAtallo.length()-10);
                    List<SourceTextEntry> entries = Core.getProject().getAllEntries();
                    for (int i = 0; i < entries.size(); i++) {
                        SourceTextEntry ste = entries.get(i);
                        // For somethingkey.accesskey can be somethingkey or somethingkey.label
                        if (newEntry.getKey().file.equalsIgnoreCase(ste.getKey().file) && (ste.getKey().id.equalsIgnoreCase(raiz) || ste.getKey().id.equalsIgnoreCase(raiz + ".label")) )
                        {
                            // TODO: If the solution is correct, the messages should be internationalized. It will be very easy.
                            text.append("\n--- HELP AND CHECKING OF ACCESSKEY ---\n\n");
                            String translationLabel = Core.getProject().getTranslationInfo(ste).translation;
                            String accesskey = Core.getProject().getTranslationInfo(newEntry).translation;
                            if ( translationLabel == null )
                            {
                                text.append("First. It recommends that you translate the next string before you assign the accesskey:\n\n" + ste.getKey().sourceText + "\n");
                            }
                            else if (accesskey != null)
                            {
                                if ( translationLabel.toLowerCase().contains(accesskey.toLowerCase()) )
                                    text.append("The accesskey seems to be OK.\n\nIMPORTANT: Need to check that the accesskey is not duplicated.");
                                else
                                    text.append("Accesskey invalid. The accesskey '" + accesskey + "' doesn't exist into the string '" + translationLabel + "'.");
                            }
                            else
                            {
                                text.append("Source string: " + ste.getKey().sourceText);
                                text.append('\n');
                                text.append("Your translation: " + translationLabel);
                                text.append("\n\nNOTE: The accesskey has to exist into his translation.");
                            }
                        }
                    }
                }
                // Before of the written, there would be to check if the CommentsTextArea class wrote something.
                // Now I am not doing it because this method run before that the onEntryActivated method of the CommentsTextArea class.
                // This method has to run after of the onEntryActivated method of the CommentsTextArea class, but I do not know how do it.
                areaComments.setText(text.toString());
            }

            void onNewFile(String activeFileName){}

        });
    }
    
    public static void unLoadPlugins(){}

}
