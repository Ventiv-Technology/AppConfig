/*
 * Copyright (c) 2014 Ventiv Technology
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
package org.ventiv.appconfig.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import groovy.transform.Sortable

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

/**
 *
 *
 * @author John Crygier
 */
@Sortable(includes = ['groupOrder', 'id', 'name'])
@Entity
class PropertyGroup {

    public PropertyGroup() {    }

    public PropertyGroup(Map<String, String> properties) {
        this.allProperties = properties.collect { k, v ->
            Property prop = new Property([key: k, value: v])
            prop.setPropertyGroup(this);

            return prop;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String name;

    Integer groupOrder;

    @JsonBackReference("environment")
    @ManyToOne
    @JoinColumn(name = "ENVIRONMENT_ID", nullable = false)
    Environment environment;

    @JsonManagedReference("propertyGroup")
    @javax.persistence.OrderBy("propertyOrder ASC, id ASC")
    @OneToMany(cascade=CascadeType.ALL, mappedBy="propertyGroup", fetch = FetchType.EAGER)
    List<Property> allProperties = [];

    public Property addProperty(Property property) {
        property.setPropertyGroup(this)
        getAllProperties().add(property)

        return property;
    }

}
