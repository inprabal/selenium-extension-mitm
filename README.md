To create MITM rootCA certificate run

$ mvn compile exec:java

SSL certificate will be created in the folder >>> src/main/resources

Create client certificate of rootCA

$ keytool -exportcert -keystore rootMitmCA.p12 -storetype PKCS12 -storepass Change1t -alias inprabal_root_ca -file rootMitmCA.crt

Install rootMitmCA.crt as Trusted Root Certificate in local machine/ browser