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

import javax.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;

public class CertificateAuthenticator
        implements Authenticator
{
    private static final String X509_ATTRIBUTE = "javax.servlet.request.X509Certificate";
    private static final String SPIFFE_PREFIX = "spiffe://";

    @Override
    public Principal authenticate(HttpServletRequest request)
            throws AuthenticationException
    {
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute(X509_ATTRIBUTE);
        if ((certs == null) || (certs.length == 0)) {
            throw new AuthenticationException(null);
        }
        //System.out.println("Certs: " + certs.length);
        System.out.println("Principal : " + certs[0].getSubjectX500Principal());
        //System.out.println("Cert2 : " + certs[1].getSubjectX500Principal());
        //System.out.println("Cert 1: " + certs[0].toString());
        //System.out.println("Cert 2: " + certs[1].toString());
        System.out.println("Serial Number: " + certs[0].getSerialNumber());

        try {
            //System.out.println("URI: " + certs[1].getSubjectAlternativeNames().size());
            System.out.println("URI: " + certs[0].getSubjectAlternativeNames().stream().findFirst().get().toString());
            //System.out.println("CertificateAuthenticator: " + certs[0].getExtendedKeyUsage().get(1).toString());
        } catch (CertificateParsingException e) {
            e.printStackTrace();
        }

        return certs[0].getSubjectX500Principal();
    }
}
