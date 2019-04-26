# payment-hub
Repository to house the payment hub for integration with external payment platforms like Mojaloop

Payment Hub is the component, which connects DFSPs to switches. In our case, it connects Fineract CN to Mojaloop, and Fineract v1.2 to Mojaloop. The Payment Hub communicates with the Fineract versions via REST API. To support the calls and actions, both Fineract versions had to be upgraded.


## Build

Put a settings.xml into your ~/.m2 directory with the following content:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd" xmlns="http://maven.apache.org/SETTINGS/1.1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <servers>
      <server>
      <username>mb-gatesprojects</username>
      <password>ModusBox</password>
      <id>modusbox-release-local</id>
    </server>
  </servers>
  <profiles>
    <profile>
      <repositories>
        <repository>
          <id>central</id>
          <name>Central Repository</name>
          <url>http://repo.maven.apache.org/maven2</url>
          <layout>default</layout>
          <snapshots><enabled>false</enabled></snapshots>
        </repository>
        <repository>
          <snapshots><enabled>false</enabled></snapshots>
          <id>modusbox-release-local</id>
          <name>libs-release</name>
          <url>https://modusbox.jfrog.io/modusbox/libs-release</url>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>central</id>
          <name>Central Repository</name>
          <url>http://repo.maven.apache.org/maven2</url>
          <layout>default</layout>
          <snapshots><enabled>false</enabled></snapshots>
          <releases><updatePolicy>never</updatePolicy></releases>
        </pluginRepository>
        <pluginRepository>
          <snapshots><enabled>false</enabled></snapshots>
          <id>modusbox-plugin-release</id>
          <name>plugins-release</name>
          <url>https://modusbox.jfrog.io/modusbox/plugins-release</url>
        </pluginRepository>
      </pluginRepositories>
      <id>artifactory</id>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>artifactory</activeProfile>
  </activeProfiles>
</settings>
```

Navigate to the sources folder in the payment-hub project and run the following:

    mvn clean package
    
## Deploy

Copy the paymenthub/sources/payment-hub/target/payment-hub-1.0.0-SNAPSHOT.jar file to your working directory.

Copy the application.yml file to the same working directory, and update its config. It is not included in the build jar file, to make it easily editable.


#### FSP - Fineract 1.X settings
    fsp-settings:
      ilp-secret: h4on38bsDjKiat2783gnklgafikmeuu5123kpobb7jm99
      auth:
        profile: BASIC # NONE, BASIC, BASIC_TWOFACTOR, OAUTH, OAUTH_TWOFACTOR
        encode: NONE # NONE, BASE64
        login-class: hu.dpc.rt.psp.dto.fsp.LoginFineractXResponseDTO
      headers:
      - name: user
        key: User
      - name: tenant
        key: Fineract-Platform-TenantId
      operations: #hub -> fsp
      - name: operation-basic-settings
        user: mifos
        password: password
        host: http://localhost
        port: 8080
      - name: auth #login
        base: fineract-provider/api/v1/authentication
      - name: requests
        base: fineract-provider/api/v1/interoperation/requests
      - name: parties
        base: fineract-provider/api/v1/interoperation/parties
      - name: quotes
        base: fineract-provider/api/v1/interoperation/quotes
      - name: transfers
        base: fineract-provider/api/v1/interoperation/transfers


#### FSP - Fineract CN settings
    fsp-settings:
      ilp-secret: llklokklsllakkskksnnqweq6665446a6sd4asdlkjaf
      auth:
        profile: OAUTH # NONE, BASIC, BASIC_TWOFACTOR, OAUTH, OAUTH_TWOFACTOR
        encode: BASE64 # NONE, BASE64
        login-class: hu.dpc.rt.psp.dto.fsp.LoginFineractCnResponseDTO
      headers:
      - name: user
        key: User
      - name: tenant
        key: X-Tenant-Identifier
      operations: #hub -> fsp
      - name: operation-basic-settings
        user: interopUser
        password: intop@d1
        host: http://payments.dpc.hu
        port: 80 #2034
      - name: auth #login
        port: 80 #2021
        base: /identity/v1/token
      - name: requests
        base: /interoperation/v1/transactions
      - name: parties
        base: /interoperation/v1/parties
      - name: quotes
        base: /interoperation/v1/quotes
      - name: transfers
        base: /interoperation/v1/transfers
