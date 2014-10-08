package org.ventiv.appconfig.model

import com.fasterxml.jackson.annotation.JsonBackReference

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 *
 *
 * @author John Crygier
 */
@Entity
class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String key;
    String value;

    @JsonBackReference("propertyGroup")
    @ManyToOne
    @JoinColumn(name = "PROPERTY_GROUP_ID", nullable = false)
    PropertyGroup propertyGroup;

}
