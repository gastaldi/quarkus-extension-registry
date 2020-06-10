package io.quarkus.registry.catalog.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.quarkus.registry.model.Release;
import org.immutables.value.Value;

/**
 * A {@link Platform} holds a set of extensions
 */
@Value.Immutable
@JsonDeserialize(builder = PlatformBuilder.class)
public interface Platform {

    @JsonProperty("group-id")
    String getGroupId();

    @Value.Default
    @JsonProperty("group-id-json")
    @Value.Auxiliary
    default String getGroupIdJson() {
        return getGroupId();
    }

    @JsonProperty("artifact-id")
    String getArtifactId();

    @Value.Default
    @JsonProperty("artifact-id-json")
    @Value.Auxiliary
    default String getArtifactIdJson() {
        return getArtifactId();
    }

    @Value.Auxiliary
    List<Release> getReleases();

    static PlatformBuilder builder() {
        return new PlatformBuilder();
    }
}
