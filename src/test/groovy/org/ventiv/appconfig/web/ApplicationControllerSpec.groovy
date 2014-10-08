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

import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.ventiv.appconfig.App
import spock.lang.Specification

import javax.annotation.Resource

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 *
 *
 * @author John Crygier
 */
@WebAppConfiguration
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = App)
class ApplicationControllerSpec extends Specification {

    @Resource WebApplicationContext webApplicationContext;
    MockMvc mockMvc;

    def setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    def "test add duplicate"() {
        when:
        def response = mockMvc.perform(
                put("/api/application")
                    .content('{ "id": "Test_Application", "name": "Test Application" }')
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
        )

        then:
        response.andExpect(status().isCreated())

        when:   "RePost with the same name / different ID"
        def repost = mockMvc.perform(
                put("/api/application")
                        .content('{ "id": "Test_Application_2", "name": "Test Application" }')
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )

        then:
        repost.andExpect(status().isConflict())

        when:   "Fetch the persisted"
        def fetched = mockMvc.perform(
                get("/api/application/Test_Application")
        )

        then:
        fetched.andExpect(status().isOk())
        fetched.andExpect(jsonPath('$.id').value("Test_Application"))

        // Ensure that the Default environment got auto-created
        fetched.andExpect(jsonPath('$.environments[0].id').value(1))
        fetched.andExpect(jsonPath('$.environments[0].name').value("Default"))
    }

    def "insert a full application"() {
        when:
        def resp = mockMvc.perform(
                put("/api/application")
                    .content('''{
                        "id": "Test_App_Full", "name": "Test Application Full",
                        "environments": [
                            {
                                "name": "Default",
                                "propertyGroups": [
                                    {
                                        "name": null,
                                        "allProperties": [
                                            { "key": "database.url", "value": "jdbc:thin:hello" },
                                            { "key": "database.user", "value": "john" }
                                        ]
                                    }
                                ]
                            }
                        ]
                    }''')
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
        )

        then:
        resp.andExpect(status().isCreated())

        when:   "Fetch the persisted"
        def fetched = mockMvc.perform(
                get("/api/application/Test_App_Full")
        )

        then:
        fetched.andExpect(status().isOk())
        fetched.andExpect(jsonPath('$.id').value("Test_App_Full"))
        fetched.andExpect(jsonPath('$.name').value("Test Application Full"))
        fetched.andExpect(jsonPath('$.environments').isArray())
        fetched.andExpect(jsonPath('$.environments[0].name').value("Default"))
        fetched.andExpect(jsonPath('$.environments[0].propertyGroups').doesNotExist())

        when: "get the environment level information"
        fetched = mockMvc.perform(
                get("/api/application/Test_App_Full/Default")
        )

        then:
        fetched.andExpect(status().isOk())
        fetched.andExpect(jsonPath('$.propertyGroups').isArray())
        fetched.andExpect(jsonPath('$.propertyGroups[0].allProperties[0].key').value("database.url"))
        fetched.andExpect(jsonPath('$.propertyGroups[0].allProperties[0].value').value("jdbc:thin:hello"))
        fetched.andExpect(jsonPath('$.propertyGroups[0].allProperties[1].key').value("database.user"))
        fetched.andExpect(jsonPath('$.propertyGroups[0].allProperties[1].value').value("john"))
    }

}
