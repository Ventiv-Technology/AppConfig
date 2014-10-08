package org.ventiv.appconfig.model

import com.fasterxml.jackson.annotation.JsonBackReference
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

/**
 *
 *
 * @author John Crygier
 */
@Entity
class PropertyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String name;

    @JsonBackReference("environment")
    @ManyToOne
    @JoinColumn(name = "ENVIRONMENT_ID", nullable = false)
    Environment environment;

    @JsonManagedReference("propertyGroup")
    @OneToMany(cascade=CascadeType.ALL, mappedBy="propertyGroup", fetch = FetchType.EAGER)
    Set<Property> properties;

}
