# payment-hub
Repository to house the payment hub for integration with external payment platforms like Mojaloop and Over-The-Top APIs like GSMA Mobile Money.

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

#### OTT - GSMA Settings
    ott-settings:
      cors-enabled: true
      apikey: u8YfSQNnNsGFAaqRm3sGShpO2ywLRJgs
      operations: #hub -> ott
      - name: operation-basic-settings
        host: https://sandbox.mobilemoneyapi.io/simulator/v1.0/mm
        tenants:
        - name: tn03
          port: 48888
        - name: tn04
          port: 48889
      - name: transactions
        base: transactions
      - name: accounts
        base: accounts
      bindings: #ott -> hub
      - name: binding-basic-settings
        host: http://0.0.0.0
        port: 58080
      - name: merchantpayment # post merchant payment
        base: merchantpayment
      - name: transfer # post peer-to-peer transfer
        base: transfer

Currently, the basic transaction use cases - Merchant Payment and Peer-To-Peer transfer - have been integrated with the Payment Hub.
