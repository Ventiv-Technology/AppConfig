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
import groovy.transform.AutoClone
import groovy.transform.Sortable

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Transient

/**
 *
 *
 * @author John Crygier
 */
@AutoClone
@Sortable(includes = ['propertyOrder', 'id', 'key'])
@Entity
class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String key;
    String value;

    Integer propertyOrder;

    @Transient
    InheritanceType inheritanceType = InheritanceType.New;

    @JsonBackReference("propertyGroup")
    @ManyToOne
    @JoinColumn(name = "PROPERTY_GROUP_ID", nullable = false)
    PropertyGroup propertyGroup;

}
