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
package io.prestosql.plugin.jdbc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import io.prestosql.spi.connector.ColumnHandle;
import io.prestosql.spi.connector.ConnectorTableHandle;
import io.prestosql.spi.connector.SchemaTableName;
import io.prestosql.spi.predicate.TupleDomain;

import javax.annotation.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;

import static java.util.Objects.requireNonNull;

public final class JdbcTableHandle
        implements ConnectorTableHandle
{
    private final SchemaTableName schemaTableName;

    // catalog, schema and table names are reported by the remote database
    private final String catalogName;
    private final String schemaName;
    private final String tableName;
    private final Optional<List<JdbcColumnHandle>> columns;
    private final TupleDomain<ColumnHandle> constraint;
    private final OptionalLong limit;

    public JdbcTableHandle(SchemaTableName schemaTableName, @Nullable String catalogName, @Nullable String schemaName, String tableName)
    {
        this(schemaTableName, catalogName, schemaName, tableName, Optional.empty(), TupleDomain.all(), OptionalLong.empty());
    }

    @JsonCreator
    public JdbcTableHandle(
            @JsonProperty("schemaTableName") SchemaTableName schemaTableName,
            @JsonProperty("catalogName") @Nullable String catalogName,
            @JsonProperty("schemaName") @Nullable String schemaName,
            @JsonProperty("tableName") String tableName,
            @JsonProperty("columns") Optional<List<JdbcColumnHandle>> columns,
            @JsonProperty("constraint") TupleDomain<ColumnHandle> constraint,
            @JsonProperty("limit") OptionalLong limit)
    {
        this.schemaTableName = requireNonNull(schemaTableName, "schemaTableName is null");
        this.catalogName = catalogName;
        this.schemaName = schemaName;
        this.tableName = requireNonNull(tableName, "tableName is null");
        requireNonNull(columns, "columns is null");
        this.columns = columns.map(ImmutableList::copyOf);
        this.constraint = requireNonNull(constraint, "constraint is null");
        this.limit = requireNonNull(limit, "limit is null");
    }

    @JsonProperty
    public SchemaTableName getSchemaTableName()
    {
        return schemaTableName;
    }

    @JsonProperty
    @Nullable
    public String getCatalogName()
    {
        return catalogName;
    }

    @JsonProperty
    @Nullable
    public String getSchemaName()
    {
        return schemaName;
    }

    @JsonProperty
    public String getTableName()
    {
        return tableName;
    }

    @JsonProperty
    public Optional<List<JdbcColumnHandle>> getColumns()
    {
        return columns;
    }

    @JsonProperty
    public TupleDomain<ColumnHandle> getConstraint()
    {
        return constraint;
    }

    @JsonProperty
    public OptionalLong getLimit()
    {
        return limit;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        JdbcTableHandle o = (JdbcTableHandle) obj;
        return Objects.equals(this.schemaTableName, o.schemaTableName) &&
                Objects.equals(this.columns, o.columns) &&
                Objects.equals(this.constraint, o.constraint) &&
                Objects.equals(this.limit, o.limit);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(schemaTableName, columns, constraint, limit);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(schemaTableName).append(" ");
        Joiner.on(".").skipNulls().appendTo(builder, catalogName, schemaName, tableName);
        columns.ifPresent(value -> builder.append(" columns=").append(value));
        limit.ifPresent(value -> builder.append(" limit=").append(value));
        return builder.toString();
    }
}
