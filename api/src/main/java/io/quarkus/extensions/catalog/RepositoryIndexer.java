package io.quarkus.extensions.catalog;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.quarkus.extensions.catalog.model.Extension;
import io.quarkus.extensions.catalog.model.Platform;
import io.quarkus.extensions.catalog.model.Release;
import io.quarkus.extensions.catalog.model.Repository;
import io.quarkus.extensions.catalog.spi.IndexVisitor;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import io.quarkus.platform.descriptor.loader.json.impl.QuarkusJsonPlatformDescriptor;

/**
 * Indexes a repository
 */
public class RepositoryIndexer {

    private final ObjectReader objectReader;

    @SuppressWarnings("deprecation")
    public RepositoryIndexer(ObjectMapper mapper) {
        this.objectReader = mapper.reader()
                .withFeatures(JsonParser.Feature.ALLOW_COMMENTS, JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS)
                .with(mapper.getDeserializationConfig().with(PropertyNamingStrategy.KEBAB_CASE));
    }

    public void index(Repository repository, IndexVisitor visitor) throws IOException {
        // Index Platforms
        for (Platform platform : repository.getPlatforms()) {
            for (Release release : platform.getReleases()) {
                visitor.visitPlatform(readPlatformDescriptor(platform, release));
            }
        }

        // Index extensions
        for (Extension extension : repository.getIndividualExtensions()) {
            for (Release release : extension.getReleases()) {
                // TODO: Grab the quarkus-extension.yaml from the extension's jar
                io.quarkus.dependencies.Extension ext = new io.quarkus.dependencies.Extension(extension.getGroupId(), extension.getArtifactId(), release.getVersion());
                visitor.visitExtension(ext, release.getQuarkusCore());
            }
        }
    }


    QuarkusPlatformDescriptor readPlatformDescriptor(Platform platform, Release release) throws IOException {
        // TODO: Use Maven API to resolve JSON?
        URL url = getPlatformJSONURL(platform, release);
        return objectReader.forType(QuarkusJsonPlatformDescriptor.class).readValue(url);
//        QuarkusJsonPlatformDescriptorLoaderImpl loader = new QuarkusJsonPlatformDescriptorLoaderImpl();
//        return loader.load(new QuarkusJsonPlatformDescriptorLoaderContext(null) {
//            @Override
//            public <T> T parseJson(Function<InputStream, T> parser) {
//                try {
//                    return parser.apply(new URL(url).openStream());
//                } catch (IOException e) {
//                    throw new UncheckedIOException(e);
//                }
//            }
//        });
    }

    static URL getPlatformJSONURL(Platform platform, Release release) {
        try {
            return new URL(MessageFormat.format("https://repo1.maven.org/maven2/{0}/{1}/{2}/{1}-{2}.json",
                                                platform.getGroupIdJson().replace('.', '/'),
                                                platform.getArtifactIdJson(),
                                                release.getVersion()));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Error while building JSON URL", e);
        }
    }
}