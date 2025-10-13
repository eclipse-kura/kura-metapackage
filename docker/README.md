# Kura Container build

The Kura container is a Docker image that provides a runtime environment for Kura. This document describes how to build the Kura container and run it. Kura containers come in two flavors:
- a core container that contains only the `kura-core` package.
- a full container that includes a full Kura installation

## Build the core container

Replace `<platform>` with the desired platform (`linux/amd64` or `linux/arm64`), and run the following command:

```bash
docker build \
    -f Dockerfile.debian \
    --platform <platform> \
    --target core-runtime \
    -t kura-core .
```

Supported platforms:
- `linux/amd64`
- `linux/arm64`

## Build the full container

Replace `<platform>` with the desired platform (`linux/amd64` or `linux/arm64`), and run the following command:

```bash
docker build \
    -f Dockerfile.debian \
    --platform <platform> \
    -t kura .
```

Supported platforms:
- `linux/amd64`
- `linux/arm64`

## Build arguments

- `RELEASE_BUILD`(optional):
    - If set to `1`, the build will use the latest available **release** version of the artifacts (i.e. it will look for the latest packages in the `kura-deb` repository and in the `kura-release` Maven repository).
    - If not set, the build will use the latest available **snapshot** version of the artifacts (i.e. it will look for the latest packages in the `kura-develop-deb`/`kura-develop-rpm` repositories and in the `kura-snapshot` Maven repository).

## Run the container

```
docker run -p 443:443 -it kura
```

## Container customisation

The Kura container can be customised by creating a new Dockerfile that extends the base Kura container image. We recommend using the **core** image as a base for your customisations but the same approach can be applied to the **full** image. Here is an example of how to create a custom Dockerfile.

In a new directory (`workdir` in this example), we have a couple of packages that we want to install in the Kura container.

```bash
ls workdir/
kura-my-package.deb
kura-my-other-package.deb
```

Let's say we built the Kura core container image as described above and tagged it as `kura-core`. Now we can create a new Dockerfile in the same directory as the `workdir` directory with the following content:

```Dockerfile
FROM kura-core

# Bind mount the host workdir directory to /tmp/packages in the container
RUN --mount=type=bind,source=/workdir,target=/tmp/packages \
# Install Kura packages \
    apt update && \
    find /tmp/packages -name "*.deb" -print0 | xargs -0 apt install --no-install-recommends -y && \
# Clean up \
    rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["/opt/eclipse/kura/bin/start_kura.sh"]
```

Now we can build the custom container image by running the following command in the same directory as the Dockerfile:

```bash
docker build \
    -t kura-custom .
```
