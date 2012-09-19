package org.aon.esolutions.appconfig.web.controller;

import java.security.Key;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

import org.aon.esolutions.appconfig.model.Application;
import org.aon.esolutions.appconfig.model.Environment;
import org.aon.esolutions.appconfig.repository.ApplicationRepository;
import org.aon.esolutions.appconfig.repository.EnvironmentRepository;
import org.aon.esolutions.appconfig.util.InheritanceUtil;
import org.aon.esolutions.appconfig.util.RSAEncryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.crygier.spring.util.web.MimeTypeViewResolver.ResponseMapping;

@Controller
@RequestMapping("/application/{applicationName}/environment")
public class EnvironmentController {
	
	@Autowired private ApplicationRepository applicationRepository;	
	@Autowired private EnvironmentRepository environmentRepository;	
	@Autowired private Neo4jTemplate template;
	@Autowired private InheritanceUtil inheritanceUtil;

	@RequestMapping(value = "/{environmentName}", method = RequestMethod.GET)
	@ResponseMapping("environmentDetails")
	public Environment getEnvironment(@PathVariable String applicationName, @PathVariable String environmentName) {
		// TODO: Push this into the Repo by writing a query
		Application app = applicationRepository.findByName(applicationName);
		template.fetch(app.getEnvironments());
		
		for (Environment anEnvironment : app.getEnvironments()) {
			if (anEnvironment.getName().equals(environmentName)) {
				RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
				
				if (attributes != null)
					attributes.setAttribute("allVariables", inheritanceUtil.getVariablesForEnvironment(anEnvironment), RequestAttributes.SCOPE_REQUEST);
					
				return anEnvironment;
			}
		}
		
		return null;
	}
	
	@RequestMapping(value = "/{environmentName}", method = RequestMethod.PUT)
	public Environment addEnvironment(@PathVariable String applicationName, @PathVariable String environmentName, @RequestParam("parentId") String parentId) {
		Application app = applicationRepository.findByName(applicationName);
		Environment parent = environmentRepository.findOne(Long.parseLong(parentId));
		
		Environment newEnv = new Environment();
		newEnv.setName(environmentName);
		newEnv.setParent(parent);
		app.addEnvironment(newEnv);
		
		return newEnv;
	}
	
	@RequestMapping(value = "/{environmentName}/keys", method = RequestMethod.POST)
	public Map<String, String> updateKeys(@PathVariable String applicationName, @PathVariable String environmentName) throws Exception {
		Environment env = getEnvironment(applicationName, environmentName);
		
		Map<String, String> answer = updateKeys(env);
		environmentRepository.save(env);
		return answer;
	}
	
	private Map<String, String> updateKeys(Environment env) throws Exception {
		Map<String, String> answer = new HashMap<String, String>();

		if (env != null) {
			// First, decrypt all values
			Key key = RSAEncryptUtil.getPrivateKeyFromString(env.getPrivateKey());
			for (String encryptedVariable : env.getEncryptedVariables()) {
				String encryptedValue = env.get(encryptedVariable);
				if (encryptedValue != null) {
					String decryptedValue = RSAEncryptUtil.decrypt(encryptedValue, key);
					env.put(encryptedVariable, decryptedValue);
				}
			}

			// Generate the new keys
			KeyPair keyPair = RSAEncryptUtil.generateKey();
			env.setPublicKey(RSAEncryptUtil.getKeyAsString(keyPair.getPublic()));
			env.setPrivateKey(RSAEncryptUtil.getKeyAsString(keyPair.getPrivate()));
			
			// Re-encrypt with the new values
			for (String encryptedVariable : env.getEncryptedVariables()) {
				String decryptedValue = env.get(encryptedVariable);
				if (decryptedValue != null) {
					String encryptedValue = RSAEncryptUtil.encrypt(decryptedValue, keyPair.getPublic());
					env.put(encryptedVariable, encryptedValue);
				}
			}
			
			answer.put("publicKey", env.getPublicKey());
			answer.put("privateKey", env.getPrivateKey());
		}
		
		return answer;
	}
	
	@RequestMapping(value = "/{environmentName}/variable/{existingKey:.*}", method = RequestMethod.POST)
	public void updateVariable(@PathVariable String applicationName, @PathVariable String environmentName, 
			                  @PathVariable String existingKey, @RequestParam("key") String updatedKey, @RequestParam("value") String updatedValue) {
		Environment env = getEnvironment(applicationName, environmentName);
		if (env != null) {
			env.remove(existingKey);
			env.put(updatedKey.trim(), updatedValue);
			env.getEncryptedVariables().remove(existingKey);
			
			environmentRepository.save(env);
		}
	}
	
	@RequestMapping(value = "/{environmentName}/variable/{existingKey}/encrypt", method = RequestMethod.POST)
	public  Map<String, String> encryptVariable(@PathVariable String applicationName, @PathVariable String environmentName, @PathVariable String existingKey) throws Exception {
		Environment env = getEnvironment(applicationName, environmentName);
		if (env != null) {
			String existingValue = env.get(existingKey);
			Key key = RSAEncryptUtil.getPublicKeyFromString(env.getPublicKey());
			String encryptedValue = RSAEncryptUtil.encrypt(existingValue, key);
			env.put(existingKey, encryptedValue);
			env.addEncryptedVariable(existingKey);
			
			environmentRepository.save(env);
			
			Map<String, String> answer = new HashMap<String, String>();
			answer.put("encryptedValue", encryptedValue);
			return answer;
		}
		
		return null;
	}
	
	@RequestMapping(value = "/{environmentName}/variable/{existingKey}/decrypt", method = RequestMethod.POST)
	public  Map<String, String> decryptVariable(@PathVariable String applicationName, @PathVariable String environmentName, @PathVariable String existingKey) throws Exception {
		Environment env = getEnvironment(applicationName, environmentName);
		if (env != null) {
			String existingValue = env.get(existingKey);
			Key key = RSAEncryptUtil.getPrivateKeyFromString(env.getPrivateKey());
			String decryptedValue = RSAEncryptUtil.decrypt(existingValue, key);
			env.put(existingKey, decryptedValue);
			env.getEncryptedVariables().remove(existingKey);
			
			if (env.getEncryptedVariables().isEmpty())
				env.addEncryptedVariable("____dummy_key_bug_work_around___");		// bug workaround
			
			environmentRepository.save(env);
			
			Map<String, String> answer = new HashMap<String, String>();
			answer.put("decryptedValue", decryptedValue);
			return answer;
		}
		
		return null;
	}
	
	@RequestMapping(value = "/{environmentName}/variable/{existingKey:.*}", method = RequestMethod.DELETE)
	public void deleteVariable(@PathVariable String applicationName, @PathVariable String environmentName, @PathVariable String existingKey) {
		Environment env = getEnvironment(applicationName, environmentName);
		if (env != null) {
			env.remove(existingKey);
			env.getEncryptedVariables().remove(existingKey);
			
			environmentRepository.save(env);
		}
	}
}
