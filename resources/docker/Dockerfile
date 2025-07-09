FROM docker:dind

# Disable Docker TLS (otherwise DinD expects certs)
ENV DOCKER_TLS_CERTDIR=""
# Add daemon.json to allow insecure registry
RUN mkdir -p /etc/docker 
RUN echo '{ "insecure-registries": ["nexus-nexus-repository-manager:5000"] }' > /etc/docker/daemon.json

# Optional: Expose Docker's default port if needed externally
EXPOSE 2375

# Entrypoint remains the default
CMD ["dockerd-entrypoint.sh"]
