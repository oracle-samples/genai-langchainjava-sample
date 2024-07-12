# Oracle OCI Generative AI LangChain For Java version 1.0.
#
# Copyright (c)  2024,  Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

# 1st stage, build the app
FROM container-registry.oracle.com/java/openjdk:21 as build

# Install maven
WORKDIR /usr/share
RUN set -x && \
    curl -O https://archive.apache.org/dist/maven/maven-3/3.8.4/binaries/apache-maven-3.8.4-bin.tar.gz && \
    tar -xvf apache-maven-*-bin.tar.gz  && \
    rm apache-maven-*-bin.tar.gz && \
    mv apache-maven-* maven && \
    ln -s /usr/share/maven/bin/mvn /bin/

WORKDIR /helidon

# Create a first layer to cache the "Maven World" in the local repository.
# Incremental docker builds will always resume after that, unless you update
# the pom
ADD pom.xml .
RUN mvn package -Dmaven.test.skip -Declipselink.weave.skip -Declipselink.weave.skip -DskipOpenApiGenerate

# Do the Maven build!
# Incremental docker builds will resume here when you change sources
ADD src src
RUN mvn package -DskipTests

RUN echo "done!"

# 2nd stage, build the runtime image
FROM container-registry.oracle.com/java/openjdk:21
WORKDIR /helidon

# Copy the binary built in the 1st stage
COPY --from=build /helidon/target/langchain4java.jar ./
COPY --from=build /helidon/target/libs ./libs
COPY config/* /root/.oci/
CMD ["java", "-jar", "langchain4java.jar"]

EXPOSE 8080