package org.aon.esolutions.appconfig.util;

import org.aon.esolutions.appconfig.model.Application
import org.aon.esolutions.appconfig.model.Environment
import org.aon.esolutions.appconfig.model.PrivateKeyHolder;
import org.aon.esolutions.appconfig.repository.ApplicationRepository
import org.aon.esolutions.appconfig.repository.EnvironmentRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

public class XmlImporter {
	
	private ApplicationRepository appRepository;
	private EnvironmentRepository envRepository;
	
	private Application currentApplication;
	private Environment currentEnvironment;
	private UpdateUtility updateUtility;
	
	public XmlImporter(ApplicationRepository appRepository, EnvironmentRepository envRepository, UpdateUtility updateUtility) {
		this.appRepository = appRepository;
		this.envRepository = envRepository;
		this.updateUtility = updateUtility;
	}

	public void importFromXml(MultipartFile multipartFile, String importMode) {
		if (multipartFile.getName().endsWith(".xml") == false && multipartFile.getOriginalFilename().endsWith(".xml") == false)
			return;
		
		XmlSlurper slurper = new XmlSlurper();
		def parsedXml = slurper.parse(multipartFile.getInputStream())
		
		this."${parsedXml.name()}"(parsedXml)
	}

	def application(def appNode) {
		// See if this guy is existing
		Application existingApp = appRepository.findByName(appNode.@name.toString());
		println existingApp
		
		currentApplication = new Application()
		setAttributesInObject(currentApplication, appNode);
		handleChildren(appNode);
		
		appRepository.save(currentApplication);
	}
	
	def environment(def envNode) {
		currentEnvironment = new Environment([parent: currentEnvironment])
		setAttributesInObject(currentEnvironment, envNode);
		
		if (currentApplication.environments)
			currentApplication.environments << currentEnvironment
		else
			currentApplication.environments = [currentEnvironment]			

		if (currentEnvironment.parent && currentEnvironment.parent.children)
			currentEnvironment.parent.children << currentEnvironment
		else if (currentEnvironment.parent)
			currentEnvironment.parent.children = [currentEnvironment]
		
		handleChildren(envNode);
		
		envRepository.save(currentEnvironment)
		currentEnvironment = currentEnvironment.parent;
	}
	
	def privateKey(def pkNode) {
		PrivateKeyHolder pkHolder = new PrivateKeyHolder()
		pkHolder.privateKey = pkNode.text()
		pkHolder.environment = currentEnvironment;
		currentEnvironment.privateKeyHolder = pkHolder;
		
		updateUtility.savePrivateKeyHolder(pkHolder);
	}
	
	def publicKey(def pkNode) {
		currentEnvironment.publicKey = pkNode.text()
	}
	
	def properties(def propertiesNode) {
		propertiesNode.children().each { propNode ->
			currentEnvironment.put(propNode.@key.toString(), propNode.text())
			if (propNode.@encrypted.toString().equalsIgnoreCase("true")) {
				currentEnvironment.addEncryptedVariable(propNode.@key.toString());
			}
		}
	}
	
	private void setAttributesInObject(def obj, def node) {
		node.attributes().each {
			if (it.value.startsWith('[') && it.value.endsWith(']'))
				obj."$it.key" = it.value.substring(1, it.value.size() - 1).split(",")
			else if (it.value.isEmpty() == false)
				obj."$it.key" = it.value
		}
	}
	
	private void handleChildren(def node) {
		node.children().each {
			this."${it.name()}"(it)
		}
	}
}
