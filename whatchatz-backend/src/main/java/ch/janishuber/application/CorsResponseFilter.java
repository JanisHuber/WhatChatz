package ch.janishuber.application;

import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
@PreMatching
public class CorsResponseFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        if ("OPTIONS".equals(request.getMethod())) {
            request.abortWith(Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .build());
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        if (!"OPTIONS".equals(requestContext.getMethod())) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
            responseContext.getHeaders().add("Access-Control-Allow-Headers",
                    "origin, content-type, accept, authorization");
            responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
            responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        }
    }
}
