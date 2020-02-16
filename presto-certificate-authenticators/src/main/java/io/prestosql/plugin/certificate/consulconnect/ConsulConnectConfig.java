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

import io.airlift.configuration.Config;
import io.airlift.configuration.ConfigDescription;

import javax.validation.constraints.NotNull;

public class ConsulConnectConfig
{
    private String consulAddr = "http://127.0.0.1:8500";
    private String consulToken = "";
    private String consulService;

    @NotNull
    public String getConsulService()
    {
        return this.consulService;
    }

    @Config("consul.service")
    @ConfigDescription("Consul service for this presto instance")
    public void setConsulService(String consulService)
    {
        this.consulService = consulService;
    }

    @NotNull
    public String getConsulAddr()
    {
        return this.consulAddr;
    }

    @Config("consul.addr")
    @ConfigDescription("Consul address")
    public void setConsulAddr(String consulAddr)
    {
        this.consulAddr = consulAddr;
    }

    public String getConsulToken()
    {
        return this.consulToken;
    }

    @Config("consul.token")
    @ConfigDescription("Consul ACL token")
    public void setConsulToken(String consulToken)
    {
        this.consulToken = consulToken;
    }
}
