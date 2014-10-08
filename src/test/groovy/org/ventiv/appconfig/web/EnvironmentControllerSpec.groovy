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
package org.ventiv.appconfig.web

import groovy.json.JsonSlurper
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.ventiv.appconfig.App
import org.ventiv.appconfig.model.Application
import org.ventiv.appconfig.model.Environment
import org.ventiv.appconfig.model.PropertyGroup
import spock.lang.Specification

import javax.annotation.Resource

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 *
 *
 * @author John Crygier
 */
@WebAppConfiguration
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = App)
class EnvironmentControllerSpec extends Specification {

    @Resource ApplicationController applicationController;
    @Resource WebApplicationContext webApplicationContext;
    MockMvc mockMvc;

    def setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    def "inheritance"() {
        when:
        insertTestData();
        def fetched = mockMvc.perform(
                get("/api/application/EnvironmentControllerSpec/Development")
        )
        def parsedResult = new JsonSlurper().parseText(fetched.andReturn().getResponse().getContentAsString())

        then:
        fetched.andExpect(status().isOk())
        parsedResult.propertyGroups.size() == 2

        parsedResult.propertyGroups[1].name == null
        parsedResult.propertyGroups[1].allProperties.size() == 4
        parsedResult.propertyGroups[1].allProperties[0].key == 'database.url'
        parsedResult.propertyGroups[1].allProperties[0].value == 'jdbc:thin:hello'
        parsedResult.propertyGroups[1].allProperties[0].inheritanceType == 'Inherited'
        parsedResult.propertyGroups[1].allProperties[1].key == 'property.1'
        parsedResult.propertyGroups[1].allProperties[1].value == 'one'
        parsedResult.propertyGroups[1].allProperties[1].inheritanceType == 'Inherited'
        parsedResult.propertyGroups[1].allProperties[2].key == 'database.user'
        parsedResult.propertyGroups[1].allProperties[2].value == 'devuser'
        parsedResult.propertyGroups[1].allProperties[2].inheritanceType == 'Overridden'
        parsedResult.propertyGroups[1].allProperties[3].key == 'database.password'
        parsedResult.propertyGroups[1].allProperties[3].value == 'password'
        parsedResult.propertyGroups[1].allProperties[3].inheritanceType == 'OverriddenUnchanged'

        parsedResult.propertyGroups[0].name == "LonePropertyGroup"
        parsedResult.propertyGroups[0].allProperties.size() == 1
        parsedResult.propertyGroups[0].allProperties[0].key == 'lone.property.group'
        parsedResult.propertyGroups[0].allProperties[0].value == 'hello world'
        parsedResult.propertyGroups[0].allProperties[0].inheritanceType == 'Inherited'

        when: "get 3rd level (Default -> Development -> Super_Development"
        fetched = mockMvc.perform(
                get("/api/application/EnvironmentControllerSpec/Super_Development")
        )
        parsedResult = new JsonSlurper().parseText(fetched.andReturn().getResponse().getContentAsString())

        then:
        fetched.andExpect(status().isOk())
        parsedResult.propertyGroups.size() == 2

        parsedResult.propertyGroups[1].name == null
        parsedResult.propertyGroups[1].allProperties.size() == 4
        parsedResult.propertyGroups[1].allProperties[0].key == 'database.url'
        parsedResult.propertyGroups[1].allProperties[0].value == 'jdbc:thin:hello'
        parsedResult.propertyGroups[1].allProperties[0].inheritanceType == 'Inherited'
        parsedResult.propertyGroups[1].allProperties[1].key == 'database.user'
        parsedResult.propertyGroups[1].allProperties[1].value == 'devuser'
        parsedResult.propertyGroups[1].allProperties[1].inheritanceType == 'Overridden'
        parsedResult.propertyGroups[1].allProperties[2].key == 'database.password'
        parsedResult.propertyGroups[1].allProperties[2].value == 'password'
        parsedResult.propertyGroups[1].allProperties[2].inheritanceType == 'OverriddenUnchanged'
        parsedResult.propertyGroups[1].allProperties[3].key == 'property.1'
        parsedResult.propertyGroups[1].allProperties[3].value == 'one override'
        parsedResult.propertyGroups[1].allProperties[3].inheritanceType == 'Overridden'

        parsedResult.propertyGroups[0].name == "LonePropertyGroup"
        parsedResult.propertyGroups[0].allProperties.size() == 1
        parsedResult.propertyGroups[0].allProperties[0].key == 'lone.property.group'
        parsedResult.propertyGroups[0].allProperties[0].value == 'hello world'
        parsedResult.propertyGroups[0].allProperties[0].inheritanceType == 'Inherited'
    }

    def "application not found"() {
        when:
        insertTestData();
        def fetched = mockMvc.perform(
                get("/api/application/NotFound/Development")
        )

        then:
        fetched.andExpect(status().isNotFound());
    }

    def "environment not found"() {
        when:
        insertTestData();
        def fetched = mockMvc.perform(
                get("/api/application/EnvironmentControllerSpec/NotFound")
        )

        then:
        fetched.andExpect(status().isNotFound());
    }


    private void insertTestData() {
        Application app = new Application([id: "EnvironmentControllerSpec", name: "Application for EnvironmentControllerSpec"]);
        Environment defaultEnv = app.addEnvironment(new Environment([name: "Default"]))
        defaultEnv.addPropertyGroup(new PropertyGroup([
                'database.url': 'jdbc:thin:hello',
                'database.user': 'john',
                'database.password': 'password',
                'property.1': "one"
        ]))
        defaultEnv.addPropertyGroup(new PropertyGroup([
                'lone.property.group': 'hello world'
        ])).setName("LonePropertyGroup")

        Environment devEnv = app.addEnvironment(new Environment([name: "Development", parent: defaultEnv]))
        devEnv.addPropertyGroup(new PropertyGroup([
                'database.user': 'devuser',
                'database.password': 'password'
        ]))

        Environment superDevEnv = app.addEnvironment(new Environment([name: "Super_Development", parent: devEnv]))
        superDevEnv.addPropertyGroup(new PropertyGroup([
                'property.1': "one override"
        ]))

        applicationController.insert(app);
    }



}