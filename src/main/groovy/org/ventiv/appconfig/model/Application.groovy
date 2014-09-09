package org.ventiv.appconfig.model

import org.springframework.hateoas.ResourceSupport

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author John Crygier
 */
@Entity
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String name;

    @ElementCollection
    List<String> ownerRoles;

    @ElementCollection
    List<String> ownerLogins;

}
