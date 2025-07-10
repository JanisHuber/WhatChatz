package ch.janishuber.adapter.rest;

import ch.janishuber.adapter.dto.ContactRequest;
import ch.janishuber.adapter.dto.UserCreationRequest;
import ch.janishuber.adapter.helpers.AuthHelper;
import ch.janishuber.adapter.persistence.ContactRepository;
import ch.janishuber.adapter.persistence.MessageRepository;
import ch.janishuber.adapter.persistence.UserRepository;
import ch.janishuber.domain.Contact;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.Map;

@Path("/api")
public class WhatChatzRestResource {
    @Inject
    private AuthHelper helpers;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ContactRepository contactRepository;
    @Inject
    private MessageRepository messageRepository;

    @POST
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveUser(@Context HttpHeaders headers, UserCreationRequest userCreationRequest) {
        try {
            String uid = helpers.verifyAndExtractUid(headers);
            try {
                userRepository.save(uid, userCreationRequest.name(), userCreationRequest.info());
            } catch (IllegalStateException e) {
                System.out.println("returning bad request");
                return Response.status(Response.Status.BAD_REQUEST).entity("User creation failed: " + e.getMessage()).build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized: " + e.getMessage()).build();
        }

        Map<String, String> result = Map.of("message", "User saved successfully");
        return Response
                .status(Response.Status.CREATED)
                .entity(result)
                .build();
    }

    @GET
    @Path("/contacts")
    public Response getAllContacts(@Context HttpHeaders headers) {
        try {
            String uid = helpers.verifyAndExtractUid(headers);
            return Response.ok(contactRepository.getAllContactsFor(uid)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized: " + e.getMessage()).build();
        }
    }

    @POST
    @Path("/contacts/new")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveContact(@Context HttpHeaders headers, ContactRequest contactRequest) {
        try {
            String uid = helpers.verifyAndExtractUid(headers);
            Contact contact = new Contact(0L, uid, contactRequest.contactId(), contactRequest.contactName(), LocalDateTime.now());
            contactRepository.save(contact);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized: " + e.getMessage()).build();
        }

        Map<String, String> result = Map.of("message", "Contact saved successfully");
        return Response
                .status(Response.Status.CREATED)
                .entity(result)
                .build();
    }

    @GET
    @Path("/users")
    public Response getUser(@QueryParam("queryName") String queryName) {
        if (queryName == null || queryName.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Query name cannot be null or empty").build();
        }

        return Response.ok(userRepository.findByName(queryName)).build();
    }

    @GET
    @Path("/users/{uid}")
    public Response getUserByUid(@PathParam("uid") String uid) {
        if (uid == null || uid.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User ID cannot be null or empty").build();
        }

        var user = userRepository.findByUid(uid);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }

        return Response.ok(user).build();
    }

    @GET
    @Path("/chats/{chatId}/messages")
    public Response getAllMessagesForChat(@PathParam("chatId") String chatId, @Context HttpHeaders headers) {
        try {
            String uid = helpers.verifyAndExtractUid(headers);
            return Response.ok(messageRepository.getAllMessagesForChat(chatId)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized: " + e.getMessage()).build();
        }
    }
}