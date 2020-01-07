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
package io.prestosql.plugin.certificate.plaintls;

import io.airlift.log.Logger;
import io.prestosql.spi.security.AccessDeniedException;
import io.prestosql.spi.security.CertificateAuthenticator;

import javax.inject.Inject;

import java.security.Principal;
import java.security.cert.X509Certificate;

public class PlainTlsAuthenticator
        implements CertificateAuthenticator
{
    private static final Logger log = Logger.get(PlainTlsAuthenticator.class);

    private final boolean tlsEnabled;

    @Inject
    public PlainTlsAuthenticator(PlainTlsConfig serverConfig)
    {
        this.tlsEnabled = serverConfig.getTlsEnabled();
    }

    @Override
    public Principal authenticate(X509Certificate[] certs) throws AccessDeniedException
    {
        if (this.tlsEnabled) {
            log.debug("Authenticated Principal: " + certs[0].getSubjectX500Principal());
            return certs[0].getSubjectX500Principal();
        }
        else {
            throw new AccessDeniedException("TLS is not enabled!!");
        }
    }
}
