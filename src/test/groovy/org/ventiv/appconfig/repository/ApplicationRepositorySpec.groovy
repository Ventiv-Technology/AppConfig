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
package org.ventiv.appconfig.repository

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 *
 *
 * @author John Crygier
 */
@WebAppConfiguration
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = App)
class ApplicationRepositorySpec extends Specification {

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
    }

}
