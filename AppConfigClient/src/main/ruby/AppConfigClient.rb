# Custom Client intended for use in Puppet's Facter - http://docs.puppetlabs.com/guides/custom_facts.html
# Put this ruby file on the file system somewhere and point facter libs at it: export FACTERLIB="/path/to/facter/libraries"
# MUST export environment variables: APPCONFIG_SERVERURL, APPCONFIG_APPLICATION, APPCONFIG_ENVIRONMENT.  APPCONFIG_USERNAME / APPCONFIG_PASSEWORD are optional
require 'net/http'
require 'yaml'

class AppConfigClientSettings
  def initialize(serverUrl = ENV["APPCONFIG_SERVERURL"], userName = ENV["APPCONFIG_USERNAME"], password = ENV["APPCONFIG_PASSWORD"])
    setServerUrl serverUrl
    setUserName userName
    setPassword password
  end

  def setServerUrl serverUrl
    @@serverUrl = serverUrl

    Facter.add("appconfig_server_url") do
      setcode do
        @@serverUrl
      end
    end
  end

  def setUserName userName
    @@userName = userName

    Facter.add("appconfig_username") do
      setcode do
        @@userName
      end
    end
  end

  def setPassword password
    @@password = password

    Facter.add("appconfig_password") do
      setcode do
        @@password
      end
    end
  end

  def fetchProperties applicationName, environmentName
    uri = URI(@@serverUrl + "/application/#{applicationName}/environment/#{environmentName}")
    req = Net::HTTP::Get.new(uri.request_uri)
    req['Accept'] = "text/yaml"
    if @@userName != nil
      req.basic_auth @@userName, @@password
    end

    res = Net::HTTP.start(uri.host, uri.port) { |http|
      http.request(req)
    }

    YAML::load( res.body )
  end

  def pushPropertiesIntoFacter applicationName, environmentName
    fetchProperties(applicationName, environmentName).each do | key, value |
      Facter.add(key) do
        setcode do
          value
        end
      end
    end
  end

end

ac = AppConfigClientSettings.new
ac.pushPropertiesIntoFacter ENV["APPCONFIG_APPLICATION"], ENV["APPCONFIG_ENVIRONMENT"]