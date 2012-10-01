require 'facter'
require 'test/unit'
require_relative '../../main/ruby/AppConfigClient'

include Test::Unit::Assertions

class TestAssertion < Test::Unit::TestCase
  def test_appconfig_server_url
    assert Facter.value('appconfig_server_url').nil? == false, "Server URL Was not Found"
    assert Facter.value('appconfig_server_url') == "http://localhost:8080/AppConfig", "Result Was: " + Facter.value('appconfig_server_url')
    assert Facter.value('appconfig_username') == "admin"
    assert Facter.value('appconfig_password') == "admin"
    assert Facter.value('database.url') == 'http://localhost/visibletoall'
  end
end