/* 
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.shared.ui.absolutelayout;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.AbstractLayoutState;

public class AbsoluteLayoutState extends AbstractLayoutState {
    // Maps each component to a position
    private Map<String, String> connectorToCssPosition = new HashMap<String, String>();

    public String getConnectorPosition(Connector connector) {
        return connectorToCssPosition.get(connector.getConnectorId());
    }

    public Map<String, String> getConnectorToCssPosition() {
        return connectorToCssPosition;
    }

    public void setConnectorToCssPosition(
            Map<String, String> componentToCssPosition) {
        connectorToCssPosition = componentToCssPosition;
    }

}