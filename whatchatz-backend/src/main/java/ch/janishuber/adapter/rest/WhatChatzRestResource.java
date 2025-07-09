package ch.janishuber.adapter.rest;

import ch.janishuber.adapter.dto.ContactRequest;
import ch.janishuber.adapter.dto.UserCreationRequest;
import ch.janishuber.adapter.helpers.AuthHelper;
import ch.janishuber.adapter.persistence.ContactRepository;
import ch.janishuber.adapter.persistence.MessageRepository;
import ch.janishuber.adapter.persistence.UserRepository;
import ch.janishuber.domain.Contact;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;

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
    public Response saveUser(@Context HttpHeaders headers, UserCreationRequest userCreationRequest) {
        try {
            String uid = helpers.verifyAndExtractUid(headers);
            userRepository.save(uid, userCreationRequest.name(), userCreationRequest.info());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized: " + e.getMessage()).build();
        }

        return Response.ok("User saved successfully").build();
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
    public Response saveContact(@Context HttpHeaders headers, ContactRequest contactRequest) {
        try {
            String uid = helpers.verifyAndExtractUid(headers);
            Contact contact = new Contact(0L, uid, contactRequest.contactId(), contactRequest.contactName(),
                    LocalDateTime.now());
            contactRepository.save(contact);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized: " + e.getMessage()).build();
        }

        return Response.ok("Contact saved successfully").build();
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