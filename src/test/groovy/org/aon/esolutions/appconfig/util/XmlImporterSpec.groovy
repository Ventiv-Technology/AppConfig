package org.aon.esolutions.appconfig.util;

import org.aon.esolutions.appconfig.model.Application;
import org.aon.esolutions.appconfig.model.Environment;
import org.aon.esolutions.appconfig.repository.ApplicationRepository;
import org.aon.esolutions.appconfig.repository.EnvironmentRepository;
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile

import spock.lang.Specification

public class XmlImporterSpec extends Specification {

	def applicationImportXml = """
		<application name="Test">
			<environment name="Default" visibleToAll="true" permittedUsers="[user]" permittedRoles="">
				<privateKey>MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI6fnzkzIBa2r31AXyulz5a6vhCRluUiTw89dtnqIKny+gRqPS4gQwbzBZsaN3hFg30o+Ky5XwI2sx+wgkR7pSQtfm5ageVkJptN9DFe2uRkPYy0iRP0CyqtEdWk7Kk1qLUx6v6ksdwAf0mllI++vB3+2YfUI0qHOt5MkUkmUC6hAgMBAAECgYA4w8c3mOWYpLVMAKbgVGo+76xeI9QO9ozI4AnBybGecpqXz0Dybty+k1MT3GdfcytWUUxIUq9BzkuUvUfbweqD1ftIgNoUtM8LW4jcjAOpV+07EdQbdKntDamSPvaeW8mA8TnScS9nVk55JhYuZb3PYLCnc0PibyGvVx9PEamT0QJBANyR6+n1GOMhCT5CgrOxnsZfQNBDPxHQSKP+Y4iAR8yqSHetYOJR5RVefhVRGGX2zLJ35R6UHqzKJB7a815r7NMCQQCliHTQ3Py5M7xvNMNjW0/yDQ/0TEvn3pw9OUqIsBZTXTmgV7nWnR9+hffDJfCJSpY14uj7P5Q/147ypH2UJL47AkBipgTMM2l5E+ptZRPNyHAJqspzGtBXaD9E0V60yyLRn4sfs3R0ZEo/324mnIl7+QCeBU98+KQrhZDYF/bm0QAvAkBNPm30gQrViZTm9+ItXRDjTMEjKO9K1hBmaXOkcTcZ06jub8FiqpNlwTIquGuURV/u6WH6zGJroDUueR+4n6S9AkEAlSuc4t/Vf3wAnm2dFjfbMzaC5q4WMXrjUhAVe2GLIUpv8bKkfEuI3M5ebahvU5FcH+PUyL6jT1SJSHDiUykojA==</privateKey>
				<publicKey>MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCOn585MyAWtq99QF8rpc+Wur4QkZblIk8PPXbZ6iCp8voEaj0uIEMG8wWbGjd4RYN9KPisuV8CNrMfsIJEe6UkLX5uWoHlZCabTfQxXtrkZD2MtIkT9AsqrRHVpOypNai1Mer+pLHcAH9JpZSPvrwd/tmH1CNKhzreTJFJJlAuoQIDAQAB</publicKey>
				<properties>
					<entry key="database.password" encrypted="true">AyU76ffxTehYDRYgmi4lu2QaZbgjXvmGzfj8efmd9YI+sxeMsH1WH513D8yoehm0CoegWsaOfwmMUxU+iu5rfHbGkR3ME5DJ5rOQ2N+h3d1UJF6U4FgfAGEUWBG3sQNZI9A6lPvOhwVXdkgOrQFXUibBytuQyfYfx2QNNw4uKvE=</entry>
					<entry key="database.url">http://localhost/test</entry>
				</properties>
				<environment name="Development" visibleToAll="true" permittedUsers="[user]" permittedRoles="">
				</environment>
			</environment>
		</application>
	"""
		
	def "test importing golden data - nothing exists yet"() {
		setup:
		def appRepository = Mock(ApplicationRepository)
		def envRepository = Mock(EnvironmentRepository)
		def updateUtility = Mock(UpdateUtility)
		def xmlFile = new MockMultipartFile("application.xml", applicationImportXml.getBytes())
		
		when:
		new XmlImporter(appRepository, envRepository, updateUtility).importFromXml(xmlFile, "full")
		
		then:
		1 * appRepository.save({ it instanceof Application && it.name == "Test" && it.environments.size() == 2 })
		1 * updateUtility.savePrivateKeyHolder({ it.privateKey.startsWith("MIICdgIBADANBgkqhkiG9") })
		1 * envRepository.save({ 
			it instanceof Environment && it.name == "Default" && it.getVisibleToAll() && it.getPermittedUsers() == ["user"] && 
			it.publicKey.startsWith("MIGfMA0GCSqGSIb3DQEB") && it.privateKeyHolder != null && it.get("database.url") == "http://localhost/test" &&
			it.getEncryptedVariables() == ["database.password"] && it.get("database.password").startsWith("AyU76ffxTehY") &&
			it.children.iterator().next().name == "Development" && it.privateKeyHolder.privateKey.startsWith("MIICdgIBADANBgkqhkiG9")
		})
		1 * envRepository.save({it instanceof Environment && it.name == "Development" && it.parent.name == "Default"})
	}
	
	def "test importing data - app with single env already exists"() {
		setup:
		def appRepository = Mock(ApplicationRepository)
		def envRepository = Mock(EnvironmentRepository)
		def updateUtility = Mock(UpdateUtility)
		def xmlFile = new MockMultipartFile("application.xml", applicationImportXml.getBytes())
		appRepository.findByName("Test") >> new Application([name : "Test"])
		
		when:
		new XmlImporter(appRepository, envRepository, updateUtility).importFromXml(xmlFile, "full")
		
		then:
		true
		1 * envRepository.delete(_)
	}
	
	private MultipartFile readFile(String fileName) {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)
		return new MockMultipartFile(fileName, (InputStream)is);
	}

}
