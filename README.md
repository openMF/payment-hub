# payment-hub
Repository to house the payment hub for integration with external payment platforms like Mojaloop

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
