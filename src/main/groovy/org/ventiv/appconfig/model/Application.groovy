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

import com.fasterxml.jackson.annotation.JsonManagedReference

import javax.persistence.CascadeType
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.OneToMany

/**
 * @author John Crygier
 */
@Entity
public class Application {

    @Id
    String id;

    String name;

    @JsonManagedReference("application")
    @OneToMany(cascade=CascadeType.ALL, mappedBy="application", fetch = FetchType.EAGER)
    Set<Environment> environments = [];

    @ElementCollection
    List<String> ownerRoles;

    @ElementCollection
    List<String> ownerLogins;

    public Environment addEnvironment(Environment environment) {
        environment.setApplication(this);
        getEnvironments().add(environment);

        return environment;
    }

}
