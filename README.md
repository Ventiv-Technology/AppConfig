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

Application Configuration Rewrite
---------------------------------

It's been decided to rewrite Application Configuration since it's a small application, and we can take advantage of some more 'modern'
frameworks like Spring Boot, AngularJS, and more while changing some fundamental things.  The goals of the rewrite are the following:

- Relational Database.  Since Ventiv technology will use this in a production atmosphere, our support team feels more comfortable with data storage that we use elsewhere.  So, Spring Data JPA will be used with Spring Boot to be able to attach to any popular Relational DB.
- Code reduction by using libraries instead of custom code (Angular vs JQuery should help here with data binding)
- The following are additional features that may be tackled
    - Properties Grouping.  Ability to group properties together, instead of just having them alphabetical
    - Versioning.  Have a log of all changes done to an Application / Environment by user (if logged in) or by other information such as IP
    - Comparing 2 environments (http://ejohn.org/projects/javascript-diff-algorithm/)
    - Better Visual Comparison when overridden - Maybe use http://ejohn.org/projects/javascript-diff-algorithm/ or https://github.com/webjars/diff
    - Recent Changes
    - Use XEditable: http://vitalets.github.io/angular-xeditable/.  This way the editor can be a TextArea which makes things easier to edit
    - Define Data Type for property?  So we can have text box vs select vs date.  Same lines as property validation.
        - Enter at Default Environment, or Application?
        - Flag encryption at this level?