# AppConfig - Application and Properties Configuration

This application was created in an effort to kick start Aon eSolution's DevOps movement by introducing tools that allow for Continuous Delivery.

At it's heart, AppConfig simply allows for the following structure:
- Application
    - Environment
        - Properties

This structure has a nice UI for editing, as well as a RESTful API backing it, for simple integration with applications.  So far, there are two clients written:
- Java Client
- Facter Client (For use with Puppet)

On top of simple properties editing, the system allows for Environment Extension.  For example, we might have a sample setup as follows
- AppConfig (Application)
    - Default
        - Development
        - Quality
        - Production

In this scenario, Development, Quality, and Production will inherit properties from Default, and can override environment specific settings...such as a database URL and Password.  This hierarchy can be as many levels deep as desired.

Also incorporated into AppConfig is a robust security model.  Each environment can be assigned to Users, Roles, or Machines (IP Address / DNS Name).  If the environment is assigned to anything, you also have the option to make it "invisible" to anyone except for those that may edit it.  Should you choose to keep it open to the public, you also have the ability to encrypt individual properties, so that only the owners may decrypt it.

Encryption / Decryption is a central feature to AppConfig.  This is one of the features that truly allows for security in the Enterprise.  With this enabled, it is possible for anyone to see what "Production" might look like, but not allow anyone to see protected passwords.  This can be reserved for administrators and machines.