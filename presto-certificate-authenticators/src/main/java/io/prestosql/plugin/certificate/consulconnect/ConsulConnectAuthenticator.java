/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.plugin.certificate.consulconnect;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.airlift.log.Logger;
import io.prestosql.spi.security.AccessDeniedException;
import io.prestosql.spi.security.CertificateAuthenticator;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.inject.Inject;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.HashMap;

public class ConsulConnectAuthenticator
        implements CertificateAuthenticator
{
    private static final Logger log = Logger.get(ConsulConnectAuthenticator.class);
    private static final String SPIFFE_PREFIX = "spiffe";

    private final boolean tlsEnabled;
    private final String consulAddr;
    private final String consulToken;
    private final String consulService;
    HashMap<String, Object> auth;

    @Inject
    public ConsulConnectAuthenticator(ConsulConnectConfig serverConfig)
    {
        this.tlsEnabled = serverConfig.getTlsEnabled();
        this.consulAddr = serverConfig.getConsulAddr();
        this.consulService = serverConfig.getConsulService();
        this.consulToken = serverConfig.getConsulToken();
    }

    @Override
    public Principal authenticate(X509Certificate[] certs) throws AccessDeniedException
    {
        log.info("Authenticating using principal: " + certs[0].getSubjectX500Principal());
        String serialNumber = String.valueOf(certs[0].getSerialNumber());
        String cert = certs[0].toString().trim();
        String spiffeId = cert.substring(cert.indexOf(SPIFFE_PREFIX), cert.indexOf("svc/")) + "svc/" + certs[0].getSubjectX500Principal().toString().split("=")[1];
        try {
            this.auth = new ObjectMapper().readValue(this.authorize(serialNumber, spiffeId), HashMap.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("auth object:" + auth.toString());
        if (this.auth.get("Authorized").equals(true)) {
            return certs[0].getSubjectX500Principal();
        }
        else {
            throw new AccessDeniedException((String) this.auth.get("Reason"));
        }
    }

    private String authorize(String serialNumber, String spiffeId) throws Exception
    {
        HttpUriRequest request = RequestBuilder.post()
                .setUri(consulAddr + "/v1/agent/connect/authorize")
                .setHeader("X-Consul-Token", consulToken)
                .setEntity(new StringEntity(String.valueOf(new ObjectMapper().createObjectNode()
                        .put("Target", consulService)
                        .put("ClientCertURI", spiffeId)
                        .put("ClientCertSerial", serialNumber)), ContentType.APPLICATION_JSON))
                .build();
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(request)) {
            return EntityUtils.toString(response.getEntity());
        }
    }
}
