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
package io.prestosql.server.security;

import io.airlift.log.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.security.cert.X509Certificate;

import static io.prestosql.server.security.UserExtraction.createUserExtraction;
import static java.util.Objects.requireNonNull;

public class CertificateAuthenticator
        implements Authenticator
{
    private static final String X509_ATTRIBUTE = "javax.servlet.request.X509Certificate";
    private static final Logger log = Logger.get(CertificateAuthenticator.class);

    private final CertificateAuthenticatorManager authenticatorManager;
    private final UserExtraction userExtraction;

    @Inject
    public CertificateAuthenticator(CertificateAuthenticatorManager authenticatorManager, CertificateConfig config)
    {
        requireNonNull(config, "config is null");
        this.userExtraction = createUserExtraction(config.getUserExtractionPattern(), config.getUserExtractionFile());
        this.authenticatorManager = requireNonNull(authenticatorManager, "authenticatorManager is null");
        authenticatorManager.setRequired();
    }

    @Override
    public AuthenticatedPrincipal authenticate(HttpServletRequest request)
            throws AuthenticationException
    {
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute(X509_ATTRIBUTE);
        if ((certs == null) || (certs.length == 0)) {
            throw new AuthenticationException(null);
        }

        try {
            Principal principal = authenticatorManager.getAuthenticator().authenticate(certs);
            String authenticatedUser = userExtraction.extractUser(principal.toString());
            return new AuthenticatedPrincipal(authenticatedUser, principal);
        }
        catch (UserExtractionException e) {
            throw new AuthenticationException(e.getMessage());
        }
        catch (RuntimeException e) {
            log.debug("CertificateAuthenticator plugin hasn't been loaded yet...");
            return null;
        }
    }
}
