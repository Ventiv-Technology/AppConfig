/**
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
package org.ventiv.appconfig;

import com.mangofactory.swagger.plugin.EnableSwagger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.ventiv.appconfig.repository.RestRepositoryConfiguration;
import org.ventiv.webjars.requirejs.EnableWebJarsRequireJs;

import java.net.URI;

/**
 * @author John Crygier
 */
@EnableSwagger
@Configuration
@ComponentScan
@EnableJpaRepositories
@Import(RestRepositoryConfiguration.class)
@EnableAutoConfiguration
@EnableWebJarsRequireJs
public class App {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public RepositoryRestConfiguration getRepositoryRestConfiguration() throws Exception {
        return new RepositoryRestConfiguration()
                    .setBaseUri(new URI("/api"));
    }

}
