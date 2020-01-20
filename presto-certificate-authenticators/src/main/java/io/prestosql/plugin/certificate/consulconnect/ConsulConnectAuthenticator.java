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

import io.airlift.log.Logger;
import io.prestosql.spi.security.AccessDeniedException;
import io.prestosql.spi.security.CertificateAuthenticator;

import javax.inject.Inject;

import java.security.Principal;
import java.security.cert.X509Certificate;

public class ConsulConnectAuthenticator
        implements CertificateAuthenticator
{
    private static final Logger log = Logger.get(ConsulConnectAuthenticator.class);
    private static final String SPIFFE_PREFIX = "spiffe";

    private final boolean tlsEnabled;

    @Inject
    public ConsulConnectAuthenticator(ConsulConnectConfig serverConfig)
    {
        this.tlsEnabled = serverConfig.getTlsEnabled();
    }

    @Override
    public Principal authenticate(X509Certificate[] certs) throws AccessDeniedException
    {
        log.info("*** ConsulConnectAuthenticator...Authenticate *** " + this.tlsEnabled + " principal: " + certs[0].getSubjectX500Principal());
        String serialNumber = String.valueOf(certs[0].getSerialNumber());
        System.out.println("*** Serial Number: " + serialNumber);
        String cert = certs[0].toString().trim();
        String spiffeId = cert.substring(cert.indexOf(SPIFFE_PREFIX), cert.indexOf("svc/")) + certs[0].getSubjectX500Principal().toString().split("=")[1];
        System.out.println("Spiffe Id: " + spiffeId);

        return certs[0].getSubjectX500Principal();
    }
}
