/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp;

import org.apache.camel.CamelContext;
import org.openmf.psp.config.ChannelSettings;
import org.openmf.psp.config.FspSettings;
import org.openmf.psp.config.HubSettings;
import org.openmf.psp.config.IbanSettings;
import org.openmf.psp.config.SwitchSettings;
import org.openmf.psp.mojaloop.config.MockSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = "hu.dpc.rt")
@EnableConfigurationProperties({ChannelSettings.class, HubSettings.class, FspSettings.class, SwitchSettings.class, MockSettings.class, IbanSettings.class})
public class PaymentHubApplication {

    public static void main(String[] args) {
        //if (CommonValues.getJavaVersion() == 8) {
        SpringApplication.run(PaymentHubApplication.class, args);
        //} else {
        //	System.err.println("Java 8 only!");
        //}
    }

    @Autowired
    private CamelContext camelContext;

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(30000);
        httpRequestFactory.setConnectTimeout(30000);
        httpRequestFactory.setReadTimeout(30000);

        return new RestTemplate(httpRequestFactory);
    }

	/*@Bean
	public RestTemplate restTemplate() throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		HttpClient client = createHttpClient();
		ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(client);
		restTemplate.setRequestFactory(factory);
		return restTemplate;
	}

	private HttpClient createHttpClient() throws Exception {
		InputStream trustStream = new FileInputStream(keystoreHolder.getKeystoreFile());
		char[] trustPassword = keystoreHolder.getKeystorePassword().toCharArray();

		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		trustStore.load(trustStream, trustPassword);

		final SSLContext sslContext = SSLContexts.custom()
				.loadKeyMaterial(new File(keystoreHolder.getKeystoreFile().toString()), keystoreHolder.getKeystorePassword().toCharArray(),
						keystoreHolder.getKeystorePassword().toCharArray(), (aliases, socket) -> clientKeyAlias)
				.loadTrustMaterial(trustStore, (x509Certificates, s) -> false).build();

		final SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1.2" }, null,
				SSLConnectionSocketFactory.getDefaultHostnameVerifier());

		final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", sslSocketFactory).build();

		final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
		connectionManager.setMaxTotal(10);
		connectionManager.setDefaultMaxPerRoute(10);

		return HttpClients.custom().setSSLSocketFactory(sslSocketFactory).setConnectionManager(connectionManager).build();
	}*/

//	@Bean
//	public CamelTransportFactory camelTransportFactory()
//	{
//		CamelTransportFactory camelTransportFactory = new CamelTransportFactory();
//
//		camelTransportFactory.setCamelContext(camelContext);
//
//
//		return camelTransportFactory;
//	}
}
