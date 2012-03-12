package org.vaadin.navigator7;

import org.vaadin.navigator7.Navigator.NavigationEvent;

/** Implemented by pages (Components playing the role of pages)
 * Implemented by pages that want to be told when parameters change FOR THEM (difference with NavigationListener)
 * Alternatively, you may prefer to use the UriParam mechanism: don't implment this interface and provide a paramChanged(UriParam uriParam) mehtod.
 * 
 * @author John Rizzo - BlackBeltFactory.com
 **/
public interface ParamChangeListener {
    /** If uri is "http://server.com/#auction/toto/auctionid=33"
     * newParams we get = "toto/auctionid=33" (and we are probably the AuctionPage class implementing PageParamsListener)
     * If there is no parameter in the uri (as "#auction" or "#auction/"), then newParams is null.
     */
    public void paramChanged(NavigationEvent navigationEvent);
}

