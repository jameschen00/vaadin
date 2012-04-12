/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.embedded;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VConsole;

public class VEmbedded extends HTML {
    public static String CLASSNAME = "v-embedded";

    protected Element browserElement;

    protected String type;

    protected ApplicationConnection client;

    public VEmbedded() {
        setStyleName(CLASSNAME);
    }

    /**
     * Creates the Object and Embed tags for the Flash plugin so it works
     * cross-browser
     * 
     * @param uidl
     *            The UIDL
     * @return Tags concatenated into a string
     */
    protected String createFlashEmbed(UIDL uidl) {
        /*
         * To ensure cross-browser compatibility we are using the twice-cooked
         * method to embed flash i.e. we add a OBJECT tag for IE ActiveX and
         * inside it a EMBED for all other browsers.
         */

        StringBuilder html = new StringBuilder();

        // Start the object tag
        html.append("<object ");

        /*
         * Add classid required for ActiveX to recognize the flash. This is a
         * predefined value which ActiveX recognizes and must be the given
         * value. More info can be found on
         * http://kb2.adobe.com/cps/415/tn_4150.html. Allow user to override
         * this by setting his own classid.
         */
        if (uidl.hasAttribute("classid")) {
            html.append("classid=\""
                    + Util.escapeAttribute(uidl.getStringAttribute("classid"))
                    + "\" ");
        } else {
            html.append("classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" ");
        }

        /*
         * Add codebase required for ActiveX and must be exactly this according
         * to http://kb2.adobe.com/cps/415/tn_4150.html to work with the above
         * given classid. Again, see more info on
         * http://kb2.adobe.com/cps/415/tn_4150.html. Limiting Flash version to
         * 6.0.0.0 and above. Allow user to override this by setting his own
         * codebase
         */
        if (uidl.hasAttribute("codebase")) {
            html.append("codebase=\""
                    + Util.escapeAttribute(uidl.getStringAttribute("codebase"))
                    + "\" ");
        } else {
            html.append("codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0\" ");
        }

        ComponentConnector paintable = ConnectorMap.get(client).getConnector(
                this);
        String height = paintable.getState().getHeight();
        String width = paintable.getState().getWidth();

        // Add width and height
        html.append("width=\"" + Util.escapeAttribute(width) + "\" ");
        html.append("height=\"" + Util.escapeAttribute(height) + "\" ");
        html.append("type=\"application/x-shockwave-flash\" ");

        // Codetype
        if (uidl.hasAttribute("codetype")) {
            html.append("codetype=\""
                    + Util.escapeAttribute(uidl.getStringAttribute("codetype"))
                    + "\" ");
        }

        // Standby
        if (uidl.hasAttribute("standby")) {
            html.append("standby=\""
                    + Util.escapeAttribute(uidl.getStringAttribute("standby"))
                    + "\" ");
        }

        // Archive
        if (uidl.hasAttribute("archive")) {
            html.append("archive=\""
                    + Util.escapeAttribute(uidl.getStringAttribute("archive"))
                    + "\" ");
        }

        // End object tag
        html.append(">");

        // Ensure we have an movie parameter
        Map<String, String> parameters = getParameters(uidl);
        if (parameters.get("movie") == null) {
            parameters.put("movie", getSrc(uidl, client));
        }

        // Add parameters to OBJECT
        for (String name : parameters.keySet()) {
            html.append("<param ");
            html.append("name=\"" + Util.escapeAttribute(name) + "\" ");
            html.append("value=\"" + Util.escapeAttribute(parameters.get(name))
                    + "\" ");
            html.append("/>");
        }

        // Build inner EMBED tag
        html.append("<embed ");
        html.append("src=\"" + Util.escapeAttribute(getSrc(uidl, client))
                + "\" ");
        html.append("width=\"" + Util.escapeAttribute(width) + "\" ");
        html.append("height=\"" + Util.escapeAttribute(height) + "\" ");
        html.append("type=\"application/x-shockwave-flash\" ");

        // Add the parameters to the Embed
        for (String name : parameters.keySet()) {
            html.append(Util.escapeAttribute(name));
            html.append("=");
            html.append("\"" + Util.escapeAttribute(parameters.get(name))
                    + "\"");
        }

        // End embed tag
        html.append("></embed>");

        if (uidl.hasAttribute(EmbeddedConnector.ALTERNATE_TEXT)) {
            html.append(uidl
                    .getStringAttribute(EmbeddedConnector.ALTERNATE_TEXT));
        }

        // End object tag
        html.append("</object>");

        return html.toString();
    }

    /**
     * Returns a map (name -> value) of all parameters in the UIDL.
     * 
     * @param uidl
     * @return
     */
    protected static Map<String, String> getParameters(UIDL uidl) {
        Map<String, String> parameters = new HashMap<String, String>();

        Iterator<Object> childIterator = uidl.getChildIterator();
        while (childIterator.hasNext()) {

            Object child = childIterator.next();
            if (child instanceof UIDL) {

                UIDL childUIDL = (UIDL) child;
                if (childUIDL.getTag().equals("embeddedparam")) {
                    String name = childUIDL.getStringAttribute("name");
                    String value = childUIDL.getStringAttribute("value");
                    parameters.put(name, value);
                }
            }

        }

        return parameters;
    }

    /**
     * Helper to return translated src-attribute from embedded's UIDL
     * 
     * @param uidl
     * @param client
     * @return
     */
    protected String getSrc(UIDL uidl, ApplicationConnection client) {
        String url = client.translateVaadinUri(uidl.getStringAttribute("src"));
        if (url == null) {
            return "";
        }
        return url;
    }

    @Override
    protected void onDetach() {
        if (BrowserInfo.get().isIE()) {
            // Force browser to fire unload event when component is detached
            // from the view (IE doesn't do this automatically)
            if (browserElement != null) {
                /*
                 * src was previously set to javascript:false, but this was not
                 * enough to overcome a bug when detaching an iframe with a pdf
                 * loaded in IE9. about:blank seems to cause the adobe reader
                 * plugin to unload properly before the iframe is removed. See
                 * #7855
                 */
                DOM.setElementAttribute(browserElement, "src", "about:blank");
            }
        }
        super.onDetach();
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (DOM.eventGetType(event) == Event.ONLOAD) {
            VConsole.log("Embeddable onload");
            Util.notifyParentOfSizeChange(this, true);
        }

        client.handleTooltipEvent(event, this);
    }

}