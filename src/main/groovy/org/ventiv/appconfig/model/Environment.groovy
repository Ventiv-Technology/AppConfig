/*
 * Copyright (c) 2013 - 2015 Ventiv Technology
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
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OneToOne

/**
 *
 *
 * @author John Crygier
 */
@Entity
class Environment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String name;

    @JsonBackReference("parent")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PARENT_ENVIRONMENT_ID", nullable = true)
    Environment parent;

    @JsonBackReference("application")
    @ManyToOne
    @JoinColumn(name = "APPLICATION_ID", nullable = false)
    Application application;

    @JsonManagedReference("environment")
    @OneToMany(cascade=CascadeType.ALL, mappedBy="environment", fetch = FetchType.LAZY)
    Set<PropertyGroup> propertyGroups = [];

    public PropertyGroup addPropertyGroup(PropertyGroup propertyGroup) {
        propertyGroup.setEnvironment(this)
        getPropertyGroups().add(propertyGroup)

        return propertyGroup;
    }

    @JsonIgnore
    public List<Property> getAllProperties() {
        return propertyGroups.allProperties.flatten()
    }
}
